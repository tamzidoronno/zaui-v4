package com.thundashop.core.usermanager.data;

import java.util.Date;

public class UserOAuthorization {
    public String code;
    public Date expire;
    public String token;
    public boolean authorized;
    public String refreshToken;
}
