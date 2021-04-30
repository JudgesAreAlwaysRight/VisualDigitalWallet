from math import ceil, floor, sqrt
from itertools import combinations
from scipy.special import comb
import cv2
import numpy as np
import qrcode
import pyzbar.pyzbar as pyzbar
from PIL import Image
from qr.kn.global_var import *
import hashlib
import pickle


def qrcodeGenerate(msg):
    qr_maker = qrcode.QRCode(version=1, error_correction=qrcode.constants.ERROR_CORRECT_M, box_size=1, border=0)
    qr_maker.add_data(msg)
    qr_maker.make(fit=True)
    qr_img = qr_maker.make_image()
    qr_img.save(TARGET)
    qr_img = cv2.imread(TARGET)
    length, width = qr_img.shape[0:2]
    return qr_img, length, width


def carrierGenerate(msg, boxsize):
    qr_maker = qrcode.QRCode(version=22, error_correction=qrcode.constants.ERROR_CORRECT_H, box_size=1, border=0)
    qr_maker.add_data(msg)
    img = qr_maker.make_image()
    return img


def alter(carry, split):
    carrier = carry
    split01 = split
    x, y = carrier.shape[0:2]
    xc, yc = split01.shape[0:2]
    shadow1 = np.zeros((x, y, 3), np.uint8)
    shadow1.fill(255)
    shadow1[x - xc - 15*ES:x - 15*ES, y - yc - 15*ES:y - 15*ES] = split01
    key = cv2.bitwise_xor(carrier, shadow1)
    # 反色
    black = np.zeros((x, y, 3), np.uint8)
    black.fill(255)
    key = cv2.bitwise_xor(key, black)
    return key


def embedding(logo, carry):
    carrier = carry
    image = cv2.imread(logo, -1)
    l, w = image.shape[0:2]
    shadow = cv2.resize(image, (l*ES, w*ES), interpolation=cv2.INTER_NEAREST)
    carrier.flags.writeable = True
    carrier[15*ES:15*ES+l*ES, 15*ES:15*ES+w*ES] = shadow
    return carrier


def makes(k, n):
    p = 0
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
    while p <= floor(k/2):
        if p % 2 == 0:
            t0 = comb((n - k + floor(k/2) - p), (floor(k/2) - p))
            t1 = 0
        else:
            t1 = comb((n - k + floor(k / 2) + p), (floor(k / 2) - p))
            t0 = 0
        a0.append(t0)
        a1.append(t1)
        p += 1
    # 3
    while p <= ((n - k) + floor(k/2)):
        a0.append(0)
        a1.append(0)
        p += 1
    # 4
    while p <= n:
        if (p - n + k) % 2 == 0:
            t0 = comb((p - floor(k/2) - 1), (p - (n-k) - floor(k/2) - 1))
            t1 = 0
        else:
            t0 = 0
            t1 = comb((p - floor(k/2) - 1), (p - (n-k) - floor(k/2) - 1))
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
    m = len(s0)
    # 计算较为接近的平方便于扩充为 n*n的形式
    goal = ceil(sqrt(m)) ** 2
    # 补全为1的行(之后转置为列)
    e = [1 for i in range(n)]
    for i in range(goal - m):
        s0.append(e)
        s1.append(e)

    s0 = np.array(s0).T
    s1 = np.array(s1).T

    return s0, s1, goal


def generateSplit(s0, s1, m, x, y, k, n, bin):
    lenm = int(sqrt(m))
    # 每个像素扩充成lenm*lenm
    newx = int(x*lenm)
    newy = int(y*lenm)

    # 合并图和分存图
    split = []
    for i in range(n):
        split.append(np.zeros((newx, newy, 3), np.uint8))

    # 这个part有优化空间，可以搞成并行的
    # 原图的横轴
    for i in range(x):
        # 原图的纵轴
        for j in range(y):
            if not bin[i, j].all():
                # 每一张分存图
                for c in range(n):
                    for o in range(len(s0[0])):
                        split[c][lenm*i + floor(o/lenm), lenm*j + o % lenm] = (1 - s1[c][o]) * 255

            if bin[i, j].all():
                for c in range(n):
                    for o in range(len(s0[0])):
                        split[c][lenm*i + floor(o/lenm), lenm*j + o % lenm] = (1 - s0[c][o]) * 255

    split_enlarged = []
    finx, finy = newx * ES, newy * ES
    for i in range(n):
        split_enlarged.append(np.zeros((finx, finy, 3), np.uint8))
        split_enlarged[i] = cv2.resize(split[i], (finx, finy), interpolation=cv2.INTER_NEAREST)

    for i in range(n):
        cv2.imwrite(ADDRESS+"split"+str(i)+".png", split_enlarged[i])

    return split_enlarged


def retrieveSplit(key, carry, x, y):

    result = cv2.bitwise_xor(key, carry)
    xc, yc = result.shape[0:2]
    black = np.zeros((xc, yc, 3), np.uint8)
    black.fill(255)
    result = cv2.bitwise_xor(result, black)
    result = result[xc - x - 15 * ES: xc - 15 * ES, yc - y - 15 * ES: yc - 15 * ES]
    return result


def mergeSplit(split, finx, finy, d, alpha, lenm):
    merge = np.zeros((finx, finy, 3), np.uint8)
    # 图像合并
    for i in range(len(split)):
        if i == 0:
            merge = split[i]
        else:
            for si in range(finx):
                for sj in range(finy):
                    for sk in range(3):
                        if merge[si, sj, sk] == split[i][si, sj, sk] and merge[si, sj, sk] == 255:
                            merge[si, sj, sk] = 255
                        else:
                            merge[si, sj, sk] = 0
    cv2.imwrite(ADDRESS + "merge.png", merge)
    lastx, lasty = finx // lenm, finy // lenm
    qr = decode(merge, lastx, lasty, lenm, d, alpha)
    cv2.imwrite(ADDRESS+"res.png", qr)
    return qr


# 根据合并的图像还原
def decode(merge, x, y, m, d, alpha):
    qr = np.zeros((x, y, 3), np.uint8)
    for ix in range(x):
        for jy in range(y):
            count = 0
            for om in range(m*m):
                for ok in range(3):
                    count += int((255 - merge[m*ix + floor(om/m), m*jy + om % m, ok]) / 255)
            count = count / 3
            if count >= d:
                qr[ix, jy] = 0
            elif count < d:
                qr[ix, jy] = 255
    return qr


def validate(result, skhash):
    xx, yy = result.shape[0:2]
    result = cv2.resize(result, (4*xx, 4*yy))
    result = Image.fromarray(cv2.cvtColor(result, cv2.COLOR_BGR2RGB))
    barcodes = pyzbar.decode(result)
    if barcodes:
        validateSK = barcodes[0].data.decode("utf-8")
        valid = hashlib.sha256()
        valid.update(validateSK.encode("utf8"))
        validmd5 = valid.hexdigest()
        if skhash == validmd5:
            flag = 1
            return validateSK, flag
        else:
            flag = 0
            return "", flag
    else:
        flag = -1
        return "", flag


def apiFirst(msg, k, n, carriermsg, logo, boxsize, currecy, wallet):
    secret, x, y = qrcodeGenerate(msg)
    s0, s1, m = makes(k, n)
    split = generateSplit(s0, s1, m, x, y, k, n, secret)
    retx, rety = split[0].shape[0:2]

    # coefficients that needs to be saved
    d = m - n + k
    alpha = (n / 2) / m
    lenm = int(sqrt(m))

    carrier_list = []
    key_list = []
    # transfer_list = []
    for i in range(n):
        img = carrierGenerate(carriermsg[i], boxsize)
        img.save(CARRIERPATH+str(i)+".png")
        img = cv2.imread(CARRIERPATH+str(i)+".png")
        carrier_list.append(img)
        carrier_list[i] = embedding(logo, carrier_list[i])
        key_list.append(alter(carrier_list[i], split[i]))
        l, w = key_list[i].shape[0:2]
        l, w = l//boxsize, w//boxsize
        key_list[i] = cv2.resize(key_list[i], (l, w))
        key_list[i] = cv2.cvtColor(key_list[i], cv2.COLOR_BGR2GRAY)
        _, key_list[i] = cv2.threshold(key_list[i], 125, 1, cv2.THRESH_BINARY)
        # cv2.imwrite(KEYPATH + str(i) + ".png", key_list[i])
        # gray = cv2.cvtColor(key_list[i], cv2.COLOR_BGR2GRAY)
        # transfer_list.append(cv2.adaptiveThreshold(~gray, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, 35, -5))
    ax0 = n
    ax1, ax2, ax3 = carrier_list[0].shape[0:3]
    carrier_store = np.zeros((ax0, ax1, ax2, ax3), np.uint8)
    for i in range(n):
        carrier_store[i] = carrier_list[i]

    return key_list, carrier_list, retx, rety, d, alpha, lenm


def apiSecond(skhash, split_no, mat, carrier, x, y, d, alpha, lenm):
    split = []
    for i in split_no:
        mat[i] = mat[i] * 255
        mat[i] = cv2.cvtColor(mat[i], cv2.COLOR_GRAY2BGR)
        res = retrieveSplit(mat[i], carrier[i], x, y)
        split.append(res)
    qr = mergeSplit(split, x, y, d, alpha, lenm)
    return validate(qr, skhash)

    # TODO: cur&wallet to corresponding logo
    # TODO: save the hash of sk in database


def api1(msg, k, n, devicemsg, currencymsg):
    cmsg = ["d1", "d2", "d3"]
    k = int(k)
    n = int(n)
    data_matrix, carrier_matrix, length, width, c1, c2, c3 = apiFirst(msg, k, n, cmsg, BTCLOGO, ES, "BTC", 1)
    return data_matrix, carrier_matrix, length, width, c1, c2, c3


def api2(skhash, split_no, data_matrix, carrier_matrix, length, width, c1, c2, c3):
    status = apiSecond(skhash, split_no, data_matrix, carrier_matrix, length, width, c1, c2, c3)
    return status


def carryStore(carry):
    enc = cv2.imencode(".png", carry)[1]
    data = np.array(enc)
    str_store = data.dumps()
    return str_store


def carryFetch(str_store):
    arr = pickle.loads(str_store)
    img_decode = cv2.imdecode(arr, cv2.IMREAD_COLOR)
    img_decode.astype(np.uint8)
    return img_decode
