package com.visualwallet.bitcoin;

import android.util.Log;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.KeyChainGroupStructure;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.math.BigInteger;

public class BitcoinClient {

    private static NetworkParameters params;
    private static String filePrefix;

    {
//        initNetWork("");
        initNetWork("testnet");
    }

    public static void initNetWork(String net) {
        if (net.equals("testnet")) {
            filePrefix = "forwarding-service-testnet";
        } else if (net.equals("regtest")) {
            filePrefix = "forwarding-service-regtest";
        } else {
            // 默认使用主链
            filePrefix = "forwarding-service";
        }
        params = getParameters(net);
    }

    public static NetworkParameters getParameters(String net) {
        if (net.equals("testnet")) {
            return TestNet3Params.get();
        } else if (net.equals("regtest")) {
            return RegTestParams.get();
        } else {
            // 默认使用主链
            return MainNetParams.get();
        }
    }

    public static Wallet generateBitcoinWallet() {
        ECKey key = new ECKey();
        Wallet wallet = Wallet.createDeterministic(params, Script.ScriptType.P2PKH);
        wallet.importKey(key);
        return wallet;
    }

    public static ECKey getBitcoinKey(String privateKeyStr) {
        ECKey key;
        if (privateKeyStr.length() == 51 || privateKeyStr.length() == 52) {
            DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, privateKeyStr);
            key = dumpedPrivateKey.getKey();
        } else {
            BigInteger privateKey = Base58.decodeToBigInteger(privateKeyStr);
            key = ECKey.fromPrivate(privateKey);
        }
        return key;
    }

    public static Wallet getBitcoinWallet(String privateKeyStr) {
        ECKey key;
        if (privateKeyStr.length() == 51 || privateKeyStr.length() == 52) {
            DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, privateKeyStr);
            key = dumpedPrivateKey.getKey();
        } else {
            BigInteger privateKey = Base58.decodeToBigInteger(privateKeyStr);
            key = ECKey.fromPrivate(privateKey);
        }
        Wallet wallet = Wallet.createDeterministic(params, Script.ScriptType.P2PKH);
        wallet.importKey(key);
        return wallet;
    }

    public static Sha256Hash sendTransaction(String targetAddress, String valueStr, String feeStr)
            throws InsufficientMoneyException {
        // We use the WalletAppKit that handles all the boilerplate for us.
        WalletAppKit kit = new WalletAppKit(
                params,
                Script.ScriptType.P2WPKH,
                KeyChainGroupStructure.DEFAULT,
                new File("."),
                "sendrequest-example");
        kit.startAsync();
        kit.awaitRunning();

        Coin value = Coin.parseCoin(valueStr);
        Coin fee = Coin.parseCoin(feeStr);

        // To which address you want to send the coins?
        // The Address class represents a Bitcoin address.
        Address toAddress = Address.fromString(params, targetAddress);
        Log.i("sendTx", "Send money to: " + toAddress);

        // Have a look at the code of the SendRequest class to see what's happening and what other options you have:
        // https://bitcoinj.github.io/javadoc/0.11/com/google/bitcoin/core/Wallet.SendRequest.html
        //
        // Please note that this might raise a InsufficientMoneyException if your wallet has not enough coins to spend.
        // When using the testnet you can use a faucet to get testnet coins.
        // In this example we catch the InsufficientMoneyException and register a BalanceFuture callback
        // that runs once the wallet has enough balance.
        try {
            Log.i("sendTx", "Send money to: " + kit.wallet().currentReceiveAddress()
                    + " value: " + value.getValue() + "satoshis. "
                    + "fee: " + fee.getValue() + "satoshis.");
            SendRequest req = SendRequest.to(toAddress, value);
            req.feePerKb = fee;
            Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), req);
            Log.i("sendTx", "coins sent. transaction hash: " + result.tx.getTxId());
            return result.tx.getTxId();
            // you can use a block explorer like https://www.biteasy.com/ to inspect the transaction
            // with the printed transaction hash.
        } catch (InsufficientMoneyException e) {
            Log.e("sendTx", "Not enough coins in your wallet. Missing "
                    + (e.missing == null ? "<NULL>" : e.missing.getValue())
                    + " satoshis are missing (including fees)");
            throw e;
        }
    }

}
