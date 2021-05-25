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
    public static final int connectTimeout = 15000;
    public static final int readTimeout = 15000;

    // 区块链接口相关
    public static final String blockchainTestDomain = "https://api.blockcypher.com";
    public static final String fromAddr = "mqeq6V1fSdAfuQra1QSpLPZ5C4LrHi9Hjx";
    public static final String toAddr = "mxr6yqkoW1ts8frpaYRSmyjW4EBWhj5r27";
    public static final long value = 20000;

    // btc demo 账号
    public static final String privateKey1 = "cSmLesshudBDNK8hKjVmnwiht981G65BAZgk5qEtxSqeLpGQWXoH";
    public static final String publicKey1 = "";
    public static final String address1 = "mqeq6V1fSdAfuQra1QSpLPZ5C4LrHi9Hjx";
    public static final String wif1 = "";
    public static final String tx1 = "01ba7d2a29d075bbd77b090d9e0662d0286fe2b061287701b2343d420a94084d";
    public static final String privateKey2 = "6a320440d8a9630996f04c3d1acd3e6990f470e19aeaaace09333a6eaf296550";
    public static final String publicKey2 = "0389214d82b8e67cef01d4c6c54377ab5f4452fa08a02a24e6ab6f71413b754ed6";
    public static final String address2 = "mxr6yqkoW1ts8frpaYRSmyjW4EBWhj5r27";
    public static final String wif2 = "cR98asvVHabjoWnw8kYD8C2WWkqXrHTbJzCHkixr38AwfQzzZBbX";
    public static final String tx2 = "";


    // 关键字常量
    public static final String WALLET_ARG = "wallet_arg";
    public static final int REQUEST_ADD_ACC = 301;
    public static final int REQUEST_DEL_ACC = 302;
}
