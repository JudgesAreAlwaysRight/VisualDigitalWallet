package com.visualwallet.data;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.visualwallet.Algorithm.Algorithm;
import com.visualwallet.common.logo.*;
import com.google.zxing.qrcode.QRCodeWriter;
import com.visualwallet.R;

import java.util.Hashtable;

public class AlgorithmUtil {
    // 生成分存图用于返回前端的数据结构
    public static class GenerateResponse {
        int[][][] keyMatrix;
        int[][] randList;
        int splitMatSize;
        int carrierMatSize;

        GenerateResponse(int [][][] kM, int [][] rL, int sMS, int cMS) {
            keyMatrix = kM;
            randList = rL;
            splitMatSize = sMS;
            carrierMatSize = cMS;
        }
    }

    public static class ValidateRequest {
        int[] splitOrder;
        int[][][] keyMatrix;
        int[][] randList;
        int k;
        int n;
        int splitMatSize;
        int carrierMatSize;

        ValidateRequest(int[] sO, int[][][] kM, int[][] rL, int k_, int n_, int sMS, int cMS) {
            splitOrder = sO;
            keyMatrix = kM;
            randList = rL;
            k = k_;
            n = n_;
            splitMatSize = sMS;
            carrierMatSize = cMS;
        }
    }


    // 由私钥pk生成数据矩阵pkMatrix
    public static int[][] dataMatGenerate(String pkBinary) {
        int [][] pkMatrix = new int [16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int index = i * 16 + j;
                if (pkBinary.charAt(index) == '1') {
                    pkMatrix[i][j] = 0;
                } else {
                    pkMatrix[i][j] = 1;
                }
            }
        }
        return pkMatrix;
    }

    // 生成carrier列表
    public static int[][][] carrierGenerate(String qrCodeText) throws WriterException {
        int [][][] carrierList = new int[5][][];
        for (int i = 0; i < 5; i++) {
            carrierList[i] = ImageUtils.encodeBitMat(qrCodeText);
        }
        return carrierList;
    }

    // 在carrier上嵌入logo
    public static int[][][] addLogo(String logo, int[][][] carrierMatrix) {
        int[][] logoMatrix;
        switch (logo) {
            case "BTC":
                logoMatrix = BTC.BTC;
            case "CNY":
                logoMatrix = CNY.CNY;
            case "ETH":
                logoMatrix = ETH.ETH;
            case "LTC":
                logoMatrix = LTC.LTC;
            case "USD":
                logoMatrix = USD.USD;
            case "USDT":
                logoMatrix = USDT.USDT;
            case "XRP":
                logoMatrix = XRP.XRP;
            default:
                logoMatrix = new int[][]{{}};
        }
        int logoSize = BTC.BTC.length;
        for (int[][] matrix : carrierMatrix) {
            for (int ix = 0; ix < logoSize; ix++) {
                System.arraycopy(logoMatrix[ix], 0, matrix[ix + 15], 15, logoSize);
            }
        }
        return carrierMatrix;
    }

    // 在嵌入了logo的carrier上嵌入分存图
    public static int[][][] addSplit(int[][][] carrierMatrix, int[][][]splitMatrix) {
        int carrierSize = carrierMatrix[0].length;
        int splitSize = splitMatrix[0].length;
        int beginIndex = carrierSize - splitSize - 15;
        for (int i = 0; i < splitMatrix.length; i++) {
            for (int ix = 0; ix < splitSize; ix++) {
                System.arraycopy(splitMatrix[i][ix], 0, carrierMatrix[i][ix+beginIndex], beginIndex, splitSize);
            }
        }
        return carrierMatrix;
    }

    // 从key中还原出split
    public static int[][][] removeCarrier(int[][][] keyMatrix, int splitSize, int carrierSize) {
        int [][][]splitMatrix = new int[keyMatrix.length][splitSize][splitSize];
        int beginIndex = carrierSize - splitSize - 15;
        for (int i = 0; i < keyMatrix.length; i++) {
            for (int j = 0; j < splitSize; j++) {
                System.arraycopy(keyMatrix[i][j+beginIndex], beginIndex, splitMatrix[i][j], 0,  splitSize);
            }
        }
        return splitMatrix;
    }

    public static String retrievePk(int [][]dataMat) {
        char[] pk = new char[256];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int index = i * 16 + j;
                pk[index] = (dataMat[i][j] == 1)? '0': '1';
            }
        }
        return new String(pk);
    }

    // 生成分存图全流程
    public static GenerateResponse generateSpilt(String pkBinary, int k, int n, String logo) throws WriterException {
        // Algorithm Part
        Algorithm.splitMatrix sMatrix = Algorithm.makeS(k, n);
        int row = sMatrix.S0.get(0).size(); // 转置后矩阵的行数, S0和S1一致
        int col0 = sMatrix.S0.size(); // 转置后S0的列数
        int m0 = (int)Math.ceil((double)Math.sqrt(col0));
        int col1 = sMatrix.S1.size(); // 转置后S1的列数
        int m1 = (int)Math.ceil((double)Math.sqrt(col1));
        int m = Math.max(m1, m0);
        int newCol = m*m; // 膨胀为平方数

        int[][] randList = Algorithm.randPermute(n, newCol, 928);

        // Split Generate Part
        int[][] pkMatrix = dataMatGenerate(pkBinary);
        int[][][] splitMatrix = Algorithm.split(pkMatrix, sMatrix, n, randList);
        int[][][] carrierMatrix = carrierGenerate("test");
        int[][][] keyMatrix = addSplit(addLogo(logo, carrierMatrix), splitMatrix);

        return new GenerateResponse(keyMatrix, randList, splitMatrix.length, carrierMatrix.length);
    }

    // 取回私钥全流程
    public static String retrievePk(ValidateRequest vR) {

        int[] splitOrder = vR.splitOrder;
        int[][][] keyMatrix = vR.keyMatrix;
        int[][] randList = vR.randList;
        int k = vR.k;
        int n = vR.n;
        int splitMatSize = vR.splitMatSize;
        int carrierMatSize = vR.carrierMatSize;

        int [][][] splitMatrix = removeCarrier(keyMatrix, splitMatSize, carrierMatSize);

        // Algorithm Part
        int[][][] splitImages = new int [splitOrder.length][][];
        for (int i = 0; i < splitOrder.length; i++) {
            splitImages[i] = splitMatrix[splitOrder[i]];
        }
        Algorithm.splitMatrix s = Algorithm.makeS(k, n); //生成矩阵
        int row = s.S0.get(0).size(); // 转置后矩阵的行数, S0和S1一致
        int col0 = s.S0.size(); // 转置后S0的列数
        int m0 = (int)Math.ceil((double)Math.sqrt(col0));
        int col1 = s.S1.size(); // 转置后S1的列数
        int m1 = (int)Math.ceil((double)Math.sqrt(col1));
        int m = Math.max(m1, m0);
        int [][] retrieveMatrix = Algorithm.restore(splitImages, s, randList, k, n, m, splitOrder);

        return retrievePk(retrieveMatrix);

    }
}
