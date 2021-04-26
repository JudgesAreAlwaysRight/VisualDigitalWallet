package com.example.vdw.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class homeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public homeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("这里是主页，没想好放啥");
    }

    public LiveData<String> getText() {
        return mText;
    }
}