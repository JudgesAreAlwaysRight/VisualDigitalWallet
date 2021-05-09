from django.http import HttpResponseRedirect, HttpResponse
from django.shortcuts import get_object_or_404, render
from django.urls import reverse
from django.views import generic
from .models import SKInfo
import hashlib
from qr.kn.shadow_gen_kn import api1, api2, carryStore, carryFetch
import json
import numpy as np
import cv2


def genSplit(request):
    if request.method == 'POST':  # 当提交表单时
        # 判断是否传参
        postBody = request.body
        skinfo = json.loads(postBody.decode('utf-8'))
        flag = skinfo['reqFlag']
        if flag == "genSplit":
            sk = skinfo['secretKey']
            k = skinfo['coeK']
            n = skinfo['coeN']
            cur = skinfo['curType']
            res, carrier, length, width, c1, c2, c3 = api1(sk, k, n, "", "")
            for i in range(len(res)):
                res[i] = res[i].tolist()
            idx = SKInfo(coeK=k, coeN=n, length=length, width=width, c1=c1, c2=c2, c3=c3)
            num = len(carrier)
            if num >= 2:
                idx.carry0 = carryStore(carrier[0])
                idx.carry1 = carryStore(carrier[1])
                if num >= 3:
                    idx.carry2 = carryStore(carrier[2])
                    if num >= 4:
                        idx.carry3 = carryStore(carrier[3])
                        if num == 5:
                            idx.carry4 = carryStore(carrier[4])
                        else:
                            print("The Maximum N is 5!")
            else:
                print("The Minimum N is 2!")
            sha = hashlib.sha256()
            sha.update(sk.encode("utf8"))
            skmd5 = sha.hexdigest()
            idx.secretKeyHash = skmd5
            idx.save()
            index = idx.id
            res = {'id': index, 'split': res}
            res = json.dumps(res)
        else:
            res = "Wrong Request Flag!"
        return HttpResponse(res)
    else:
        return HttpResponse('transfer fail!')


def validate(request):
    if request.method == 'POST':  # 当提交表单时
        # 判断是否传参
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
            target = SKInfo.objects.get(pk=index)
            skhash = target.secretKeyHash
            length = target.length
            width = target.width
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
            sk, text = api2(skhash, splitNo, data_matrix, carrier_matrix, length, width, c1, c2, c3)
            res = {"secretKey": sk, "flag": text}
            res = json.dumps(res)
        else:
            res = "Wrong Request Flag!"
        return HttpResponse(res)
    else:
        return HttpResponse('transfer failed!')
