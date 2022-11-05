import os

import cv2
import numpy as np

btc = cv2.imread("btc.png")
usd = cv2.imread("dollar.png")
ecny = cv2.imread("ecny.png")
eth = cv2.imread("eth.png")
ltc = cv2.imread("ltc.png")
usdt = cv2.imread("usdt.png")
xrp = cv2.imread("xrp.png")

nameList = ["btc", "usd", "ecny", "eth", "ltc", "usdt", "xrp"]

btc = btc[:, :, 0]
btc[btc > 1] = 1
np.savetxt("btc.txt", btc, "%d", ',', '},\n{')

usd = usd[:, :, 0]
usd[usd > 1] = 1
np.savetxt("usd.txt", usd, "%d", ',', '},\n{')

ecny = ecny[:, :, 0]
ecny[ecny > 1] = 1
np.savetxt("ecny.txt", ecny, "%d", ',', '},\n{')

eth = eth[:, :, 0]
eth[eth > 1] = 1
np.savetxt("eth.txt", eth, "%d", ',', '},\n{')

ltc = ltc[:, :, 0]
ltc[ltc > 1] = 1
np.savetxt("ltc.txt", ltc, "%d", ',', '},\n{')

usdt = usdt[:, :, 0]
usdt[usdt > 1] = 1
np.savetxt("usdt.txt", usdt, "%d", ',', '},\n{')

xrp = xrp[:, :, 0]
xrp[xrp > 1] = 1
np.savetxt("xrp.txt", xrp, "%d", ',', '},\n{')

for i in nameList:
    filename = i + ".txt"
    with open(filename, 'rb+') as f:
        f.seek(0)
        f.seek(-4, 2)
        f.truncate()
        f.close()

    with open(filename, 'r+') as f:
        f.seek(0)
        f.write("{{")
        f.seek(0, 2)
        f.write("}")
