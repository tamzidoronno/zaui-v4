/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.remote;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

class Clarion {
      private Clarion clarion;

      private long offset;

      public Clarion() {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("GMT"));
            cal.set(1800, Calendar.DECEMBER, 28, 0, 0, 0);
            long start = cal.getTime().getTime();
            cal.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
            long end = cal.getTime().getTime();
            offset = end - start;
      }

      public long clarion(Date d) {
            if (clarion == null) {
                  clarion = new Clarion();
            }
            return (d.getTime() + offset) / (1000 * 60 * 60 * 24);
      }
}
