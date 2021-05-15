from django.http import HttpResponseRedirect, HttpResponse
from django.shortcuts import get_object_or_404, render
from django.urls import reverse
from django.views import generic
from .models import SKInfo
import hashlib
from qr.kn.shadow_gen_kn import api1, api2, api3, carryStore, carryFetch
import json
import numpy as np
import time
import cv2

# TODO: support the logo of users' customization


def genSplit(request):
    if request.method == 'POST':  # 当提交表单时
        # 判断是否传参
        postBody = request.body
        skinfo = json.loads(postBody.decode('utf-8'))
        flag = skinfo['reqFlag']
        if flag == "genSplit":
            startall = time.time()
            sk = skinfo['secretKey']
            k = skinfo['coeK']
            n = skinfo['coeN']
            android = skinfo['android_id']
            cur = skinfo['curType']
            startapi = time.time()
            res, carrier, length, width, c1, c2, c3, splithash = api1(sk, k, n, android, cur)
            endapi = time.time()
            for i in range(len(res)):
                res[i] = res[i].tolist()
            startdb = time.time()
            idx = SKInfo(coeK=k, coeN=n, length=length, width=width, c1=c1, c2=c2, c3=c3)
            num = len(carrier)
            if num >= 2:
                idx.carry0 = carryStore(carrier[0])
                idx.carry1 = carryStore(carrier[1])
                idx.hash0 = splithash[0]
                idx.hash1 = splithash[1]
                if num >= 3:
                    idx.carry2 = carryStore(carrier[2])
                    idx.hash2 = splithash[2]
                    if num >= 4:
                        idx.carry3 = carryStore(carrier[3])
                        idx.hash3 = splithash[3]
                        if num == 5:
                            idx.carry4 = carryStore(carrier[4])
                            idx.hash4 = splithash[4]
                        elif num >= 5:
                            print("The Maximum N is 5!")
            else:
                print("The Minimum N is 2!")
            enddb = time.time()
            sha = hashlib.sha256()
            sha.update(sk.encode("utf8"))
            skmd5 = sha.hexdigest()
            idx.secretKeyHash = skmd5
            idx.save()
            index = idx.id
            res = {'id': index, 'split': res}
            res = json.dumps(res)
            endall = time.time()
            print(endall-startall, endapi-startapi, enddb-startdb)
        else:
            res = "Wrong Request Flag!"
        return HttpResponse(res)
    else:
        return HttpResponse('transfer fail!')


def detect(request):
    if request.method == 'POST':
        postBody = request.body
        skinfo = json.loads(postBody.decode('utf-8'))
        flag = skinfo['reqFlag']
        if flag == "cheatDetect":
            info = json.loads(postBody.decode('utf-8'))
            index = info['id']
            splitNo = info['index']
            data_matrix = info['keys']
            data_matrix = np.array(data_matrix, dtype=np.uint8)
            target = SKInfo.objects.get(pk=index)
            carry_list = [target.carry0, target.carry1, target.carry2, target.carry3, target.carry4]
            hashlist = [target.hash0, target.hash1, target.hash2, target.hash3, target.hash4]
            carrier = carryFetch(carry_list[splitNo])
            skhash = hashlist[splitNo]
            length = target.length
            width = target.width
            coeK = target.coeK
            coeN = target.coeN
            res = api3(skhash, data_matrix, carrier, length, width, coeK, coeN)
        else:
            res = 0
        return HttpResponse(res)
    else:
        return HttpResponse('transfer failed!')


def validate(request):
    if request.method == 'POST':  # 当提交表单时
        # 判断是否传参
        start = time.time()
        postBody = request.body
        skinfo = json.loads(postBody.decode('utf-8'))
        flag = skinfo['reqFlag']
        if flag == "validQR":
            info = json.loads(postBody.decode('utf-8'))
            index = info['id']
            splitNo = info['index']
            data_matrix = info['keys']
            for i in range(len(data_matrix)):
                data_matrix[i] = np.array(data_matrix[i], dtype=np.uint8)
            carrier_matrix = []
            start3 = time.time()
            target = SKInfo.objects.get(pk=index)
            end3 = time.time()
            print("dbtime")
            print(end3-start3)
            skhash = target.secretKeyHash
            length = target.length
            width = target.width
            coeK = target.coeK
            c1 = target.c1
            c2 = target.c2
            c3 = target.c3
            coeN = target.coeN
            if coeN >= 2:
                carrier_matrix.append(carryFetch(target.carry0))
                carrier_matrix.append(carryFetch(target.carry1))
                if coeN >= 3:
                    carrier_matrix.append(carryFetch(target.carry2))
                    if coeN >= 4:
                        carrier_matrix.append(carryFetch(target.carry3))
                        if coeN == 5:
                            carrier_matrix.append(carryFetch(target.carry4))
            start2 = time.time()
            sk, text = api2(skhash, splitNo, data_matrix, carrier_matrix, length, width, c1, c2, c3, coeK, coeN)
            end2 = time.time()
            print("apitime")
            print(end2-start2)
            res = {"secretKey": sk, "flag": text}
            res = json.dumps(res)
            end = time.time()
            print("totaltime")
            print(end-start)
        else:
            res = "Wrong Request Flag!"
        return HttpResponse(res)
    else:
        return HttpResponse('transfer failed!')
