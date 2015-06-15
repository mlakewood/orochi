#!/usr/bin/env python

import requests
from time import sleep
import sys

status = 500
count = 0

url = sys.argv[1] 

while count < 4:
    try:
        res = requests.get(url)
    except Exception:
        print "got exception"
        sleep(1)
        count += 1
        continue
    status = res.status_code
    if status == 200:
        print "we got 200!"
        exit(0)
    count += 1
#    sleep(1)

print "we got badness"
exit(16)
