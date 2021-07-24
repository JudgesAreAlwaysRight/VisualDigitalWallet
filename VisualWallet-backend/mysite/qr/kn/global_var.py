ES = 1  # enlarge_scale: 分存图放大倍数
PATHLOCALE = r"E:/CISCN/VisualDigitalWallet/VisualWallet-backend/mysite/qr/kn/imgs/"
PATHREMOTE = r"/data/django/mysite_git/VisualWallet-backend/mysite/qr/kn/imgs/"
REMOTE = 1
PATH = ""
if REMOTE:
    PATH = PATHREMOTE
else:
    PATH = PATHLOCALE
TARGET = PATH + "test.png"
CARRIERPATH = PATH + "carrier"
KEYPATH = PATH + "key"
RETSPLITPATH = PATH + "ret"
AUDIOPATH = PATH + "audios/"
ADDRESS = PATH + ""
BTCLOGO = PATH + "template/btc.png"
ETHLOGO = PATH + "template/eth.png"
ETHDOTLOGO = PATH + "template/eth_dot.png"
LTCLOGO = PATH + "template/ltc.png"
XRPLOGO = PATH + "template/xrp.png"
USDTLOGO = PATH + "template/usdt.png"
USDLOGO = PATH + "template/dollar.png"
CNYLOGO = PATH + "template/ecny.png"


KEY_TYPE_ERROR = -1
KEY_LENGTH_ERROR = -2
