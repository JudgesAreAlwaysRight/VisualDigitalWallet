import cv2
import numpy as npy
import qrcode
from global_var import *


# 生成二维码
def qrcodeGenerate():
    qr_maker = qrcode.QRCode(version=1, error_correction=qrcode.constants.ERROR_CORRECT_M, box_size=BOXSIZE, border=0)
    qr_maker.add_data("secret")
    qr_maker.make(fit=True)
    img = qr_maker.make_image()
    img.save(r"./imgs/test.png")


# 图片预处理
def preprocess(path):
    img = cv2.imread(path, 1)
    qr = cv2.QRCodeDetector()
    points = qr.detect(img)
    print(points)
    # 原大小赋值
    x, y = img.shape[0:2]
    # 大小处理
    image = cv2.resize(img, (int(y / SCALESIZE), int(x / SCALESIZE)))
    # 灰度化处理
    # gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    # 二值化处理
    binary = image
    # binary = cv2.adaptiveThreshold(~gray, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, 35, -5)
    return x, y, binary


# 创建空白图
def genBlankImg(x, y):
    orix = int(x / SCALESIZE)
    oriy = int(y / SCALESIZE)

    newx = int(x / SCALESIZE) * 2
    newy = int(y / SCALESIZE) * 2

    split1 = npy.zeros((newx, newy, 3), npy.uint8)  # 空白子图1
    split2 = npy.zeros((newx, newy, 3), npy.uint8)  # 空白子图2
    merged = npy.zeros((newx, newy, 3), npy.uint8)   # 合并图
    return orix, oriy, split1, split2, newx, newy, merged


# 分存细节
def pixelConverge(split1, split2, binary, i, j):
    step = int(STEP)
    for k in range(step):
        for q in range(step):
            split1[2 * i + k, 2 * j + q] = 0
            split1[2 * i + k + step, 2 * j + q] = 255
            split1[2 * i + k, 2 * j + q + step] = 255
            split1[2 * i + k + step, 2 * j + q + step] = 0
            # 黑
            if binary[i, j, 0] == 0:
                split2[2 * i + k, 2 * j + q] = 255
                split2[2 * i + k + step, 2 * j + q] = 0
                split2[2 * i + k, 2 * j + q + step] = 0
                split2[2 * i + k + step, 2 * j + q + step] = 255
            # 白
            elif binary[i, j, 0] == 255:
                split2[2 * i + k, 2 * j + q] = 0
                split2[2 * i + k + step, 2 * j + q] = 255
                split2[2 * i + k, 2 * j + q + step] = 255
                split2[2 * i + k + step, 2 * j + q + step] = 0


# 分存
def expand(orix, oriy, binary, split1, split2):
    for i in range(0, orix, 4):
        for j in range(0, oriy, 4):
            pixelConverge(split1, split2, binary, i, j)
    # split1 = cv2.resize(split1, (oriy, orix))
    # split2 = cv2.resize(split2, (oriy, orix))
    cv2.imshow("bin: ", binary)
    cv2.imshow("split1: ", split1)
    cv2.imshow("split2: ", split2)
    cv2.imwrite(SPLITA, split1)
    cv2.imwrite(SPLITB, split2)
    return split1, split2


# 合并
def merge(split1, split2, newx, newy):
    # 利用异或进行合并
    merged = cv2.bitwise_xor(split1, split2)
    # 异或合并会反色，此处进行二次反色以还原图像
    for i in range(newx):
        for j in range(newy):
            for k in range(3):
                merged[i, j, k] = 255 - merged[i, j, k]
    cv2.imshow("result: ", merged)
    cv2.waitKey(0)


if __name__ == '__main__':
    qrcodeGenerate()
    length, width, binary_img = preprocess('./imgs/test.png')
    oril, oriw, split01, split02, newl, neww, merged0 = genBlankImg(length, width)
    split01, split02 = expand(oril, oriw, binary_img, split01, split02)
    merge(split01, split02, newl, neww)
