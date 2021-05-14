package com.example.visualwallet.ui.home;

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

import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private Button transBtn;
    private EditText editTextKey;
    private String key = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        editTextKey = root.findViewById(R.id.editTextKey);
        key = editTextKey.getText().toString();

        transBtn = root.findViewById(R.id.button_trans);
        transBtn.setOnClickListener(view_->{
            TransRequest transRequest = new TransRequest(Constant.fromAddr, Constant.toAddr, Constant.value);
            transRequest.setNetCallback(new NetCallback() {
                @Override
                public void callBack(@Nullable @org.jetbrains.annotations.Nullable Map res) {
                }
            });
            if (key != null) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), "转账成功", Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }
}