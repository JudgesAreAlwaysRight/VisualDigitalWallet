package com.example.vdw.ui.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.vdw.Account;

import com.example.vdw.R;

public class walletFragment extends Fragment {
    private TextView wallet_text;
    private Button wallet_btn1;
    private Button wallet_add;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.f_wallet, null);
        wallet_btn1 = (Button) view.findViewById(R.id.wallet_button1);
        wallet_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Account.class);
                //目标Activity
                startActivity(intent);
            }
        });
        return view;
    }

}