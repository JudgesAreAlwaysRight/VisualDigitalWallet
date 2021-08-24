#!/bin/bash
#
# milkyboat@github
# 2021-Aug24
#
# this bash will auto mining one block per second for one hour
#

i=0
for i in $(seq 0 1 3600);
do
  bitcoin-cli -regtest -generate 1
  sleep 1
done
