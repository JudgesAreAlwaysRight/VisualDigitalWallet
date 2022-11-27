from os import urandom
from bitcoin import SelectParams
from bitcoin.wallet import CBitcoinSecret, P2PKHBitcoinAddress

SelectParams('mainnet')
# SelectParams('testnet')
a = urandom(32)
private_key = CBitcoinSecret.from_secret_bytes(a)
print(a.hex())
print("Private key: %s" % private_key)
print("Address: %s" % P2PKHBitcoinAddress.from_pubkey(private_key.pub))
