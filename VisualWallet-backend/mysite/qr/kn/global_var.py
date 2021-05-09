ES = 1  # enlarge_scale: 分存图放大倍数
PATHLOCALE = r"D:/NKU/junior2/CISCN/VisualDigitalWallet/VisualWallet-backend/mysite/qr/kn/imgs/"
PATHREMOTE = r"/data/django/mysite_git/VisualWallet-backend/mysite/qr/kn/imgs/"
REMOTE = 0
PATH = ""
if REMOTE:
    PATH = PATHREMOTE
else:
    PATH = PATHLOCALE
TARGET = PATH + "test.png"
CARRIERPATH = PATH + "carrier"
KEYPATH = PATH + "key"
RETSPLITPATH = PATH + "ret"
ADDRESS = PATH + ""
BTCLOGO = PATH + "template/btc.png"
ETHLOGO = PATH + "template/eth.png"
ETHDOTLOGO = PATH + "template/eth_dot.png"
