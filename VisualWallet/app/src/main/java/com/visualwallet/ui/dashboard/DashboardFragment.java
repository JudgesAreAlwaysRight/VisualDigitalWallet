package com.visualwallet.ui.dashboard;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.visualwallet.R;
import com.visualwallet.bitcoin.BitcoinClient;
import com.visualwallet.common.Constant;
import com.visualwallet.net.TransRequest;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.wallet.Wallet;

import java.util.Objects;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private EditText editTextKey;
    private EditText editTextAddress;
    private EditText editTextValue;
    private EditText editTextFee;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        editTextAddress = root.findViewById(R.id.editTextAddress);
        editTextKey = root.findViewById(R.id.editTextKey);
        editTextValue = root.findViewById(R.id.editTextValue);
        editTextFee = root.findViewById(R.id.editTextFee);

        Button transBtn = root.findViewById(R.id.button_trans);
        transBtn.setOnClickListener(view_-> lockedEntry());
//        transBtn.setOnClickListener(view_-> transfer());

        return root;
    }

    private void lockedEntry() {
        Log.i("lockedEntry", "an operate was muted");
        toastInfo("暂未开放");
    }

    private void transfer() {
        Log.i("transfer", "click");

        String toAddress = editTextAddress.getText().toString();
        String key = editTextKey.getText().toString();
        String value = editTextValue.getText().toString();
        String fee = editTextFee.getText().toString();

        if (key.length() != 51 && key.length() != 52) {
            toastInfo("私钥格式错误");
            return;
        }

        new TransRequest(toAddress, key, value, fee, "public").setNetCallback(res -> {
            Log.i("transfer", "got res");

            if (res != null) {
                Integer resFlag = (Integer) res.get("flag");
                String resContent = (String) res.get("content");
                if (resFlag != null && resContent != null) {
                    // 获取到了正确的返回信息
                    resContent = resContent.replace("\\", "");
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    new AlertDialog.Builder(requireContext())
                            .setTitle("转账成功")
                            .setMessage("向\n" + toAddress + "\n转账 10 BTC 成功")
                            .setPositiveButton("确定", null)
                            .show();
                    Looper.loop();
                    return;
                }
            }
            toastInfo("转账异常");
        }).start();
        toastInfo("转账请求已发出，等待区块链确认");
    }

    private void toastInfo(String str) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
        Looper.loop();
    }

}