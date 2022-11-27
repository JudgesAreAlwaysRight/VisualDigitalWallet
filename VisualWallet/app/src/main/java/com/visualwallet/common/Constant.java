package com.visualwallet.common;

/**
 * Create by LuczyDoge @ 2021/4/28
 * app 常量
 */
public class Constant {

    public static final String downloadPath = "/DCIM/VisualWallet/";

    public static final int localWalletRandomSeed = 215646;

    // 网络相关
    public static final String protocol = "http";
    public static final String domain = "vw.milkyship.cn";
//    public static final String domain = "10.130.93.165";
    public static final String port = "8000";
    public static final String projectRoot = "/vw";
    public static final int connectTimeout = 20000;
    public static final int readTimeout = 20000;

    // 区块链接口相关
    public static final long btc2sat = 100000000;

    // 关键字常量
    public static final String WALLET_ARG = "wallet_arg";
    public static final int REQUEST_ADD_ACC = 301;
    public static final int REQUEST_DEL_ACC = 302;
    public static final int FILE_SELECT_CODE = 401;
}
