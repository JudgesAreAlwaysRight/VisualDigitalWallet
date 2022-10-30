package com.visualwallet.bitcoin;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;

public class BitcoinUtil {

    public static String[] genAccount() {
        ECKey key = new ECKey();
        return new String[] {
                key.getPrivateKeyAsHex(),
                key.getPublicKeyAsHex()
        };
    }



}
