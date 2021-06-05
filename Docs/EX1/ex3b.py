# 学号：1810546 姓名：袁嘉蔚
# 此练习作用是赎回上述产生的交易，且脚本需要尽量小。
# x=1178，y=632
from sys import exit
from bitcoin.core.script import *

from utils import *
from config import my_private_key, my_public_key, my_address, faucet_address
from ex1 import P2PKH_scriptPubKey
from ex3a import ex3a_txout_scriptPubKey


######################################################################
# set these parameters correctly
amount_to_send = 0.00029044
txid_to_spend = 'd3b6e7ab050a4585761b4f7b90d6cf5fcb4be8f3de4763638bc44fd57f5bdd43'
utxo_index = 0
######################################################################

txin_scriptPubKey = ex3a_txout_scriptPubKey
######################################################################
# implement the scriptSig for redeeming the transaction created
#   in  Exercise 3a.
#   极简脚本，只需要输入x，y，依照ex3a.py注释中的脚本执行流程即可进行验证。
x=1178
y=632
txin_scriptSig = [x,y]
######################################################################
txout_scriptPubKey = P2PKH_scriptPubKey(faucet_address)

response = send_from_custom_transaction(
    amount_to_send, txid_to_spend, utxo_index,
    txin_scriptPubKey, txin_scriptSig, txout_scriptPubKey)
print(response.status_code, response.reason)
print(response.text)
# txid= e6be1340dfb3bfc11d74fa44245f29d54d3bbcc07a1643a8311918cf6a61abd5