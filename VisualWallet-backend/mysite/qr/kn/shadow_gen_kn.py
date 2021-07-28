from math import ceil, floor, sqrt
from itertools import combinations
from scipy.special import comb
import cv2
import os
import stat
import numpy as np
import qrcode
from qr.kn.global_var import *
import hashlib
import pickle
import random
from qr.kn.audio import *
import time
import datetime
import json


def datamatGenerate(msg):
    idx = 0
    qr_img = np.zeros((16, 16, 3), np.uint8)
    for i in range(16):
        for j in range(16):
            if msg[idx] == '1':
                qr_img[i, j] = [0, 0, 0]
            else:
                qr_img[i, j] = [255, 255, 255]
            idx += 1
    length, width = qr_img.shape[0:2]
    return qr_img, length, width


def carrierGenerate(msg, k):
    version = 28
    if k == 4:
        version = 32
    elif k == 5:
        version = 32

    qr_maker = qrcode.QRCode(version=version, error_correction=qrcode.constants.ERROR_CORRECT_H, box_size=1, border=0)
    qr_maker.add_data(msg)
    img = qr_maker.make_image()
    return img


def alter(carry, split, k, n):
    if k >= 4 and n == 5:
        return split
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
    image = image[:, :, :3]
    l, w = image.shape[0:2]
    shadow = cv2.resize(image, (l*ES, w*ES), interpolation=cv2.INTER_NEAREST)
    carrier.flags.writeable = True
    carrier[15*ES:15*ES+l*ES, 15*ES:15*ES+w*ES] = shadow
    return carrier


def maskSplit(split, fixed_num, n, seed):
    obj = hashlib.sha256()
    obj.update(seed.encode("utf-8"))
    if fixed_num == n:
        splithash = []
        for i in range(n):
            splithash.append(hashlib.sha256(split[i].tobytes()).hexdigest())
        return split, splithash
    maskRef = bin(int(obj.hexdigest(), 16))[2:]
    if len(maskRef) <= 256:
        for i in range(256-len(maskRef)):
            maskRef = maskRef + "0"
    mask_img = np.zeros((16, 16, 3), np.uint8)
    for i in range(16):
        for j in range(16):
            if maskRef[i*16+j] == '1':
                mask_img[i, j] = [0, 0, 0]
            else:
                mask_img[i, j] = [255, 255, 255]
    x, y = split[0].shape[0:2]
    mask = cv2.resize(mask_img, (x, y), interpolation=cv2.INTER_NEAREST)
    for i in range(n - fixed_num):
        split[n-1-i] = cv2.bitwise_xor(mask, split[n-1-i])
    splithash = []
    for i in range(n):
        splithash.append(hashlib.sha256(split[i].tobytes()).hexdigest())
    return split, splithash


def unmaskSplit(date, split, fixed_num, n, split_no):
    obj = hashlib.sha256()
    obj.update(date.encode("utf-8"))
    if fixed_num == n:
        return split
    maskRef = bin(int(obj.hexdigest(), 16))[2:]
    if len(maskRef) <= 256:
        for i in range(256 - len(maskRef)):
            maskRef = maskRef + "0"
    mask_img = np.zeros((16, 16, 3), np.uint8)
    for i in range(16):
        for j in range(16):
            if maskRef[i * 16 + j] == '1':
                mask_img[i, j] = [0, 0, 0]
            else:
                mask_img[i, j] = [255, 255, 255]
    x, y = split[0].shape[0:2]
    mask = cv2.resize(mask_img, (x, y), interpolation=cv2.INTER_NEAREST)
    for i in range(len(split)):
        if split_no[i] >= fixed_num:
            split[i] = cv2.bitwise_xor(mask, split[i])
    return split


def genQueue(aID, n):
    all = []
    q = []
    qlen = len(aID)
    random.seed(6666)
    for i in range(qlen):
        q.append(ord(aID[i]) % n)
    all.append(q)
    for i in range(n-1):
        temp = []
        j = len(temp)
        while j < qlen:
            a = random.randint(0, n-1)
            join = True
            for x in range(len(all)):
                if a == all[x][j]:
                    join = False
                    break
            if join:
                temp.append(a)
            j = len(temp)
        all.append(temp)
    return all


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
    while p <= floor(k/2):
        if p % 2 == 0:
            t0 = comb((n - k + floor(k/2) - p), (floor(k/2) - p))
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
            t1 = comb((p - floor(k / 2) - 1), (p - (n - k) - floor(k / 2) - 1))
        if p >= 1:
            if (k >= 1) and (k < n - p + 1):
                for x in range(k):
                    d0 += comb(n-x, p-1)* t0
                    d1 += comb(n-x, p-1)* t1
            elif k >= n-p+1:
                    d0 += comb(n, p) * t0
                    d1 += comb(n, p) * t1
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

    return s0, s1, goal, d0, d1


def generateSplit(s0, s1, m, x, y, k, n, bin, queue):
    lenm = int(sqrt(m))
    # 每个像素扩充成lenm*lenm
    newx = int(x*lenm)
    newy = int(y*lenm)

    # 合并图和分存图
    split = []
    for i in range(n):
        split.append(np.zeros((newx, newy, 3), np.uint8))
    # randlistblack = random.sample(range(0, m), m)
    # randlistwhite = random.sample(range(0, m), m)
    # 这个part有优化空间，可以搞成并行的
    # 原图的横轴
    pixel = 0
    for i in range(x):
        # 原图的纵轴
        for j in range(y):
            if not bin[i, j].all():
                # 每一张分存图
                for c in range(n):
                    for o in range(len(s0[0])):
                        if pixel < n * 8:
                            split[c][lenm * i + floor(o / lenm), lenm * j + o % lenm] = \
                                (1 - s1[queue[(-c + n + pixel // 8) % n][pixel % 8]][o]) * 255
                        else:
                            split[c][lenm * i + floor(o / lenm), lenm * j + o % lenm] = \
                                (1 - s1[c][o]) * 255
            if bin[i, j].all():
                for c in range(n):
                    for o in range(len(s0[0])):
                        if pixel < n * 8:
                            split[c][lenm * i + floor(o / lenm), lenm * j + o % lenm] = \
                                (1 - s0[queue[(-c + n + pixel // 8) % n][pixel % 8]][o]) * 255
                        else:
                            split[c][lenm * i + floor(o / lenm), lenm * j + o % lenm] = \
                                (1 - s0[c][o]) * 255
            pixel += 1
    split_enlarged = []
    finx, finy = newx * ES, newy * ES
    for i in range(n):
        split_enlarged.append(np.zeros((finx, finy, 3), np.uint8))
        split_enlarged[i] = cv2.resize(split[i], (finx, finy), interpolation=cv2.INTER_NEAREST)
    return split_enlarged


def retrieveSplit(key, carry, x, y, K, N):
    if N == 5 and K >=4:
        return key
    result = cv2.bitwise_xor(key, carry)
    xc, yc = result.shape[0:2]
    black = np.zeros((xc, yc, 3), np.uint8)
    black.fill(255)
    result = cv2.bitwise_xor(result, black)
    result = result[xc - x - 15 * ES: xc - 15 * ES, yc - y - 15 * ES: yc - 15 * ES]
    return result


def splitCheck(split, sk):
    splithash = hashlib.sha256(split.tobytes()).hexdigest()
    if splithash == sk:
        return 1
    else:
        return 0


def mergeSplit(split, finx, finy, d0, d1, lenm):
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
    # cv2.imwrite(ADDRESS + "merge.png", merge)
    lastx, lasty = finx // lenm, finy // lenm
    qr = decode(merge, lastx, lasty, lenm, d0, d1)
    # cv2.imwrite(ADDRESS+"res.png", qr)
    return qr


# 根据合并的图像还原
def decode(merge, x, y, m, d0, d1):
    qr = np.zeros((x, y, 3), np.uint8)
    for ix in range(x):
        for jy in range(y):
            count = 0
            for om in range(m*m):
                for ok in range(3):
                    count += int((255 - merge[m*ix + floor(om/m), m*jy + om % m, ok]) / 255)
            count = count / 3
            if count >= d1:
                qr[ix, jy] = 0
            elif count <= d0:
                qr[ix, jy] = 255
    return qr


def validate(result, skhash):
    xx, yy = result.shape[0:2]
    sk = ""
    for i in range(xx):
        for j in range(yy):
            if (result[i, j] == [0, 0, 0]).all():
                sk = sk + "1"
            elif (result[i, j] == [255, 255, 255]).all():
                sk = sk + "0"
            else:
                sk = sk
    valid = hashlib.sha256()
    valid.update(sk.encode("utf8"))
    validmd5 = valid.hexdigest()
    if skhash == validmd5:
        flag = 1
        return sk, flag
    else:
        flag = 0
        sk = ""
        return sk, flag


def apiFirst(msg, k, n, fixed, carriermsg, android_id, logo, boxsize, seed):
    secret, x, y = datamatGenerate(msg)
    s0, s1, m, d0, d1 = makes(k, n)
    queue = genQueue(android_id, n)
    split = generateSplit(s0, s1, m, x, y, k, n, secret, queue)
    split, splithash = maskSplit(split, fixed, n, seed)
    retx, rety = split[0].shape[0:2]
    d = d0
    alpha = d1
    lenm = int(sqrt(m))
    carrier_list = []
    key_list = []
    # transfer_list = []
    for i in range(n):
        img = carrierGenerate(carriermsg[i], k)
        img.save(CARRIERPATH+str(i)+".png")
        os.chmod(CARRIERPATH+str(i)+".png", stat.S_IRWXU | stat.S_IRWXG | stat.S_IRWXO)
        img = cv2.imread(CARRIERPATH+str(i)+".png")
        carrier_list.append(img)
        carrier_list[i] = embedding(logo, carrier_list[i])
        key_list.append(alter(carrier_list[i], split[i], k, n))
        l, w = key_list[i].shape[0:2]
        l, w = l//boxsize, w//boxsize
        key_list[i] = cv2.resize(key_list[i], (l, w))
        key_list_store = cv2.resize(key_list[i], (8 * l, 8 * w), interpolation=cv2.INTER_NEAREST)
        cv2.imwrite(KEYPATH + str(i) + ".png", key_list_store)
        key_list[i] = cv2.cvtColor(key_list[i], cv2.COLOR_BGR2GRAY)
        _, key_list[i] = cv2.threshold(key_list[i], 125, 1, cv2.THRESH_BINARY)
        # mask(key_list)

    ax0 = n
    ax1, ax2, ax3 = carrier_list[0].shape[0:3]
    carrier_store = np.zeros((ax0, ax1, ax2, ax3), np.uint8)
    for i in range(n):
        carrier_store[i] = carrier_list[i]

    test = cv2.cvtColor(split[0], cv2.COLOR_BGR2GRAY)
    _, test = cv2.threshold(test, 125, 1, cv2.THRESH_BINARY)

    return key_list, carrier_list, retx, rety, d, alpha, lenm, splithash, test


def apiSecond(skhash, split_no, mat, carrier, x, y, d0, d1, lenm, coeK, coeN, fixed, date_time, audio):
    split = []
    if audio != "":
        audio_name = AUDIOPATH + audio
        split0 = wmget(audio_name, x, y)
        split0 = split0 * 255
        split0 = cv2.cvtColor(split0, cv2.COLOR_GRAY2BGR)
        split.append(split0)

    for i in range(len(split_no)):
        mat[i] = mat[i] * 255
        mat[i] = cv2.cvtColor(mat[i], cv2.COLOR_GRAY2BGR)
        res = retrieveSplit(mat[i], carrier[split_no[i]], x, y, coeK, coeN)
        split.append(res)
    if audio != "":
        split_no.insert(0, 0)
    split = unmaskSplit(date_time, split, fixed, coeN, split_no)
    qr = mergeSplit(split, x, y, d0, d1, lenm)
    return validate(qr, skhash)


def api1(msg, k, n, fixed, devicemsg, currencymsg, seed):
    cmsg = []
    for i in range(5):
        msg1 = "Device ID: " + "MyDeviceId" + str(i) + "\n"
        msg2 = "Type: " + currencymsg + "\n"
        msg3 = "Index: " + str(i) + "\n"
        cmsg.append(msg1+msg2+msg3)
    k = int(k)
    n = int(n)
    if currencymsg == "BTC":
        logo = BTCLOGO
    elif currencymsg == "ETH":
        logo = ETHLOGO
    elif currencymsg == "LTC":
        logo = LTCLOGO
    elif currencymsg == "XRP":
        logo = XRPLOGO
    elif currencymsg == "USDT":
        logo = USDTLOGO
    elif currencymsg == "CNY":
        logo = CNYLOGO
    elif currencymsg == "USD":
        logo = USDLOGO
    else:
        logo = BTCLOGO
    data_matrix, carrier_matrix, length, width, c1, c2, c3, splithash, audio = apiFirst(msg, k, n, fixed, cmsg, devicemsg, logo, ES, seed)
    return data_matrix, carrier_matrix, length, width, c1, c2, c3, splithash, audio


def api1_2(split, audio_name, index, pos):
    src = AUDIOPATH + audio_name + pos
    dest = AUDIOPATH + "g_" + str(index) + pos
    wmadd(src, split, dest)


def api2(skhash, split_no, data_matrix, carrier_matrix, length, width, c1, c2, c3, coeK, coeN, fixed, date_time, audio):
    status = apiSecond(skhash, split_no, data_matrix, carrier_matrix, length, width, c1, c2, c3, coeK, coeN, fixed, date_time, audio)
    return status


def api3(splithash, split, carrier, length, width, coeK, coeN):
    split = split*255
    split = cv2.cvtColor(split, cv2.COLOR_GRAY2BGR)

    res = retrieveSplit(split, carrier, length, width, coeK, coeN)
    res2 = splitCheck(res, splithash)

    return res2


def api3_2(splithash, audio_name, carrier, length, width, coeK, coeN):
    audio_name = AUDIOPATH + audio_name
    split = wmget(audio_name, length, width)
    split = split * 255
    split = cv2.cvtColor(split, cv2.COLOR_GRAY2BGR)
    # res = retrieveSplit(split, carrier, length, width, coeK, coeN)
    res2 = splitCheck(split, splithash)

    return res2


def api4(msg, k, n, fixed, skhash, carrier_matrix, android_id, seed, currencymsg):
    if skhash != hashlib.sha256(msg.encode("utf-8")).hexdigest():
        return -1, [], "", []
    elif fixed == n:
        return 0, [], "", []
    else:
        secret, x, y = datamatGenerate(msg)
        queue = genQueue(android_id, n)
        s0, s1, m, d0, d1 = makes(k, n)
        split = generateSplit(s0, s1, m, x, y, k, n, secret, queue)
        split, splithash = maskSplit(split, fixed, n, seed)
        update_list = []
        index = 0
        for i in range(fixed, n):
            update_list.append(alter(carrier_matrix[i], split[i], k, n))
            update_list[index] = cv2.cvtColor(update_list[index], cv2.COLOR_BGR2GRAY)
            _, update_list[index] = cv2.threshold(update_list[index], 125, 1, cv2.THRESH_BINARY)
            index += 1
        return 1, update_list, splithash


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
