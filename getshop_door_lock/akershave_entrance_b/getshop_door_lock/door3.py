#!/usr/bin/python

import codelock

if __name__ == "__main__":
    #lock = codelock.DoorLock(16, 7, 10, 27, 51517)
    lock = codelock.DoorLock(18, 23, 10, 27, 51519, 3, True, "https://20363.getshop.com/scripts/pushAccessCode.php?engine=default&id=3&code={CODE}&domain=EntraceB")
    lock.main()

