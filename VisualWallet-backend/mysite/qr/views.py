from django.http import HttpResponseRedirect, HttpResponse, FileResponse, Http404
from django.shortcuts import get_object_or_404, render
from django.urls import reverse
from django.views import generic
from .models import SKInfo
import hashlib
from qr.kn.shadow_gen_kn import api1, api1_2, api2, api3, api3_2, api4, carryStore, carryFetch
import json
import numpy as np
import time
import datetime
import cv2
import os
import stat
from qr.kn.global_var import *


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
            fixed_num = skinfo['fixed_num']
            android = skinfo['android_id']
            cur = skinfo['curType']
            seed = skinfo['seed']
            needAudio = int(skinfo['needAudio'])
            res, carrier, length, width, c1, c2, c3, splithash, audio_clip = api1(sk, k, n, fixed_num, android, cur, seed)
            for i in range(len(res)):
                res[i] = res[i].tolist()
            idx = SKInfo(coeK=k, coeN=n, fixed=fixed_num, length=length, width=width, c1=c1, c2=c2, c3=c3, date=seed)
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
            sha = hashlib.sha256()
            sha.update(sk.encode("utf8"))
            skmd5 = sha.hexdigest()
            idx.secretKeyHash = skmd5
            idx.save()
            index = idx.id
            if needAudio == 1:
                name = skinfo['audioName']
                pos = skinfo['type']
                api1_2(audio_clip, name, index, pos)
            res = {'id': index, 'split': res}
            res = json.dumps(res)
        else:
            res = "Wrong Request Flag!"
        return HttpResponse(res)
    else:
        return HttpResponse("Generate Request Method Error")


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
            isAudio = int(info['isAudio'])
            if isAudio == 0:
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
            if isAudio == 0:
                result = api3(skhash, data_matrix, carrier, length, width, coeK, coeN)
            elif isAudio == 1:
                type = info['type']
                name = 'd_'+str(index)+type
                result = api3_2(skhash, name, carrier, length, width, coeK, coeN)
        else:
            result = -1
        res = {"id": index, "flag": result}
        res = json.dumps(res)
        return HttpResponse(res)
    else:
        return HttpResponse("Detect Request Method Error")


def validate(request):
    if request.method == 'POST':
        postBody = request.body
        skinfo = json.loads(postBody.decode('utf-8'))
        flag = skinfo['reqFlag']
        if flag == "validQR":
            info = json.loads(postBody.decode('utf-8'))
            index = info['id']
            splitNo = info['index']
            data_matrix = info['keys']
            hasAudio = int(info["hasAudio"])
            pos = info["type"]

            for i in range(len(data_matrix)):
                data_matrix[i] = np.array(data_matrix[i], dtype=np.uint8)
            carrier_matrix = []

            target = SKInfo.objects.get(pk=index)

            skhash = target.secretKeyHash
            length = target.length
            width = target.width
            coeK = target.coeK
            c1 = target.c1
            c2 = target.c2
            c3 = target.c3
            coeN = target.coeN
            fixed = target.fixed
            date_time = target.date
            if coeN >= 2:
                carrier_matrix.append(carryFetch(target.carry0))
                carrier_matrix.append(carryFetch(target.carry1))
                if coeN >= 3:
                    carrier_matrix.append(carryFetch(target.carry2))
                    if coeN >= 4:
                        carrier_matrix.append(carryFetch(target.carry3))
                        if coeN == 5:
                            carrier_matrix.append(carryFetch(target.carry4))
            # start2 = time.time()
            if hasAudio == 1:
                audioname = "d_" + str(index) + pos
            else:
                audioname = ""
            sk, text = api2(skhash, splitNo, data_matrix, carrier_matrix, length, width, c1, c2, c3, coeK, coeN, fixed, date_time, audioname)

            res = {"secretKey": sk, "flag": text}
            res = json.dumps(res)

        else:
            res = "Wrong Request Flag!"
        return HttpResponse(res)
    else:
        return HttpResponse("Validate Request Method Error")


def update(request):
    if request.method == 'POST':  # 当提交表单时
        # 判断是否传参
        # start = time.time()
        postBody = request.body
        skinfo = json.loads(postBody.decode('utf-8'))
        flag = skinfo['reqFlag']
        if flag == "updateQR":
            info = json.loads(postBody.decode('utf-8'))
            index = info['id']
            sk = skinfo['secretKey']
            android = skinfo['android_id']
            seed = skinfo['seed']
            # currency = skinfo['curType']
            currency = "1111"
            carrier_matrix = []
            target = SKInfo.objects.get(pk=index)
            skhash = target.secretKeyHash
            coeK = target.coeK
            coeN = target.coeN
            fixed = target.fixed
            if coeN >= 2:
                carrier_matrix.append(carryFetch(target.carry0))
                carrier_matrix.append(carryFetch(target.carry1))
                if coeN >= 3:
                    carrier_matrix.append(carryFetch(target.carry2))
                    if coeN >= 4:
                        carrier_matrix.append(carryFetch(target.carry3))
                        if coeN == 5:
                            carrier_matrix.append(carryFetch(target.carry4))
            flag, update_res, splithash = api4(sk, coeK, coeN, fixed, skhash, carrier_matrix, android, seed, currency)
            if flag == 1:
                target.date = seed
                hashlist = [target.hash0, target.hash1, target.hash2, target.hash3, target.hash4]
                for i in range(fixed, coeN):
                    hashlist[i] = splithash[i]
                target.hash0 = hashlist[0]
                target.hash1 = hashlist[1]
                target.hash2 = hashlist[2]
                target.hash3 = hashlist[3]
                target.hash4 = hashlist[4]
                target.save()
            else:
                update_res = []
            for i in range(len(update_res)):
                update_res[i] = update_res[i].tolist()
            res = {"flag": flag, "updated": update_res}
            res = json.dumps(res)
        else:
            res = "Wrong Request Flag!"
        return HttpResponse(res)
    else:
        return HttpResponse("Update Request Method Error")


def upload(request):
    if request.method == 'POST':
        flag = request.POST.get('reqFlag', None)
        if flag != 'fileUpload':
            return HttpResponse("Upload Flag Error")
        file = request.FILES.get("file", None)
        postfix = request.POST.get('type', None)
        mode = int(request.POST.get('mode', None))
        index_num = request.POST.get('id', None)
        if mode == 0:
            if not file:
                return HttpResponse("no files for upload!")
            file_name = datetime.datetime.now().strftime('%Y-%m-%d-%H-%M-%S-%f')

        elif mode == 1:
            file_name = "d_" + index_num
        elif mode == 2:
            file_name = "v_" + index_num
        else:
            return HttpResponse("Upload Mode Error")
        file_name_pos = file_name + postfix
        dest = open(os.path.join(AUDIOPATH, file_name_pos), "wb+")
        for chunk in file.chunks():
            dest.write(chunk)
        os.chmod(AUDIOPATH + file_name_pos, stat.S_IRWXU | stat.S_IRWXG | stat.S_IRWXO)
        dest.close()
        res = {"file_name": file_name}
        res = json.dumps(res)
        return HttpResponse(res)
    else:
        return HttpResponse("Upload Request Method Error")


def download(request):
    if request.method == 'POST':
        body = request.body
        sheet = json.loads(body.decode('utf-8'))
        filename = sheet['id']
        pos = sheet['type']
        filename = 'g_' + str(filename) + pos
        file = open(os.path.join(AUDIOPATH, filename), "rb")
        response = FileResponse(file)

        return response
    else:
        return HttpResponse("Download Request Method Error")


def transact(request):
    if request.method == 'POST':
        body = request.body
        sheet = json.loads(body.decode('utf-8'))
        flag = sheet['flag']
        if flag != "testTransact":
            res = {"flag": 0, "content": ""}
        else:
            send_tx = "bitcoin-cli -regtest sendtoaddress bcrt1ql5jtgq74u5kr20hltj365sdz2vzqfsvf5gwplk 10 \"bitcoin transact test\""
            confirm_tx = "bitcoin-cli -regtest generate 1"
            get_tx = "bitcoin-cli -regtest listreceivedbyaddress"
            os.system(send_tx)
            os.system(confirm_tx)
            result = os.popen(get_tx)
            res = {"flag": 1, "content": result}
        res = json.dumps(res)
        return HttpResponse(res)
    else:
        return HttpResponse("transact Request Method Error")
