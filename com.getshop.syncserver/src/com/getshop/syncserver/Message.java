package com.getshop.syncserver;

public class Message {

    public static class Types {
        public static int authenticate = 1;
        public static int deletefile = 2;
        public static int sendfile = 3;
        public static int authenticated = 4;
        public static int failed = 5;
        public static int ok = 6;
        public static int ping = 7;
        public static int pong = 8;
        public static int movefile = 9;
    }

    public String errorMessage;
    public String username;
    public String address;
    public String password;

    public byte[] data;
    public String filepath;
    public int type;
    public String fromPath;
    public String toPath;
}
