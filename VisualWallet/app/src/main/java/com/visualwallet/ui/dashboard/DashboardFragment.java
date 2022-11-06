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

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private EditText editTextKey;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        editTextKey = root.findViewById(R.id.editTextKey);

        Button transBtn = root.findViewById(R.id.button_trans);
        transBtn.setOnClickListener(view_->{
            toastInfo("暂未开放");
        });

        return root;
    }

    private void toastInfo(String str) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    private void transfer() {

    }

    private void test() {
        Log.i("transfer", "click");
        String key = editTextKey.getText().toString();

        ECKey ecKey = new ECKey();
        Log.i("genkey", "sk " + ecKey.getPrivateKeyAsHex());
        Log.i("genkey", "pk " + ecKey.getPublicKeyAsHex());

//            if (!key.equals(Constant.privateKey0)) {
//                Wallet wallet = BitcoinClient.generateBitcoinWallet();
//                toastInfo(wallet.toString());
////                Log.i("transfer", "key error");
////                toastInfo("私钥错误");
//            } else {
//                Log.i("transfer", "key correct");
//                new TransRequest().setNetCallback(res -> {
//                    Log.i("transfer", "got res");
//
//                    if (res != null) {
//                        Integer resFlag = (Integer) res.get("flag");
//                        String resContent = (String) res.get("content");
//                        if (resFlag != null && resContent != null) {
//                            // 获取到了正确的返回信息
//                            resContent = resContent.replace("\\", "");
//                            if (Looper.myLooper() == null) {
//                                Looper.prepare();
//                            }
//                            new AlertDialog.Builder(getContext())
//                                    .setTitle("转账成功")
//                                    .setMessage(
//                                            "bcrt1ql5jtgq74u5kr20hltj365sdz2vzqfsvf5gwplk\n" +
//                                            "向\n" +
//                                            "bcrt1ql5jtgq74u5kr20hltj365sdz2vzqfsvf5gwplk\n" +
//                                            "转账 10 BTC 成功")
//                                    .setPositiveButton("确定", null)
//                                    .show();
//                            Looper.loop();
//                            return;
//                        }
//                    }
//                    toastInfo("转账异常");
//                }).start();
//                toastInfo("转账请求已发出，等待区块链确认");
//            }
    }

}