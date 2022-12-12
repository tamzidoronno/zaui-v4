package com.thundashop.zauiactivity.constant;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ZauiConstants {
    public static final String OCTO_API_ENDPOINT = "https://api.zaui.io/octo";
    public static final String OCTO_API_KEY = "8bf6895a9a5a92b932d3c0aa9b24a8c7ba0b10d498983cea8eef17f35f2fb95b";

    public static Pair<String,String> OCTO_CONTENT = new ImmutablePair<>("OCTO-Capabilities","octo/content");
    public static Pair<String,String> OCTO_PRICING = new ImmutablePair<>("OCTO-Capabilities","octo/pricing");
    public static String ZAUI_ACTIVITY_TAG = "zaui-activity";
}
