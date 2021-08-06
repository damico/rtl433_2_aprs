#!/usr/bin/python
from usb.core import find as finddev
dev = finddev(idVendor=0x0bda, idProduct=0x2838)
dev.reset()
