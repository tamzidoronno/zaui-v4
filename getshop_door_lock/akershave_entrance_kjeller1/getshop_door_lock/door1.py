#!/usr/bin/python

import codelock

if __name__ == "__main__":
    # LED, OPENBUTTON, STRIKE_RELAY, DOORENGINE_RELAY, SOCKETPORT
    #lock = codelock.DoorLock(13, 9, 22, 29,51519, 1, True, "https://20363.getshop.com/scripts/pushAccessCode.php?engine=default&id=1&code={CODE}&domain=kjeller1")
    lock = codelock.DoorLock(16, 7, 10, 27,51517, 1, True, "https://20363.getshop.com/scripts/pushAccessCode.php?engine=default&id=1&code={CODE}&domain=kjeller1")
    #lock = codelock.DoorLock(16, 7, 10, 27, 51517)


    lock.main()

