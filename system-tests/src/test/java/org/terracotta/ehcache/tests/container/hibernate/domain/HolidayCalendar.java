/*
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *      http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Platform.
 *
 * The Initial Developer of the Covered Software is
 *      Terracotta, Inc., a Software AG company
 */
package org.terracotta.ehcache.tests.container.hibernate.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HolidayCalendar {
  private Long   id;
  private Map holidays = new HashMap(); // Date -> String
  
  public HolidayCalendar init() {
    DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
    try {
      holidays.clear();
      holidays.put(df.parse("2009.01.01"), "New Year's Day");
      holidays.put(df.parse("2009.02.14"), "Valentine's Day");
      holidays.put(df.parse("2009.11.11"), "Armistice Day");
    } catch (ParseException e) {
      System.out.println("Error parsing date string");
      throw new RuntimeException(e);
    }
    return this;
  }
  
  public Map getHolidays() {
    return holidays;
  }

  protected void setHolidays(Map holidays) {
    this.holidays = holidays;
  }

  public void addHoliday(Date d, String name) {
    holidays.put(d, name);
  }
  
  public String getHoliday(Date d) {
    return (String)holidays.get(d);
  }
  
  public boolean isHoliday(Date d) {
    return holidays.containsKey(d);
  }

  protected Long getId() {
    return id;
  }
  
  protected void setId(Long id) {
    this.id = id;
  }
}

