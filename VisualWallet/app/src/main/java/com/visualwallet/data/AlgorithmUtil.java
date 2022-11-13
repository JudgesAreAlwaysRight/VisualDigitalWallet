package com.visualwallet.data;

import static com.visualwallet.Algorithm.Algorithm.makeS;
import static com.visualwallet.Algorithm.Algorithm.randPermute;
import static com.visualwallet.Algorithm.Algorithm.restore;

import com.google.zxing.WriterException;
import com.visualwallet.Algorithm.Algorithm;
import com.visualwallet.common.Constant;
import com.visualwallet.common.logo.BTC;
import com.visualwallet.common.logo.CNY;
import com.visualwallet.common.logo.ETH;
import com.visualwallet.common.logo.LTC;
import com.visualwallet.common.logo.USD;
import com.visualwallet.common.logo.USDT;
import com.visualwallet.common.logo.XRP;
import com.visualwallet.entity.GenerateResponse;

import java.time.Instant;
import java.util.Random;

public class AlgorithmUtil {

    public static class ValidateRequest {
        int[] splitOrder;
        int[][][] keyMatrix;
        int k;
        int n;
        int splitMatSize;
        int carrierMatSize;

        public ValidateRequest(int[] splitOrder, int[][][] keyMatrix, int k, int n,
                               int splitMatSize, int carrierMatSize) {
            this.splitOrder = splitOrder;
            this.keyMatrix = keyMatrix;
            this.k = k;
            this.n = n;
            this.splitMatSize = splitMatSize;
            this.carrierMatSize = carrierMatSize;
        }
    }


    /**
     * 由私钥pk生成数据矩阵pkMatrix
     */
    private static int[][] dataMatGenerate(String pkBinary) {
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

    private static String getQrcodeInfo(int index) {
        return "随身模式分存图 index\":" + index + "}";
    }

    /**
     * 生成carrier列表
     */
    private static int[][][] carrierGenerate(int coeK, int coeN) throws WriterException {
        int [][][] carrierList = new int[coeN][][];
        for (int i = 0; i < coeN; i++) {
            if (coeK < 4) {
                carrierList[i] = ImageUtils.encodeBitMat(getQrcodeInfo(i), 28);
            } else {
                carrierList[i] = ImageUtils.encodeBitMat(getQrcodeInfo(i), 32);
            }
        }
        return carrierList;
    }

    /**
     * 在carrier上嵌入logo
     */
    private static int[][][] addLogo(String logo, int[][][] carrierMatrix) {
        int[][] logoMatrix;
        switch (logo.toUpperCase()) {
            case "BTC":
                logoMatrix = BTC.BTC;
                break;
            case "CNY":
                logoMatrix = CNY.CNY;
                break;
            case "ETH":
                logoMatrix = ETH.ETH;
                break;
            case "LTC":
                logoMatrix = LTC.LTC;
                break;
            case "USD":
                logoMatrix = USD.USD;
                break;
            case "USDT":
                logoMatrix = USDT.USDT;
                break;
            case "XRP":
                logoMatrix = XRP.XRP;
                break;
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

    /**
     * 在carrier上嵌入分存图
     */
    private static int[][][] addSplit(int[][][] carrierMatrix, int[][][]splitMatrix) {
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

    /**
     * 在carrier上增加/去除mask
     */
    private static int[][][] maskKey(int[][][] keyMatrix) {
        int keySize = keyMatrix[0].length;
        long seed = 12345;
        for (int[][] key : keyMatrix) {
            Random generator = new Random(seed);
            for (int i = (int) (keyMatrix[0].length * 0.5); i < keySize - 10; i++) {
                for (int j = (int) (keyMatrix[0].length * 0.5); j < keySize - 10; j++) {
                    key[i][j] ^= generator.nextInt(2);
                }
            }
        }
        return keyMatrix;
    }

    /**
     * 从key中还原出split
     */
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

    private static String retrievePk(int [][]dataMat) {
        char[] pk = new char[256];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int index = i * 16 + j;
                pk[index] = (dataMat[i][j] == 1)? '0': '1';
            }
        }
        return new String(pk);
    }

    private static int getNewCol(Algorithm.SplitMatrix sMatrix) {
        int col0 = sMatrix.S0.size(); // 转置后S0的列数
        int m0 = (int)Math.ceil((double)Math.sqrt(col0));
        int col1 = sMatrix.S1.size(); // 转置后S1的列数
        int m1 = (int)Math.ceil((double)Math.sqrt(col1));
        int m = Math.max(m1, m0);
        return m*m; // 膨胀为平方数
    }

    /**
     * 生成分存图全流程
     */
    public static GenerateResponse generateSpilt(String pkBinary, int k, int n, String logo) throws WriterException {
        // Algorithm Part
        Algorithm.SplitMatrix sMatrix = makeS(k, n);
        int newCol = getNewCol(sMatrix);

        int[][] randList = randPermute(n, newCol, Constant.localWalletRandomSeed);

        // Split Generate Part
        int[][] pkMatrix = dataMatGenerate(pkBinary);
        int[][][] splitMatrix = Algorithm.split(pkMatrix, sMatrix, n, randList);
        int[][][] carrierMatrix = carrierGenerate(k, n);

        int[][][] keyMatrix = addSplit(carrierMatrix, splitMatrix);
        int[][][] maskedKey = maskKey(keyMatrix);
        int[][][] keyWithLogo = addLogo(logo, maskedKey);

        return new GenerateResponse(keyWithLogo, randList, splitMatrix[0].length, carrierMatrix[0].length);
    }

    /**
     * 取回私钥全流程
     */
    public static String retrievePk(ValidateRequest vR) {

        int[] splitOrder = vR.splitOrder;
        int[][][] keyMatrix = vR.keyMatrix;
        int k = vR.k;
        int n = vR.n;
        int splitMatSize = vR.splitMatSize;
        int carrierMatSize = vR.carrierMatSize;

        int [][][] splitMatrix = removeCarrier(maskKey(keyMatrix), splitMatSize, carrierMatSize);

        // Algorithm Part
        Algorithm.SplitMatrix s = makeS(k, n); //生成矩阵
        int col0 = s.S0.size(); // 转置后S0的列数
        int m0 = (int)Math.ceil((double)Math.sqrt(col0));
        int col1 = s.S1.size(); // 转置后S1的列数
        int m1 = (int)Math.ceil((double)Math.sqrt(col1));
        int m = Math.max(m1, m0);
        int newCol = getNewCol(s);
        int[][] randList = randPermute(n, newCol, Constant.localWalletRandomSeed);
        int [][] retrieveMatrix = restore(splitMatrix, s, randList, k, n, m, splitOrder);

        return retrievePk(retrieveMatrix);
    }
}
