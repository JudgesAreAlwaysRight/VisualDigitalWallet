# 学号1：1810546 姓名1：袁嘉蔚  学号2：1813560 姓名2：阮国昊
# public_key=b'\x03\xc0\x1d|\x14\xe53\x8fvc\x92\xe6\x88\x9d&\xdauE\x8f\n\xb8\xe5\x8c\xcar0\x80Wl\x8b\x1b\x99\x95'
# 此python文件作用是赎回交易，将赎回的资金发给testnet提供的地址。
from sys import exit
from bitcoin.core.script import *

from utils import *
from config import my_private_key, my_public_key, my_address, faucet_address
from ex1 import P2PKH_scriptPubKey
from ex2a import (ex2a_txout_scriptPubKey, cust1_private_key, cust2_private_key,
                  cust3_private_key)


def multisig_scriptSig(txin, txout, txin_scriptPubKey):
    bank_sig = create_OP_CHECKSIG_signature(txin, txout, txin_scriptPubKey,
                                             my_private_key)
    cust1_sig = create_OP_CHECKSIG_signature(txin, txout, txin_scriptPubKey,
                                             cust1_private_key)
    cust2_sig = create_OP_CHECKSIG_signature(txin, txout, txin_scriptPubKey,
                                             cust2_private_key)
    cust3_sig = create_OP_CHECKSIG_signature(txin, txout, txin_scriptPubKey,
                                             cust3_private_key)
    ######################################################################
    # Accomplished:  Complete this script to unlock the BTC that was locked in the
    #  multisig transaction created in Exercise 2a.
    # 修改后只需要银行和第一方的签名即可赎回交易。OP_0 将空项加入栈中。
    return [OP_0, cust1_sig, bank_sig]
    ######################################################################


def send_from_multisig_transaction(amount_to_send, txid_to_spend, utxo_index,
                                   txin_scriptPubKey, txout_scriptPubKey):
    txout = create_txout(amount_to_send, txout_scriptPubKey)

    txin = create_txin(txid_to_spend, utxo_index)
    txin_scriptSig = multisig_scriptSig(txin, txout, txin_scriptPubKey)

    new_tx = create_signed_transaction(txin, txout, txin_scriptPubKey,
                                       txin_scriptSig)

    return broadcast_transaction(new_tx)

if __name__ == '__main__':
    ######################################################################
    # set these parameters correctly
    amount_to_send = 0.00029044
    txid_to_spend = 'ab8f8e1699e343e686bd595f7ed5ac6d804bf6e1044a97da08eda3470aee975a'
    utxo_index = 0
    ######################################################################

    txin_scriptPubKey = ex2a_txout_scriptPubKey
    txout_scriptPubKey = P2PKH_scriptPubKey(faucet_address)

    response = send_from_multisig_transaction(
        amount_to_send, txid_to_spend, utxo_index,
        txin_scriptPubKey, txout_scriptPubKey)
    print(response.status_code, response.reason)
    print(response.text)
    
# txid:  a7353df216da7b74d15753fbcecbfb8b38cda8ea5fbc07f2746031dfa8b7782e