#!/usr/bin/python

import codelock

if __name__ == "__main__":
    # LED, OPENBUTTON, STRIKE_RELAY, DOORENGINE_RELAY, SOCKETPORT
    lock = codelock.DoorLock(13, 11, 10, 27,  51518, 1, False, "https://haugesund.getshop.com/scripts/pushAccessCode.php?engine=default&id=1&code={CODE}&domain=entrance")
    lock.main()

