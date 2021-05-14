package com.example.visualwallet.common;

/**
 * Create by LuczyDoge @ 2021/4/28
 * app 常量
 */
public class Constant {
    // 网络相关
    public static final String protocol = "http";
    public static final String domain = "vw.milkyship.cn";
    public static final String port = "8000";
    public static final String projectRoot = "/vw";
    public static int connectTimeout = 3000;
    public static int readTimeout = 3000;

    // 区块链接口相关
    public static final String blockchainTestDomain = "https://api.blockcypher.com";
    public static final String fromAddr = "";
    public static final String toAddr = "";
    public static final long value = 1000;

    // 关键字常量
    public static final String WALLET_ARG = "wallet_arg";
    public static final int REQUEST_ADD_ACC = 301;
    public static final int REQUEST_DEL_ACC = 302;
}
