# rtl433_2_aprs
Parser of RF Data from Weather Stations to be sent over APRS, written in Java

## Dependencies
- Librtlsdr Rrtl-sdr codebase: https://osmocom.org/projects/rtl-sdr/
- RTL_433 radio transmissions decoder (433Mhz, 868MHz, 915Mhz via rtl_sdr dongles): https://github.com/merbanan/rtl_433
- ASFK audio modulation/encoding over AX.25 protocol: https://github.com/damico/javAX25
- GPSD Client: https://github.com/damico/gpsd.client
- Adafruit_BMP.BMP085: https://github.com/adafruit/Adafruit_Python_BMP
- PYUSB: https://pyusb.github.io/pyusb/

## Compiling
- Satisfy the software dependencies;
- Just clone this repository and run the following maven command `mvn clean install -DskipTests`. If you want to generate a executable jar package use this command: `mvn clean install -DskipTests package`.

## Software

### Diagram

![Software Diagram](https://raw.githubusercontent.com/damico/rtl433_2_aprs/main/dist/software-diagram.svg)

## Hardware

### Diagram

![Hardware Diagram](https://raw.githubusercontent.com/damico/rtl433_2_aprs/main/dist/hardware-diagram.svg)

### Prototype 1

![Hardware Prototype 1](https://github.com/damico/rtl433_2_aprs/raw/main/dist/hardware-propotype-01.jpeg)

### Prototype 2

![Hardware Prototype 2a](https://github.com/damico/rtl433_2_aprs/assets/692043/e0f4969c-de4a-4d2a-b6a7-7f544a7e9659)

![Hardware Prototype 2b](https://github.com/damico/rtl433_2_aprs/assets/692043/40223f41-0ca4-43f5-90c0-da4e19f70ff6)


## Sound Card Troubleshooting
If you cannot be sure if an encoded APRS data has been sent/audible by your sound card, maybe it is muted or not working through command line interface, in these cases you may find useful run these 2 commands:

1. Force unmute the Master volume: `$ amixer sset Master unmute` or maybe `$ amixer sset Headphones unmute`
2. Force test audio signals over headphones and speakers: `$ speaker-test -t wav -c 6`
