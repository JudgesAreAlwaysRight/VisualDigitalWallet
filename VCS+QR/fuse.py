import cv2
import numpy as npy
import qrcode
from global_var import *
import inspect
import pyzbar.pyzbar as pyzbar
from PIL import Image


def carrierGenerate(msg, loc):
    """
        生成载体二维码
        :param msg: 二维码中的信息-对应不同设备名
        :param loc: 文件保存路径
    """
    qr_maker = qrcode.QRCode(version=25, error_correction=qrcode.constants.ERROR_CORRECT_H, box_size=4, border=0)
    qr_maker.add_data(msg)
    img = qr_maker.make_image()
    img.save(loc)


def fuse(carry, split):
    """
        将载体和shadow融合
        :param carry: 载体二维码
        :param split: 第一阶段的shadow
        :return: 载体二维码、shadow、融合图像
    """
    carrier = cv2.imread(carry)
    split01 = cv2.imread(split)
    x, y = carrier.shape[0:2]
    shadow1 = npy.zeros((x, y, 3), npy.uint8)
    shadow1.fill(255)
    xc, yc = split01.shape[0:2]
    shadow1[x - xc - 120:x - 120, y - yc - 120:y - 120] = split01
    key = cv2.bitwise_xor(carrier, shadow1)
    # 反色
    black = npy.zeros((x, y, 3), npy.uint8)
    black.fill(255)
    key = cv2.bitwise_xor(key, black)
    return carrier, split01, key


def validate(key1, key2):
    """
        将fuse的结果图像进行异或叠加
        :param key1: 融合图像1
        :param key2: 融合图像2
        :return: 最终结果二维码-可提取secret
    """
    result = cv2.bitwise_xor(key1, key2)
    x, y = result.shape[0:2]
    black = npy.zeros((x, y, 3), npy.uint8)
    black.fill(255)
    result = cv2.bitwise_xor(result, black)
    cv2.waitKey(0)
    return result


def retrieve_name(var):
    """
    Gets the name of var. Does it from the out most frame inner-wards.
    :param var: variable to get name from.
    :return: string
    """
    for fi in reversed(inspect.stack()):
        names = [var_name for var_name, var_val in fi.frame.f_locals.items() if var_val is var]
        if len(names) > 0:
            return names[0]


def showAndSave(*image):
    """
        过程中生成图像的显示和保存，保存路径在./imgs/下
        :param image: 图像名
    """
    for img in image:
        cv2.imshow(retrieve_name(img), img)
        filename = "./imgs/" + retrieve_name(img)+'.png'
        cv2.imwrite(filename, img)
    cv2.waitKey(0)


if __name__ == "__main__":
    carrierGenerate("device1", CARRIERA)
    carrierGenerate("device2", CARRIERB)
    carrier1, split1, keyA = fuse(CARRIERA, SPLITA)
    carrier2, split2, keyB = fuse(CARRIERB, SPLITB)
    res = validate(keyA, keyB)
    showAndSave(carrier1, carrier2, split1, split2, keyA, keyB, res)
