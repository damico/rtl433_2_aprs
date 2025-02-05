import serial
import pynmea2
def parseGPS(str):
    if str.find('GGA') > 0:
       msg = pynmea2.parse(str)
       #print(msg.sentence_type)
       print(msg.timestamp,msg.lat,msg.lat_dir,msg.lon,msg.lon_dir,msg.altitude,msg.altitude_units)
serialPort = serial.Serial("/dev/ttyAMA0", 9600, timeout=0.5)
while True:
    str = serialPort.readline().decode('ascii', errors='replace')
    #print(str)
    parseGPS(str)
