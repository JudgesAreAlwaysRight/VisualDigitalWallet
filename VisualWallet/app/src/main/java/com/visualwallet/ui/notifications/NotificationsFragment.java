package com.visualwallet.ui.notifications;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.visualwallet.common.GlobalVariable;
import com.visualwallet.data.AppModeUtil;
import com.visualwallet.entity.VisualWallet;
import com.visualwallet.ui.Account;
import com.visualwallet.ui.AddNewTag;
import com.visualwallet.R;
import com.visualwallet.common.Constant;
import com.visualwallet.data.DataUtil;
import com.visualwallet.data.WalletQuery;

import java.io.IOException;
import java.util.Objects;

public class NotificationsFragment extends Fragment {

    private LinearLayout accountLL;
    private ImageButton wallet_add;
    private Switch modeSwitch;
    private String onlineMode;
    private String offlineMode;

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
        onlineMode = getResources().getString(R.string.wallet_list_title_mode_online);
        offlineMode = getResources().getString(R.string.wallet_list_title_mode_offline);
        modeSwitch = view.findViewById(R.id.switch1);
        modeSwitch.setChecked(GlobalVariable.appMode == 1);
        modeSwitch.setText(GlobalVariable.appMode == 1 ? onlineMode : offlineMode);
        modeSwitch.setOnCheckedChangeListener(this::onCheckChanged);

        WalletQuery.initPrefName();
        refreshScrollView();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onResume() {
        super.onResume();
        modeSwitch.setChecked(GlobalVariable.appMode == 1);
        modeSwitch.setText(GlobalVariable.appMode == 1 ? onlineMode : offlineMode);
        refreshScrollView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void refreshScrollView() {
        // 清空
        accountLL.removeAllViews();
        // 读取数据并显示到scroll view
        WalletQuery query = new WalletQuery(getActivity());
        VisualWallet[] visualWallets = query.getWallets();
        for (VisualWallet w : visualWallets) {
            if (w != null) {
                addCtrl(w);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addCtrl(VisualWallet w) {
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
                ClipData mClipData = ClipData.newPlainText("Keycrux account sequence code", accStr);
                cm.setPrimaryClip(mClipData);
                Toast.makeText(getContext(), getResources().getString(R.string.export_success), Toast.LENGTH_LONG).show();
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
            if ((GlobalVariable.appMode == 1) != isChecked) {
                GlobalVariable.appMode = (isChecked ? 1 : 0);
                AppModeUtil.setAppMode(requireActivity(), GlobalVariable.appMode);
                modeSwitch.setText(GlobalVariable.appMode == 1 ? onlineMode : offlineMode);
                WalletQuery.initPrefName();
                refreshScrollView();
            }
        }
    }
}
