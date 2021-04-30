import cv2
from math import ceil, floor, sqrt
from itertools import combinations, groupby
import numpy as np
from scipy.special import comb, perm
import random

"""

    在(k，n)方案中，一共生成了n个分存项目
    对于原图像的每一个像素，被扩充成了m个子像素
    最终形成n*m的一个分存矩阵，每一行对应一个分存项目
    合并图像实际上是把其中任意k行的元素进行或运算并比较其汉明重（1的数目）
    S黑的汉明重大于d（设定阈值）时，这个像素被认为是黑色的
    S白的汉明重小于d-am时(其中a >= 1/m)，被认为是白色的，介于之中的无法识别

"""
address = 'C:/Users/49393/Desktop/GraphOP/'
img = cv2.imread(r'C:\Users\49393\Desktop\GraphOP\test1.png', 1)
# gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
# # # 二值化处理
# bin = cv2.adaptiveThreshold(~gray, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, 35, -5)
bin = img
# cv2.imwrite(address+"Binary1.png", bin)
x, y = img.shape[0:2]
"""
    新增在makes中计算阈值，将阈值也作为makes的返回值，阈值统一命名为d0.d1
"""

# 创建用于分存的s0,s1
def makes(k, n):
    p = 0
    d1 = 0
    d0 = 0
    a0 = []
    a1 = []
    s0 = []
    s1 = []
    # 之后生成组合数
    all = [1]
    for i in range(n):
        all.append(0)
        all.append(1)
    all.append(0)
    # 生成所有组合并去重
    b = list(combinations(all, n))
    s = set()
    for i in range(len(b)):
        s.add(b[i])
    allc = [list(x) for x in list(s)]

    # 2
    while p <= floor(k / 2):
        if p % 2 == 0:
            t0 = comb((n - k + floor(k / 2) - p), (floor(k / 2) - p))
            t1 = 0
        else:
            t1 = comb((n - k + floor(k / 2) + p), (floor(k / 2) - p))
            t0 = 0
        if p >= 1:
            if (k >= 1) and (k < n - p + 1):
                for x in range(k):
                    d0 += comb(n-x, p-1)*t0
                    d1 += comb(n-x, p-1)*t1
            elif k >= n-p+1:
                    d0 += comb(n, p) * t0
                    d1 += comb(n, p) * t1
        print("p=%d: d0=%d, d1 =%d" % (p, d0, d1))
        a0.append(t0)
        a1.append(t1)
        p += 1
    # 3
    while p <= ((n - k) + floor(k / 2)):
        a0.append(0)
        a1.append(0)
        p += 1
    # 4
    while p <= n:
        if (p - n + k) % 2 == 0:
            t0 = comb((p - floor(k / 2) - 1), (p - (n - k) - floor(k / 2) - 1))
            t1 = 0
        else:
            t0 = 0
            t1 = comb((p - floor(k / 2) - 1), (p - (n - k) - floor(k / 2) - 1))
        if p >= 1:
            if (k >= 1) and (k < n - p + 1):
                for x in range(k):
                    d0 += comb(n-x, p-1)* t0
                    d1 += comb(n-x, p-1)* t1
            elif k >= n-p+1:
                    d0 += comb(n, p) * t0
                    d1 += comb(n, p) * t1
        print("p=%d: d0=%d, d1 =%d" % (p, d0, d1))
        a0.append(t0)
        a1.append(t1)
        p += 1

    # 5
    p = 0
    # 6

    while p <= n:
        for c in range(len(allc)):
            if allc[c].count(1) == p:
                for x in range(int(a0[p])):
                    s0.append(allc[c])
                for x in range(int(a1[p])):
                    s1.append(allc[c])
        p += 1
    # 扩充为m个像素

    m1 = len(s0)
    m2 = len(s1)
    m = max(m1, m2)
    # 计算较为接近的平方便于扩充为 n*n的形式
    goal = ceil(sqrt(m)) ** 2

    # 补全为1的行(之后转置为列)
    e = [0 for i in range(n)]
    for i in range(goal - m2):
        s1.append(e)
    for i in range(goal - m1):
        s0.append(e)

    s0 = np.array(s0).T
    s1 = np.array(s1).T
    print(len(s1[1]))
    print(s0)
    print(s1)
    return s0, s1, goal, d0, d1


# 根据合并的图像还原
def decode(merge, m, d0, d1):
    qr = np.zeros((x, y, 3), np.uint8)

    for ix in range(x):
        for jy in range(y):
            count = 0
            for om in range(m * m):
                for ok in range(3):
                    count += int((255 - merge[m * ix + floor(om / m), m * jy + om % m, ok]) / 255)
            count = count / 3
            # print(count)
            if count >= d1:
                for ii in range(3):
                    qr[ix, jy, ii] = 0
            # elif count <= d - int(alpha*m*m):
            elif count <= d0:
                for ii in range(3):
                    qr[ix, jy, ii] = 255

    return qr


if __name__ == "__main__":
    k, n = eval(input("Set K, N(max 5) for your QR code: "))
    s0, s1, m, d0, d1 = makes(k, n)
    print(d0)
    print(d1)
    lenm = int(sqrt(m))
    # 每个像素扩充成lenm*lenm
    newx = int(x * lenm)
    newy = int(y * lenm)

    # 合并图和分存图
    split = []
    for i in range(n):
        split.append(np.zeros((newx, newy, 3), np.uint8))

    mergek = np.zeros((newx, newy, 3), np.uint8)
    # 这个part有优化空间，可以搞成并行的
    # 原图的横轴
    # print(m)
    randlistblack = random.sample(range(0, m), m)
    randlistwhite = random.sample(range(0, m), m)
    # print(randlist)
    for i in range(x):
        # 原图的纵轴
        for j in range(y):
            # print(bin[i, j])
            # print("any %d" % j, bin[i, j].any())
            # print("all %d" % j, bin[i, j].all())
            if bin[i, j].all() == False:
                # 每一张分存图
                for c in range(n):
                    for o in range(len(s0[0])):
                        split[c][lenm * i + floor(o / lenm), lenm * j + o % lenm] = (1 - s1[c][randlistblack[o]]) * 255

            if bin[i, j].all() == True:
                for c in range(n):
                    for o in range(len(s0[0])):
                        split[c][lenm * i + floor(o / lenm), lenm * j + o % lenm] = (1 - s0[c][randlistwhite[o]]) * 255

    for i in range(n):
        cv2.imwrite(address + "split" + str(i) + ".png", split[i])

    # 图像合并
    r = random.sample(range(0, n), k)
    # print(k)
    # print(r)
    for i in range(k):
        # print("k%d" % i)
        if i == 0:
            mergek = split[r[i]]
        else:
            for si in range(newx):
                for sj in range(newy):
                    for sk in range(3):
                        if mergek[si, sj, sk] == split[r[i]][si, sj, sk] and mergek[si, sj, sk] == 255:
                            mergek[si, sj, sk] = 255
                        else:
                            mergek[si, sj, sk] = 0

    cv2.imwrite(address + "mergek.png", mergek)

    # for i in range(newx):
    #     for j in range(newy):
    #         for k in range(3):
    #             if 255 - merge[i, j, k] == 0:
    #                 merge[i, j, k] = 1
    #             elif 255 - merge[i, j, k] == 255:
    #                 merge[i, j, k] = 0



    qr = decode(mergek, lenm, d0, d1)
    cv2.imwrite(address + "res.png", qr)

