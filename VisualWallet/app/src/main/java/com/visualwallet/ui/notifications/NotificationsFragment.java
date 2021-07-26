package com.visualwallet.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.visualwallet.Account;
import com.visualwallet.AddNewTag;
import com.visualwallet.R;
import com.visualwallet.common.Constant;
import com.visualwallet.data.WalletQuery;
import com.visualwallet.entity.Wallet;

public class NotificationsFragment extends Fragment {

    private LinearLayout accountLL;
    private ImageButton wallet_add;
    private Switch modeSwitch;

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
        modeSwitch = view.findViewById(R.id.switch1);
        modeSwitch.setChecked(Constant.appMode == 1);
        modeSwitch.setOnCheckedChangeListener(this::onCheckChanged);
        refreshScrollView();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onResume() {
        super.onResume();
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

    private void addCtrl(Wallet w) {
        Button newBtn = new Button(getActivity());
        newBtn.setText(String.format("%s\t\t\t\t\t%s", w.getWalName(), w.getCurType()));
        newBtn.setTextSize(20);
        newBtn.setTextColor(Color.WHITE);
        newBtn.setBackgroundResource(R.drawable.button_account);
        newBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Account.class);
            intent.putExtra(Constant.WALLET_ARG, w);
            startActivityForResult(intent, Constant.REQUEST_DEL_ACC);
        });
        newBtn.setWidth(960);
        newBtn.setElevation(5);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20,10,20,10);
        newBtn.setLayoutParams(layoutParams);
        accountLL.addView(newBtn);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onCheckChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.isPressed()) {
            if ((Constant.appMode == 1) != isChecked) {
                Constant.appMode = (isChecked ? 1 : 0);
                modeSwitch.setText(isChecked ? "在线模式" : "本地模式");
                WalletQuery.initPrefName();
                refreshScrollView();
            }
        }
    }
}
