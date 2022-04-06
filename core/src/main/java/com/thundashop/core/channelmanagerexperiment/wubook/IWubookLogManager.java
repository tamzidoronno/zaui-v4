package com.thundashop.core.channelmanagerexperiment.wubook;

import java.util.stream.Stream;

public interface IWubookLogManager {

    void save(String message, Long timeStamp);

    Stream<WubookLog> get();

}
