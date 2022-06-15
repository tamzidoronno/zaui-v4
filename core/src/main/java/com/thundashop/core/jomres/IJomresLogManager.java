package com.thundashop.core.jomres;

import java.util.stream.Stream;

public interface IJomresLogManager {

    void save(String message, Long timeStamp);

    Stream<JomresLog> get();

}
