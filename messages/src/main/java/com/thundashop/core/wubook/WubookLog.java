package com.thundashop.core.wubook;

import com.thundashop.core.common.DataCommon;
import java.util.HashMap;

public class WubookLog extends DataCommon {
       private String message;
       private Long timeStamp;

       public WubookLog() {
       }

       public WubookLog(String message, Long timeStamp) {
              this.message = message;
              this.timeStamp = timeStamp;
       }

       public String getMessage() {
              return message;
       }

       public Long getTimeStamp() {
              return timeStamp;
       }
}
