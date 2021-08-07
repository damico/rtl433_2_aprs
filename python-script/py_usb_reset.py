#!/usr/bin/python3
import sys
import usb.core


def main():
    return reset_usb_device(sys.argv[1], sys.argv[2])


def reset_usb_device(id_vendor, id_product):
    print('Looking for device:', id_vendor, id_product)
    dev = usb.core.find(idVendor=int(id_vendor, 16), idProduct=int(id_product, 16))
    if dev is None:
        print('Device not found')
        return 1
    else:
        print('Device found ('+dev.product+'), reseting it!')
        dev.reset()
        return 0


if __name__ == "__main__":
    main()
