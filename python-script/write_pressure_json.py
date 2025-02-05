#!/usr/bin/python
import Adafruit_BMP.BMP085 as BMP085
from datetime import datetime 
sensor = BMP085.BMP085()
psea = sensor.read_pressure() / pow(1.0 - sensor.read_altitude()/44330.0, 5.255)
print('{"pressure":',psea,', "datetime": ','"'+datetime.now().strftime('%Y-%m-%d %H:%M:%S')+'"','}')
