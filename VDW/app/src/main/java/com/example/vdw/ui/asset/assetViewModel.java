package com.example.vdw.ui.asset;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class assetViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public assetViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("这里放资产，暂时还没做");
    }

    public LiveData<String> getText() {
        return mText;
    }
}