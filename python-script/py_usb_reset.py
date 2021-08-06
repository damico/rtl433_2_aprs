#!/usr/bin/python3
from usb.core import find as finddev
import sys


def main():
    reset_usb_device(sys.argv[1], sys.argv[2])


def reset_usb_device(id_vendor, id_product):
    dev = finddev(idVendor=0x0bda, idProduct=0x2838)
    dev.reset()


if __name__ == "__main__":
    main()
