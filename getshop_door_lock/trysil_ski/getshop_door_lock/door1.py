#!/usr/bin/python

import codelock

if __name__ == "__main__":
    # LED, OPENBUTTON, STRIKE_RELAY, DOORENGINE_RELAY, SOCKETPORT
    lock = codelock.DoorLock(16, 11, 10, 22, 51517, 2, True, "https://trysilhotell.getshop.com/scripts/pushAccessCode.php?engine=default&id=2&code={CODE}&domain=External%20Door")

    lock.main()

