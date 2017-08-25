#!/usr/bin/python

import codelock

if __name__ == "__main__":
    # LED, OPENBUTTON, STRIKE_RELAY, DOORENGINE_RELAY, SOCKETPORT
    lock = codelock.DoorLock(22, 11, 16, 17,  51517, 1, False, "https://www.getshop.com/scripts/pushAccessCode.php?engine=default&id=1&code={CODE}&domain=External%20Door")
    lock.main()

