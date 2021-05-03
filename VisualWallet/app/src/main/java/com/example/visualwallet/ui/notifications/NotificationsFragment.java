package com.example.visualwallet.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.visualwallet.Account;
import com.example.visualwallet.AddNewTag;
import com.example.visualwallet.R;

public class NotificationsFragment extends Fragment {

    private LinearLayout accountll;
    private Button wallet_btn1;
    private ImageButton wallet_add;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_notifications, null);
        accountll = (LinearLayout)view.findViewById(R.id.accounts_linear);
        wallet_btn1 = (Button) view.findViewById(R.id.wallet_button1);
        wallet_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Account.class);
                //TODO
                //目标Activity
                //这里应该做成所有的账户button都访问Account，并且根据提交的数据显示
                startActivity(intent);
            }
        });
        wallet_add = (ImageButton) view.findViewById(R.id.wallet_button_add);
        wallet_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewTag.class);
                //TODO
                //startActivity(intent);
                //这里应该有个返回值啥的
                addCtrl(view);
            }
        });
        return view;
    }

    private void addCtrl(View view) {
        if(view == null) {
            return;
        }
        Button newbtn = new Button(getActivity());
        newbtn.setText("狗币"+"账户");//这里应该是返回的币种类型
        newbtn.setTextSize(20);
        newbtn.setBackgroundResource(R.drawable.buttom_press);
//        newbtn.setOnClickListener(); 加一个监听列表
        accountll.addView(newbtn);
        return;
    }

}