import json
import requests
from bitcoin.core import b2x, lx, COIN, COutPoint, CMutableTxOut, CMutableTxIn, CMutableTransaction
from bitcoin.core.script import *
from bitcoin.core.scripteval import VerifyScript, SCRIPT_VERIFY_P2SH


def send_from_custom_transaction(
        amount_to_send, txid_to_spend, utxo_index,
        txin_scriptPubKey, txin_scriptSig, txout_scriptPubKey):
    txout = create_txout(amount_to_send, txout_scriptPubKey)
    txin = create_txin(txid_to_spend, utxo_index)
    new_tx = create_signed_transaction(txin, txout, txin_scriptPubKey,
                                       txin_scriptSig)
    return broadcast_transaction(new_tx)


def create_txin(txid, utxo_index):
    return CMutableTxIn(COutPoint(lx(txid), utxo_index))


def create_txout(amount, scriptPubKey):
    return CMutableTxOut(amount*COIN, CScript(scriptPubKey))


def create_OP_CHECKSIG_signature(txin, txout, txin_scriptPubKey, seckey):
    tx = CMutableTransaction([txin], [txout])
    sighash = SignatureHash(CScript(txin_scriptPubKey), tx,
                            0, SIGHASH_ALL)
    sig = seckey.sign(sighash) + bytes([SIGHASH_ALL])
    return sig


def create_signed_transaction(txin, txout, txin_scriptPubKey,
                              txin_scriptSig):
    tx = CMutableTransaction([txin], [txout])
    txin.scriptSig = CScript(txin_scriptSig)
    VerifyScript(txin.scriptSig, CScript(txin_scriptPubKey),
                 tx, 0, (SCRIPT_VERIFY_P2SH,))
    return tx


def broadcast_transaction(tx, network):
    raw_transaction = b2x(tx.serialize())
    headers = {'content-type': 'application/x-www-form-urlencoded'}
    response = ""
    if network == "mainnet":
        response = requests.post(
            'https://api.blockcypher.com/v1/btc/main/txs/push',
            headers=headers,
            data='{"tx": "%s"}' % raw_transaction
        )
    elif network == "testnet":
        response = requests.post(
            'https://api.blockcypher.com/v1/btc/test3/txs/push',
            headers=headers,
            data='{"tx": "%s"}' % raw_transaction
        )
    if not response.ok:
        raise RuntimeError("broadcast exception")
    return response.text


def get_utxo(address):
    url = "https://blockchain.info/zh-cn/unspent?active=" + address

    url = 'https://chain.api.btc.com/v3/address/' + address + '/unspent'
    response = requests.get(url=url)
    if response.status_code != 200:
        raise RuntimeError("http error: get utxo from btc.com failed")
    try:
        response_json = json.loads(response.text)
        utxo_list = response_json['data']['list']
        return utxo_list
    except Exception:
        raise RuntimeError("decode error: decode utxo failed")


def get_best_uxto_sublist(utxo_list, total_amount):
    # 余额是否充足
    if utxo_list is None or len(utxo_list) <= 0:
        return [], False
    total_balance = 0
    for utxo in utxo_list:
        total_balance += utxo['value']
    if total_balance < total_amount:
        return [], False

    # 获取最佳的utxo列表
    sublist = []

    amount_cnt = 0
    utxo_list = sorted(utxo_list, key=lambda x: x['value'])
    for utxo in utxo_list:
        if amount_cnt < total_amount:
            sublist.append(utxo)
            amount_cnt += utxo['value']
        else:
            break

    return sublist, True
