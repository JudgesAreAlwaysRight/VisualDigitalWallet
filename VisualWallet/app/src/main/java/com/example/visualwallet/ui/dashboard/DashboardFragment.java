package com.example.visualwallet.ui.dashboard;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.blockcypher.context.BlockCypherContext;
import com.blockcypher.exception.BlockCypherException;
import com.blockcypher.model.transaction.Transaction;
import com.blockcypher.model.transaction.intermediary.IntermediaryTransaction;
import com.blockcypher.utils.gson.GsonFactory;
import com.blockcypher.utils.sign.SignUtils;
import com.example.visualwallet.R;
import com.example.visualwallet.common.Constant;

import java.util.ArrayList;
import java.util.Collections;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        EditText editTextKey = root.findViewById(R.id.editTextKey);

        Button transBtn = root.findViewById(R.id.button_trans);
        transBtn.setOnClickListener(view_ -> {
            Log.i("transfer", "click");
            String toast_info = "转账异常";
            String key = editTextKey.getText().toString();

            try {

                if (!key.equals(Constant.privateKey1)) {
                    Log.i("transfer", "key error");
                    toast_info = "私钥错误";
                } else {
                    Log.i("transfer", "key correct");
                    BlockCypherContext context = new BlockCypherContext("v1", "btc", "test3", "YOURTOKEN");
                    // 下面这句应当发送一个post并返回201状态，但是现在会没有任何Exception情况下直接进finally块
//                    IntermediaryTransaction unsignedTx = context.getTransactionService()
//                            .newTransaction(
//                                    new ArrayList<>(Collections.singletonList(Constant.address1)),
//                                    new ArrayList<>(Collections.singletonList(Constant.address2)),
//                                    80000
//                            );
//                    Log.i("transfer", "sign tx");
//                    SignUtils.signWithHexKeyWithPubKey(unsignedTx, Constant.privateKey1);
//                    Transaction tx = context.getTransactionService().sendTransaction(unsignedTx);
//                    Log.i("transfer", "Sent transaction:");
//                    Log.i("transfer demo", GsonFactory.getGsonPrettyPrint().toJson(tx));

                    toast_info = "转账请求已发出，等待区块链确认";
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                Toast.makeText(getActivity(), toast_info, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        });

        return root;
    }
}