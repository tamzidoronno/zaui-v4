#!/usr/bin/python

# OLD
#curl -v -u admin:adminadmin --globoff "http://localhost:8083/ZWave.zway/Run/devices[2].instances[0].commandClasses[98].Set(0)"

import RPi.GPIO as GPIO
from time import sleep

GPIO.setmode(GPIO.BCM)
GPIO.setup(22, GPIO.OUT)
GPIO.output(22, True)






