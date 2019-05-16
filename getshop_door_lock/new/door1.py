
import codelock

if __name__ == "__main__":
    # LED, OPENBUTTON, STRIKE_RELAY, DOORENGINE_RELAY, SOCKETPORT
    #lock = codelock.DoorLock(16, 7, 10, 27, 51517)
    lock = codelock.DoorLock(13, 11, 10, 27, 51518, 1, False, "NOTINUSE")
    lock.main()

