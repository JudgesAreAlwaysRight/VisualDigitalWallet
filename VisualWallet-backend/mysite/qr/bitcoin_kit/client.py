from bitcoin import SelectParams
from bitcoin.wallet import CBitcoinAddress, CBitcoinSecret, P2PKHBitcoinAddress
from utils import *

"""
utxo接口来自btc.com，相关接口文档参考：https://explorer.btc.com/zh-CN/btc/adapter?type=api-doc
"""


def transact(private_key, target_address, amount, fee, network='mainnet'):
    SelectParams(network)

    # 地址与密钥
    from_private_key = CBitcoinSecret(private_key)
    from_public_key = from_private_key.pub
    from_address = P2PKHBitcoinAddress.from_pubkey(from_public_key)
    to_address = CBitcoinAddress(target_address)

    # 获取utxo
    utxo_list = get_utxo(target_address)
    if not is_sufficient(utxo_list, amount + fee):
        raise RuntimeError("not sufficient funds")

    # tx
    tx_in_script_pub_key = [OP_DUP, OP_HASH160, from_address, OP_EQUALVERIFY, OP_CHECKSIG]
    tx_out_script_pub_key = [OP_DUP, OP_HASH160, to_address, OP_EQUALVERIFY, OP_CHECKSIG]

    tx_in = create_txin(utxo_list[0], 0)
    tx_out = create_txout(amount, tx_out_script_pub_key)

    signature = create_OP_CHECKSIG_signature(tx_in, tx_out, tx_in_script_pub_key, from_private_key)
    tx_in_script_sig = [signature, from_public_key]

    new_tx = create_signed_transaction(tx_in, tx_out, tx_in_script_pub_key, tx_in_script_sig)

    return broadcast_transaction(new_tx, network)





