package com.visualwallet.Algorithm;

import com.visualwallet.common.Constant;

import java.util.ArrayList;
import java.util.Random;

public class Algorithm {

    /**
     * detect混合在还原里了
     * 分存用的矩阵，就是那个S0S1一串01的那个
     */
    public static class SplitMatrix {
        public ArrayList<ArrayList<Integer>> S0;
        public ArrayList<ArrayList<Integer>> S1;
        int thresh0;
        int thresh1;

        public SplitMatrix() {
            S0 = new ArrayList<>();
            S1 = new ArrayList<>();
            thresh0 = 0;
            thresh1 = 0;
        }
    }

    private static int factorial(int n) {
        return (n > 1) ? n * factorial(n - 1) : 1;
    }

    private static int combination(int n, int m) {
        return (n >= m) ? factorial(n) / factorial(n - m) / factorial(m) : 0;
    }

    /**
     * 计算阈值
     */
    private static int calcThresh(int[] alpha, int k, int n) {
        int t = 0;
        for (int h = 1; h <= n; h++) { // h=0时无意义
            int temp = 0;
            if (k >= n - h + 1) {
                temp += combination(n, h) * alpha[h];
            } else {
                for (int i = 1; i <= k; i++) {
                    temp += combination(n - i, h - 1) * alpha[h];
                }
            }
            t += temp;
        }
        return t;
    }

    /**
     * 插入列向量
     */
    private static void insertM(int n, int h, ArrayList<ArrayList<Integer>> s, ArrayList<Integer> temp) {
        if (n == 0 && h == 0) {
            // System.out.println(temp);
            s.add(temp);
            return;
        }
        if (n < 0 || h < 0) {
            return;
        }
        ArrayList<Integer> temp0 = new ArrayList<>(temp);
        temp0.add(0);
        ArrayList<Integer> temp1 = new ArrayList<>(temp);
        temp1.add(1);
        insertM(n - 1, h - 1, s, temp1);
        insertM(n - 1, h, s, temp0);
    }

    private static boolean inList(int[] list, int target, int index) {
        if (list == null || index == 0) {
            return false;
        }
        for (int i = 0; i < index; i++) {
            if (target == list[i]) {
                return true;
            }
        }
        return false;
        // return new HashSet<>(Arrays.asList(list)).contains(target);
    }

    /**
     * 生成分存时的随机列表
     */
    public static int[][] randPermute(int n, int l, int seed) {
        int[][] randList = new int[n][l];
        Random r = new Random(seed);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < l; j++) {
                int temp = r.nextInt(l);
                if (inList(randList[i], temp, j)) {
                    j--;
                } else {
                    randList[i][j] = temp;
                }
            }
        }
        return randList;
    }

    /**
     * 生成分存矩阵，返回一个splitMatrix,包括01的还原阈值thresh0和thresh1
     * 这时候是转置矩阵
     */
    public static SplitMatrix makeS(int k, int n) {
        SplitMatrix s = new SplitMatrix();
        // 各个海明重的a(t,h)的数目,h = 0,1,2,...,n-1
        int[] alpha0 = new int[n + 1];
        int[] alpha1 = new int[n + 1];
        // ceil(n/2)处取0
        int z = (n + 1) / 2;
        alpha0[z] = 0;
        alpha1[z] = 0;
        // 求解剩余部分，先取一堆0，这个取0源于有n+1个方程的n元方程组必有解，有多种取值方案，固定取一种不影响
        // -1代表还没求解
        int flag = 1;
        for (int i = 0; i < n + 1; i++) {
            if (i != z) {
                if (flag == 1) {
                    alpha1[i] = 0;
                    alpha0[i] = -1;
                    flag = 0;
                } else {
                    alpha0[i] = 0;
                    alpha1[i] = -1;
                    flag = 1;
                }
            }
        }

        // 求解未取0的部分,跳过中间全0的
        double[][] martixA = new double[k][n + 1]; // 增广矩阵,有n个未知数和k个方程，最后一个是0
        for (int i = 0; i < n + 1; i++) { // 约束3方程
            if (alpha0[i] == 0 && alpha1[i] == 0) {
                continue;
            } else if (alpha0[i] == -1) {
                if (i > z) {
                    martixA[0][i - 1] = combination(n, i);
                    continue;
                }
                martixA[0][i] = combination(n, i);
            } else {
                if (i > z) {
                    martixA[0][i - 1] = -1 * combination(n, i);
                    continue;
                }
                martixA[0][i] = -1 * combination(n, i);
            }
        }
        martixA[0][n] = 0;
        for (int ka = 1; ka < k; ka++) { // 约束7方程组
            martixA[ka][0] = 0;
            martixA[ka][n] = 0;
            for (int h = n - ka + 1; h < n + 1; h++) {
                if (alpha0[h] == 0 && alpha1[h] == 0) {
                    continue;
                } else if (alpha0[h] == -1) {
                    if (h > z) {
                        martixA[ka][h - 1] = combination(n, h);
                        continue;
                    }
                    martixA[ka][h] = combination(n, h);
                } else {
                    if (h > z) {
                        martixA[ka][h - 1] = -1 * combination(n, h);
                        continue;
                    }
                    martixA[ka][h] = -1 * combination(n, h);
                }
            }
            for (int h = 1; h < n - ka + 1; h++) {
                for (int x = 1; x < ka + 1; x++) {
                    if (alpha0[h] == 0 && alpha1[h] == 0) {
                        continue;
                    } else if (alpha0[h] == -1) {
                        if (h > z) {
                            martixA[ka][h - 1] = combination(n - x, h - 1);
                            continue;
                        }
                        martixA[ka][h] += combination(n - x, h - 1);
                    } else {
                        if (h > z) {
                            martixA[ka][h - 1] = -1 * combination(n - x, h - 1);
                            continue;
                        }
                        martixA[ka][h] += -1 * combination(n - x, h - 1);
                    }
                }
            }

        }
        // 矩阵求解
        // 转化为阶梯矩阵
        for (int i = 0; i < k; i++) {
            for (int j = i + 1; j < k; j++) {
                if (martixA[j][i] != 0) {
                    double r = martixA[j][i] / martixA[i][i];
                    for (int m = i; m < n + 1; m++) {
                        martixA[j][m] -= martixA[i][m] * r;
                    }
                }
            }
        }
        double[] res = new double[n];
        res[n - 1] = 1;
        for (int i = k - 1; i >= 0; i--) {
            double known = 0;
            for (int j = n - 1; j >= n - k + i; j--) {
                known += martixA[i][j] * res[j];
            }
            res[n - k + i - 1] = -1 * known / martixA[i][n - k + i - 1];
        }
        // 结果归一化
        int[] intres = new int[n];
        double min = 99999999;
        for (int i = 0; i < n; i++) {
            min = Math.min(min, res[i]);
        }
        for (int i = 0; i < n; i++) {
            intres[i] = (int) (res[i] / min);
        }
        // 写回到alpha中,现在的alpha代表所有海明重为i的向量需要被添加alpha[i]次
        int flag2 = 0;
        for (int i = 0; i < n + 1; i++) {
            if (i == z) {
                flag2 = 1;
            } else {
                if (alpha0[i] == -1) {
                    alpha0[i] = intres[i - flag2];
                } else {
                    alpha1[i] = intres[i - flag2];
                }
            }
        }
        // 填充s
        s.thresh0 = calcThresh(alpha0, k, n);
        s.thresh1 = calcThresh(alpha1, k, n);
        for (int i = 0; i < n + 1; i++) {
            ArrayList<Integer> t0 = new ArrayList<>();
            ArrayList<Integer> t1 = new ArrayList<>();
            for (int j = 0; j < alpha0[i]; j++) {
                insertM(n, i, s.S0, t0);
            }
            for (int j = 0; j < alpha1[i]; j++) {
                insertM(n, i, s.S1, t1);
            }
        }
        // System.out.println(s.S0);
        // System.out.println(s.S1);
        // System.out.println("\n");
        return s;
    }

    /**
     * 输入一个原始的像素点矩阵，返回一系列分存矩阵，做过random permute
     */
    public static int[][][] split(int[][] origin, SplitMatrix s, int n, int[][] randList) {

        int row = s.S0.get(0).size(); // 转置后矩阵的行数, S0和S1一致
        int col0 = s.S0.size(); // 转置后S0的列数
        int m0 = (int) Math.ceil((double) Math.sqrt(col0));
        int col1 = s.S1.size(); // 转置后S1的列数
        int m1 = (int) Math.ceil((double) Math.sqrt(col1));
        int m = Math.max(m1, m0);
        int newcol = m * m; // 膨胀为平方数
        // 填充0向量
        ArrayList<Integer> zero = new ArrayList<>();
        for (int i = 0; i < row; i++) {
            zero.add(0);
        }
        for (int i = 0; i < newcol - col0; i++) {
            s.S0.add(zero);
        }
        for (int i = 0; i < newcol - col1; i++) {
            s.S1.add(zero);
        }

        int l = origin.length * m; // 膨胀后矩阵的长宽
        int[][][] splitImages = new int[n][l][l]; // n张图存为l*l的矩阵
        for (int i = 0; i < n; i++) {  // 第n个分存图，对应第n行并采用第n个随机序列
            for (int x = 0; x < origin.length; x++) {
                for (int y = 0; y < origin.length; y++) {
                    //原图的(x,y)像素膨胀到(mx,my)(mx,my+1)...(mx,my+m-1)
                    //以及(mx+1,my)...(mx+m-1,my)
                    if (origin[x][y] == 0) {
                        // 这个像素取值0
                        for (int t = 0; t < newcol; t++) {
                            // 填充
                            splitImages[i][m * x + t / m][m * y + t % m] = s.S0.get(randList[i][t]).get(i);
                            // splitImages[i][m*x+t/m][m*y+t%m] = s.S0.get(t).get(i).intValue();
                        }
                    }
                    if (origin[x][y] == 1) {
                        // 这个像素取值1
                        for (int t = 0; t < newcol; t++) {
                            // 填充
                            splitImages[i][m * x + t / m][m * y + t % m] = s.S1.get(randList[i][t]).get(i);
                            // splitImages[i][m*x+t/m][m*y+t%m] = s.S1.get(t).get(i).intValue();
                        }
                    }

                }

            }
        }
        return splitImages;
    }

    /**
     * 输入一系列分存矩阵，还原像素点矩阵
     */
    public static int[][] restore(int[][][] image, SplitMatrix s, int[][] randList, int k, int n, int m, int[] order) {
        int l = image[0][0].length / m;

        int[][] recoverList = new int[randList.length][randList[0].length]; //还原用的列表
        for (int i = 0; i < randList.length; i++) {
            for (int j = 0; j < randList[0].length; j++) {
                for (int x = 0; x < randList[0].length; x++) {
                    if (j == randList[i][x]) {
                        recoverList[i][j] = x;
                    }
                }
            }
        }

        int[][] recover = new int[l][l];
        int[][] sum = new int[l * m][l * m];    // 存放合成后的图
        for (int i = 0; i < order.length; i++) { // 像素叠加
            for (int x = 0; x < image[0].length; x++) {
                for (int y = 0; y < image[0][0].length; y++) {
                    int place = recoverList[order[i]][(x % m) * m + y % m];
                    int newx = (int) (x / m) * m + (int) (place / m);
                    int newy = (int) (y / m) * m + place % m;
                    if (sum[x][y] == 1 || image[i][newx][newy] == 1) {
                        sum[x][y] = 1;
                    }
                }
            }
        }
        // for(int j = 0; j < sum.length; j++){
        //     for(int i = 0; i < sum[j].length; i++){
        //         System.out.print(sum[j][i]);
        //         System.out.print('\t');
        //     }
        //     System.out.print("\n");
        // }
        // System.out.println("------SumMartix");
        // 还原
        for (int x = 0; x < l; x++) {
            for (int y = 0; y < l; y++) {
                int threshold = 0;
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < m; j++) {
                        threshold += sum[x * m + i][y * m + j];
                    }
                }
                if (threshold >= s.thresh1) {
                    recover[x][y] = 1;
                } else if (threshold <= s.thresh0) {
                    recover[x][y] = 0;
                } else {
                    System.exit(0);
                }
            }
        }

        return recover;
    }

    private static void test(String[] args) {
        // Algorithm al = new Algorithm();
        SplitMatrix s = makeS(3, 4); //生成矩阵

        int row = s.S0.get(0).size(); // 转置后矩阵的行数, S0和S1一致
        int col0 = s.S0.size(); // 转置后S0的列数
        int m0 = (int) Math.ceil((double) Math.sqrt(col0));
        int col1 = s.S1.size(); // 转置后S1的列数
        int m1 = (int) Math.ceil((double) Math.sqrt(col1));
        int m = Math.max(m1, m0);
        int newcol = m * m; // 膨胀为平方数
        // System.out.print("\n");

        int[][] randList = randPermute(4, newcol, Constant.localWalletRandomSeed); //这个随机列表需要保存

        // for(int i = 0; i < randList.length; i++){
        //     for(int j = 0; j < randList[i].length; j++){
        //         System.out.print(randList[i][j]);
        //         System.out.print('\t');
        //     }
        //     System.out.print("\n");
        // }

        int[][] origin = {{0, 1, 0}, {1, 0, 0}, {0, 0, 1}};//测试用
        int[][][] splitImages = split(origin, s, 4, randList); // 得到3份分存图

        // for(int i = 0; i < 3; i++){
        //     for(int j = 0; j < splitImages[i].length; j++){
        //         for(int k = 0; k < splitImages[i][j].length; k++){
        //             System.out.print(splitImages[i][j][k]);
        //             System.out.print('\t');
        //         }
        //         System.out.print("\n");
        //     }
        //     System.out.print("------SplitMartix");
        //     System.out.println(i);
        // }

        // 这轮还要输入每张图片的编号，这个编号按理来说应该存在外面的掩码里
        int[] order = {0, 2, 3};
        int[][][] inputImages = {splitImages[0], splitImages[2], splitImages[3]};

        int[][] recover = restore(inputImages, s, randList, 3, 4, m, order);

        for (int[] ints : recover) {
            for (int anInt : ints) {
                System.out.print(anInt);
                System.out.print('\t');
            }
            System.out.print("\n");
        }
    }
}
