from bitcoin import SelectParams
from bitcoin.base58 import decode
from bitcoin.wallet import CBitcoinAddress, CBitcoinSecret, P2PKHBitcoinAddress


SelectParams('testnet')

# TODO: Fill this in with your private key.
my_private_key = CBitcoinSecret(
    'cMkAudBwWfqhCC2rsBEMn1N4qcUk4nQookSd1hDqppZYEp2Jh2in')
my_public_key = my_private_key.pub
print(my_public_key)
my_address = P2PKHBitcoinAddress.from_pubkey(my_public_key)
faucet_address = CBitcoinAddress('mv4rnyY3Su5gjcDNzbMLKBQkBicCtHUtFB')