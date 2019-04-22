#!/usr/bin/python

import codelock

if __name__ == "__main__":
#    lock = codelock.DoorLock(16, 7, 10, 27, 51517)
    lock = codelock.DoorLock(13, 11, 10, 27, 51518, 2, True, "https://20363.getshop.com/scripts/pushAccessCode.php?engine=default&id=2&code={CODE}&domain=EntraceB")
    lock.main()

