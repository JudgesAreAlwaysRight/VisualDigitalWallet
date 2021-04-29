package com.example.visualwallet.net;

import java.util.Map;

public abstract class NetRequest extends Thread {

    private NetCallback netCallback;

    public NetRequest setNetCallback(NetCallback netCallback) {
        this.netCallback = netCallback;
        return this;
    };

    public void callBack(Map res) { this.netCallback.callBack(res); }
}
