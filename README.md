# rtl433_2_aprs
Parser of RF Data from Weather Stations to be sent over APRS

## Dependency
The main lib dependency of this project is: https://github.com/damico/javAX25 for ASFK audio modulation/encoding over AX.25 protocol 

## Sound Card Troubleshooting
If you cannot be sure if an encoded APRS data has been sent/audible by your sound card, maybe it is muted or not working through command line interface, in these case you may find useful run these 2 commands:

1. Force unmute the Master volume: `$ amixer sset Master unmute` 
2. Force test audio signals over headphones and speakers: `$ speaker-test -t wav -c 6`
