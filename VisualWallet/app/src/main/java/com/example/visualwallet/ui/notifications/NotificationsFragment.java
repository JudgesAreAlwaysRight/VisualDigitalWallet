package com.example.visualwallet.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.visualwallet.AddNewTag;
import com.example.visualwallet.R;
import com.example.visualwallet.data.WalletQuery;
import com.example.visualwallet.entity.Wallet;

public class NotificationsFragment extends Fragment {

    private LinearLayout accountLL;
    private Button wallet_btn1;
    private ImageButton wallet_add;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_notifications, null);
        accountLL = view.findViewById(R.id.accounts_linear);
        wallet_add = view.findViewById(R.id.wallet_button_add);
        wallet_add.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddNewTag.class);
            startActivity(intent);
        });
        // 读取数据并显示到scroll view
        WalletQuery query = new WalletQuery(getActivity());
        Wallet[] wallets = query.getWallets();
        for (Wallet w : wallets) {
            if (w != null) {
                addCtrl(w.getAddress(), w.getCurType(), w.getWalName(), w.getCoeK(), w.getCoeN());
            }
        }

        return view;
    }

    @SuppressLint("DefaultLocale")
    private void addCtrl(String address, String type, String name, int K, int N) {

        Button newBtn = new Button(getActivity());
        newBtn.setText(String.format("%s - %s (%d/%d)\n地址：%s", name, type, K, N, address));
        newBtn.setTextSize(20);
        newBtn.setBackgroundResource(R.drawable.buttom_press);
        newBtn.setOnClickListener(v -> {
            int pos = v.getVerticalScrollbarPosition();
            Log.i("scroll view", String.valueOf(pos));
        });
        accountLL.addView(newBtn);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        assert data != null;
        String add = data.getStringExtra("add");
        Log.i("add", add);
        String type = data.getStringExtra("type");
        Log.i("type", type);
        String name = data.getStringExtra("name");
        Log.i("name", name);
        int K = data.getIntExtra("K", 0);
        Log.i("k", String.valueOf(K));
        int N = data.getIntExtra("N", 0);
        Log.i("n", String.valueOf(N));
        //以上是bug
        //这里应该有个返回值啥的
        addCtrl(add, type, name, K, N);
    }
}