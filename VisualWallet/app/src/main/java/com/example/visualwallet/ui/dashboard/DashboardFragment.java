package com.example.visualwallet.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.visualwallet.R;
import com.example.visualwallet.common.Constant;
import com.example.visualwallet.net.NetCallback;
import com.example.visualwallet.net.TransRequest;
import com.example.visualwallet.ui.home.HomeViewModel;

import java.util.Map;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    private Button transBtn;
    private EditText editTextKey;
    private String key = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        editTextKey = root.findViewById(R.id.editTextKey);
        key = editTextKey.getText().toString();

        transBtn = root.findViewById(R.id.button_trans);
        transBtn.setOnClickListener(view_->{
            TransRequest transRequest = new TransRequest(Constant.fromAddr, Constant.toAddr, Constant.value);
            transRequest.setNetCallback(new NetCallback() {
                @Override
                public void callBack(@Nullable @org.jetbrains.annotations.Nullable Map res) {
                    if (res != null && res.get("code") != null && ((String)res.get("code")).equals("200")) {
                        Toast.makeText(getActivity(), "转账请求已发出，等待区块链确认", Toast.LENGTH_LONG).show();
                    }
                    else if (res != null && res.get("code") != null) {
                        Toast.makeText(getActivity(), "转账异常，错误" + ((String)res.get("code")), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        return root;
    }
}