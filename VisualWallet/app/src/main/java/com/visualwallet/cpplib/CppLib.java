package com.visualwallet.cpplib;

public class CppLib {
    /*
     * 如需增加c++动态链接接口，
     * 先在本类中添加java native函数，
     * 然后在 <项目根目录>/app/src/main/java 目录下使用以下命令以重新生成c++头文件，
     *
     * javah -jni com.visualwallet.cpplib.CppLib
     *
     * 运行该命令后，在 <项目根目录>/app/src/main/java 中会出现一个.h文件，将其移动到 <项目根目录>/app/src/main/cpp 中。
     * 然后按照头文件中声明的函数格式进行实现
     * 注意要在static区域添加动态链接库加载代码
     */

    public static native boolean detect(byte[] androidID, int n, int splitID, int[][] s0, int[][] s1, int sRows, int sCols, int[][] toDetect, int detectSize);
    // bool detect(char* androidID, int n, int splitID, int** s0, int** s1, int sRows, int sCols, int** toDetect, int detectSize);

    static {
        System.loadLibrary("detect");
    }
}
