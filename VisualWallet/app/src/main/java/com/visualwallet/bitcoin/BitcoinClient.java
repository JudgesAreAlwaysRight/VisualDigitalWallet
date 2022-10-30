package com.visualwallet.bitcoin;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;

public class BitcoinClient {

    private static NetworkParameters parameters;
    private static String filePrefix;

    public static NetworkParameters initNetWork() {
        return initNetWork("");
    }

    public static NetworkParameters initNetWork(String net) {
        if (net.equals("testnet")) {
            parameters = TestNet3Params.get();
            filePrefix = "forwarding-service-testnet";
        } else if (net.equals("regtest")) {
            parameters = RegTestParams.get();
            filePrefix = "forwarding-service-regtest";
        } else {
            parameters = MainNetParams.get();
            filePrefix = "forwarding-service";
        }
        return parameters;
    }

}
