package com.visualwallet.data;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.visualwallet.Algorithm.Algorithm;
import com.visualwallet.Algorithm.Algorithm.*;
import com.google.zxing.qrcode.QRCodeWriter;
import com.visualwallet.R;

import java.util.Hashtable;

public class AlgorithmUtil {
    // 由私钥pk生成数据矩阵pkMatrix
    public static int[][] dataMatGenerate(String pkBinary) {
        int [][] pkMatrix = new int [16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int index = i * 16 + j;
                if (pkBinary.charAt(index) == '1') {
                    pkMatrix[i][j] = 0;
                } else {
                    pkMatrix[i][j] = 256;
                }
            }
        }
        return pkMatrix;
    }

    public static int[][][] carrierGenerate(String qrCodeText) throws WriterException {
        int [][][] carrierList = new int[5][][];
        for (int i = 0; i < 5; i++) {
            carrierList[i] = ImageUtils.encodeBitMat(qrCodeText);
        }
        return carrierList;
    }

    public static int[][][] addLogo(int logo, int[][][] carrierMatrix) {
        return carrierMatrix;
    }

    public static void generateSpilt(String pkBinary, int k, int n, int logo) throws WriterException {
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


    }
}
