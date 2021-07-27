package com.visualwallet.ui.notifications;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.visualwallet.Account;
import com.visualwallet.AddNewTag;
import com.visualwallet.Collect;
import com.visualwallet.MainActivity;
import com.visualwallet.R;
import com.visualwallet.common.Constant;
import com.visualwallet.data.DataUtil;
import com.visualwallet.data.WalletQuery;
import com.visualwallet.entity.Wallet;

import java.io.IOException;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addCtrl(Wallet w) {
        View accView = View.inflate(getContext(), R.layout.layout_account_item, null);
        TextView nameView = accView.findViewById(R.id.accName);
        nameView.setText(w.getWalName());
        TextView typeView = accView.findViewById(R.id.accType);
        typeView.setText(w.getCurType());
        ImageButton delBtn = accView.findViewById(R.id.accDel);
        delBtn.setOnClickListener(v-> {
            WalletQuery query = new WalletQuery(getActivity());
            query.deleteWallet(w.getId());
            refreshScrollView();
        });
        accView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Account.class);
            intent.putExtra(Constant.WALLET_ARG, w);
            startActivityForResult(intent, Constant.REQUEST_DEL_ACC);
        });
        accView.setOnLongClickListener( v-> {
            String accStr = null;
            try {
                accStr = DataUtil.serialize(w);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (accStr != null) {
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Keycrux账户序列码", accStr);
                cm.setPrimaryClip(mClipData);
                Toast.makeText(getContext(), "账户序列码已复制到剪贴板", Toast.LENGTH_LONG).show();
                return true;
            }
            return false;
        });
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20,20,20,20);
        accView.setLayoutParams(layoutParams);
        accountLL.addView(accView);
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
