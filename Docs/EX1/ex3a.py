# 学号：1810546 姓名：袁嘉蔚
# 此练习作用是生成一个满足线性方程组的解（x，y）的交易
# x+y=1810 x-y=546 (满足学号:1810546）
from sys import exit
from bitcoin.core.script import *

from utils import *
from config import my_private_key, my_public_key, my_address, faucet_address
from ex1 import send_from_P2PKH_transaction


######################################################################
#  Complete the scriptPubKey implementation for Exercise 2
#   Assume we input the correct answer: x=1178, y=632.
#   STACK                       SCRIPT
#   x,y                         OP_2DUP,OP_ADD,1810,OP_EQUALVERIFY,OP_SUB,546,OP_EQUAL
#   x,y,x,y                     OP_ADD,1810,OP_EQUALVERIFY,OP_SUB,546,OP_EQUAL
#   x,y,x+y                     1810,OP_EQUALVERIFY,OP_SUB,546,OP_EQUAL
#   x,y,x+y,1810                OP_EQUALVERIFY,OP_SUB,546,OP_EQUAL
#   x,y                         OP_SUB,546,OP_EQUAL
#   x-y                         546,OP_EQUAL
#   x-y,546                     OP_EQUAL
#   true                        Empty.
ex3a_txout_scriptPubKey = [OP_2DUP,OP_ADD,1810,OP_EQUALVERIFY,OP_SUB,546,OP_EQUAL]
######################################################################

if __name__ == '__main__':
    ######################################################################
    #  set these parameters correctly
    amount_to_send = 0.00089044
    txid_to_spend = (
        'e06759225a95de31ca690fdc25839c1f88f0e388dcde316381c637b73d4b17d9')
    utxo_index = 4
    ######################################################################

    response = send_from_P2PKH_transaction(
        amount_to_send, txid_to_spend, utxo_index,
        ex3a_txout_scriptPubKey)
    print(response.status_code, response.reason)
    print(response.text)
# txid: d3b6e7ab050a4585761b4f7b90d6cf5fcb4be8f3de4763638bc44fd57f5bdd43