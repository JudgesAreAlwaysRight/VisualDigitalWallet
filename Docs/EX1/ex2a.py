# 学号1：1810546 姓名1：袁嘉蔚  学号2：1813560 姓名2：阮国昊
# public_key=b'\x03\xc0\x1d|\x14\xe53\x8fvc\x92\xe6\x88\x9d&\xdauE\x8f\n\xb8\xe5\x8c\xcar0\x80Wl\x8b\x1b\x99\x95'
# 此python文件作用是进行一个四方签名的多签名交易，本次实验我们使用以上公钥作为银行的公钥。
from sys import exit
from bitcoin.core.script import *

from utils import *
from bitcoin.wallet import CBitcoinSecret, P2PKHBitcoinAddress
from bitcoin.core.script import *
from config import my_private_key, my_public_key, my_address, faucet_address
from ex1 import send_from_P2PKH_transaction

# 三方客户的私钥和公钥
cust1_private_key = CBitcoinSecret(
    'cUWBLX3MED3QZ6qyyqEqbBPFSpDtNEXjeyNgcye1B2gC4PBYGPHD')
cust1_public_key = cust1_private_key.pub
cust2_private_key = CBitcoinSecret(
    'cVujYR6WZpDVE3LqytdHPUTR9CmWXSbm8wkearo2kYV2ZC448WzX')
cust2_public_key = cust2_private_key.pub
cust3_private_key = CBitcoinSecret(
    'cRLhZKmE4drYTzd6QY2rd3ZbQLkTgzniJUG5Qm1YgMTdDR26CdiP')
cust3_public_key = cust3_private_key.pub


######################################################################
# Accomplished: Complete the scriptPubKey implementation for Exercise 3

# You can assume the role of the bank for the purposes of this problem
# and use my_public_key and my_private_key in lieu of bank_public_key and
# bank_private_key.
# 脚本运行逻辑：需要银行和三方客户中任意一方用户的签名验证才能授权交易（1-3）
# 对于（m-n）的多方交易，脚本形式为 OP_M, pubkey_1, ..., pubkey_N, OP_N, OPCHECKMULTISIG
# 因此可以进行如下顺序验证：
# 1.验证银行签名，若不成功返回fail
# 2.验证三方客户中任意一方的签名，若是真压入True，若假压入False
# 在验证银行签名时需要加入VERIFY, 防止栈中有两个True值，而实际上只需要一个。
ex2a_txout_scriptPubKey = [my_public_key, OP_CHECKSIGVERIFY,
                           OP_1, cust1_public_key, cust2_public_key, cust3_public_key, OP_3, OP_CHECKMULTISIG]
######################################################################

if __name__ == '__main__':
    ######################################################################
    # set these parameters correctly
    amount_to_send = 0.00089044
    txid_to_spend = (
        'e06759225a95de31ca690fdc25839c1f88f0e388dcde316381c637b73d4b17d9')
    utxo_index = 0
    ######################################################################

    response = send_from_P2PKH_transaction(
        amount_to_send, txid_to_spend, utxo_index,
        ex2a_txout_scriptPubKey)
    print(response.status_code, response.reason)
    print(response.text)
    
# txid：ab8f8e1699e343e686bd595f7ed5ac6d804bf6e1044a97da08eda3470aee975a