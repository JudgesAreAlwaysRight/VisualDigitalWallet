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

import com.example.visualwallet.Account;
import com.example.visualwallet.AddNewTag;
import com.example.visualwallet.R;
import com.example.visualwallet.common.Constant;
import com.example.visualwallet.data.WalletQuery;
import com.example.visualwallet.entity.Wallet;

public class NotificationsFragment extends Fragment {

    private LinearLayout accountLL;
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
            startActivityForResult(intent, Constant.REQUEST_ADD_ACC);
        });
        refreshScrollView();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constant.REQUEST_ADD_ACC) {
            if (data == null) return; // 用户直接返回了
            Wallet w = (Wallet) data.getSerializableExtra("wallet");

            Log.i("add", w.getAddress());
            Log.i("type", w.getCurType());
            Log.i("name", w.getWalName());
            Log.i("k", String.valueOf(w.getCoeK()));
            Log.i("n", String.valueOf(w.getCoeN()));

            addCtrl(w);
        } else if (requestCode == Constant.REQUEST_DEL_ACC) {
            // TODO: 删除账户
        }
        refreshScrollView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void refreshScrollView() {
        // 清空
        accountLL.removeAllViews();
        // 读取数据并显示到scroll view
        WalletQuery query = new WalletQuery(getActivity());
        Wallet[] wallets = query.getWallets();
        for (Wallet w : wallets) {
            if (w != null) {
                addCtrl(w);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void addCtrl(Wallet w) {
        Button newBtn = new Button(getActivity());
        newBtn.setText(String.format("%s - %s (%d/%d)\n地址：%s",
                w.getWalName(), w.getCurType(), w.getCoeK(), w.getCoeN(), w.getAddress()));
        newBtn.setTextSize(20);
        newBtn.setBackgroundResource(R.drawable.buttom_press);
        newBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Account.class);
            intent.putExtra(Constant.WALLET_ARG, w);
            startActivityForResult(intent, Constant.REQUEST_DEL_ACC);
        });
        accountLL.addView(newBtn);
    }
}
