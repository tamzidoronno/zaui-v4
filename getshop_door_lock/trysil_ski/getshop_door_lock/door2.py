#!/usr/bin/python

import codelock

if __name__ == "__main__":
    #lock = codelock.DoorLock(16, 7, 10, 27, 51517)
    # LED, OPENBUTTON, STRIKE_RELAY, DOORENGINE_RELAY, SOCKETPORT
    lock = codelock.DoorLock(16, 7, 27, 17, 51518, 1, True, "https://trysilhotell.getshop.com/scripts/pushAccessCode.php?engine=default&id=1&code={CODE}&domain=External%20Door")
    lock.main()

