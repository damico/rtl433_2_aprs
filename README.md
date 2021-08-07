# rtl433_2_aprs
Parser of RF Data from Weather Stations to be sent over APRS, written in Java

## Dependencies
- Librtlsdr Rrtl-sdr codebase: https://osmocom.org/projects/rtl-sdr/
- RTL_433 radio transmissions decoder (433Mhz, 868MHz, 915Mhz via rtl_sdr dongles): https://github.com/merbanan/rtl_433
- ASFK audio modulation/encoding over AX.25 protocol: https://github.com/damico/javAX25
- GPSD Client: https://github.com/damico/gpsd.client
- Adafruit_BMP.BMP085: https://github.com/adafruit/Adafruit_Python_BMP
- PYUSB: https://pyusb.github.io/pyusb/

## Software

### Diagram

![Software Diagram](https://raw.githubusercontent.com/damico/rtl433_2_aprs/main/dist/software-diagram.svg)

## Hardware

### Diagram

![Hardware Diagram](https://raw.githubusercontent.com/damico/rtl433_2_aprs/main/dist/hardware-diagram.svg)

### Prototype

![Hardware Protorype](https://github.com/damico/rtl433_2_aprs/raw/main/dist/hardware-propotype-01.jpeg)

## Sound Card Troubleshooting
If you cannot be sure if an encoded APRS data has been sent/audible by your sound card, maybe it is muted or not working through command line interface, in these cases you may find useful run these 2 commands:

1. Force unmute the Master volume: `$ amixer sset Master unmute` 
2. Force test audio signals over headphones and speakers: `$ speaker-test -t wav -c 6`
