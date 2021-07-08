# rtl433_2_aprs
Parser of RF Data from Weather Stations to be sent over APRS, written in Java

## Dependencies
- RTL_433 radio transmissions decoder (433Mhz, 868MHz, 915Mhz via rtl_sdr dongles): https://github.com/merbanan/rtl_433
- ASFK audio modulation/encoding over AX.25 protocol: https://github.com/damico/javAX25

## Sound Card Troubleshooting
If you cannot be sure if an encoded APRS data has been sent/audible by your sound card, maybe it is muted or not working through command line interface, in these cases you may find useful run these 2 commands:

1. Force unmute the Master volume: `$ amixer sset Master unmute` 
2. Force test audio signals over headphones and speakers: `$ speaker-test -t wav -c 6`
