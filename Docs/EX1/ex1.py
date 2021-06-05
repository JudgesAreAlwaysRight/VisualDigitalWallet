######################################################################
#TODO: 学号1：1810546 姓名1：袁嘉蔚  学号2：1813560 姓名2：阮国昊
# public_key=b'\x03\xc0\x1d|\x14\xe53\x8fvc\x92\xe6\x88\x9d&\xdauE\x8f\n\xb8\xe5\x8c\xcar0\x80Wl\x8b\x1b\x99\x95'
# 采用faucet_address='mv4rnyY3Su5gjcDNzbMLKBQkBicCtHUtFB'
# 本次交易的发出地址my_address='mv4rnyY3Su5gjcDNzbMLKBQkBicCtHUtFB'
# 共进行了五笔交易，四笔送给faucet，另一笔送给组员，其中有一笔因为没有缴纳手续费而失败。
# 分成十份的txid=‘e06759225a95de31ca690fdc25839c1f88f0e388dcde316381c637b73d4b17d9’
# 具体的代码注释见下
######################################################################

from bitcoin.core.script import *

from utils import *
from config import (my_private_key, my_public_key, my_address,
                    faucet_address)


def P2PKH_scriptPubKey(address):
    ######################################################################
    # PayToPublicKeyHash transaction
    return [OP_DUP, OP_HASH160, address, OP_EQUALVERIFY, OP_CHECKSIG]
    ######################################################################


def P2PKH_scriptSig(txin, txout, txin_scriptPubKey):
    signature = create_OP_CHECKSIG_signature(txin, txout, txin_scriptPubKey,
                                             my_private_key)
    ######################################################################
    # in the PayToPublicKeyHash transaction. You may need to use variables
    # that are globally defined.
    return [signature, my_public_key]
    #TODO：按照脚本执行顺序进行说明，首先为了scriptSig生成了并返回了签名，以及公钥。
    # 然后scriptSig与scriptPubKey进行结合，形成一个<sig> <pubKey>
    # OP_DUP OP_HASH160 <pubKeyHash> OP_EQUALVERIFY OP_CHECKSIG的队列，其
    # 依次进入堆栈，首先签名和公钥进去，然后对签名进行复制（OP_DUP)和哈希（OP_HASH160），生成（pubKeyHashA）
    # ，随后与pubkey的hash进行比对是否相等（OP_EQUALVERIFY）,若想等则通过验证，
    # 剩下的<sig> <pubKey>最后验证签名（OP_CHECKSIG），若通过，返回真。
    ######################################################################


def send_from_P2PKH_transaction(amount_to_send, txid_to_spend, utxo_index,
                                txout_scriptPubKey):
    txout = create_txout(amount_to_send, txout_scriptPubKey)

    txin_scriptPubKey = P2PKH_scriptPubKey(my_address)
    txin = create_txin(txid_to_spend, utxo_index)
    txin_scriptSig = P2PKH_scriptSig(txin, txout, txin_scriptPubKey)

    new_tx = create_signed_transaction(txin, txout, txin_scriptPubKey,
                                       txin_scriptSig)

    return broadcast_transaction(new_tx)


if __name__ == '__main__':
    ######################################################################
    # TODO: set these parameters correctly
    amount_to_send = 0.00049044
    txid_to_spend = (
        'e06759225a95de31ca690fdc25839c1f88f0e388dcde316381c637b73d4b17d9')
    utxo_index = 3
    ######################################################################

    txout_scriptPubKey = P2PKH_scriptPubKey(faucet_address)
    response = send_from_P2PKH_transaction(
        amount_to_send, txid_to_spend, utxo_index, txout_scriptPubKey)
    print(response.status_code, response.reason)
    print(response.text)
   ######################################################################
   #TODO: 共进行了五笔交易，第一笔将btc分为十份，第二（失败，因为没有fee）、四、五笔为转给系统地址，第三笔为转给队友
   # split_test_coins: "e06759225a95de31ca690fdc25839c1f88f0e388dcde316381c637b73d4b17d9",
   #  To faucet_address: "95a5ce829cfdd169e985eabba70324738bf1bffcac5b1c449340b9d21c66aa88",
   # To faucet_address: "6f639ac797df02a063b2b2bc2cac22e35593d9d833a1e522d55e7e252eb30be1",
   # To my_teammate: "228a5d6cc3403f2be2d60af2c862776e72fed88880170a43c536427823b13fe0",
   # To faucet_address: "52327d3b8c9d55ef92f8e16d707b7cc67eb26c25be4a9b19b408e38612ba90eb",
  ######################################################################