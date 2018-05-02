/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.announcements.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 *     <p>$LastChangedBy$ $LastChangedDate$
 */
public class AnnouncementCleanupThread extends Thread {

  @Autowired private IAnnouncementService announcementService;

  private int hourToCheck = 3; // military time
  private int minuteToCheck = 0;
  private int checkInterval = 60; // seconds
  private int expireThreshold = 60; // in days
  private long maxCheckIntervalMillis = 43200000L; // 12 hours
  private boolean keepRunning;

  private static Logger log = LoggerFactory.getLogger(AnnouncementCleanupThread.class);

  public AnnouncementCleanupThread() {
    setDaemon(true);
    keepRunning = true;
  }

  public void stopThread() {
    keepRunning = false;
    log.info("Stopping cleanup thread...");
    this.interrupt();
  }

  /* (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    if (expireThreshold < 0) {
      //A value less than 0 indicates we want to retain all expired announcements
      // and as such there is no reason for the thread to keep running
      this.stopThread();
    }

    Date now;
    Calendar nowCal = new GregorianCalendar();
    long lastCheckTime = System.currentTimeMillis();
    boolean firstCheck = true;

    while (true && keepRunning) {
      now = new Date();
      nowCal.setTime(now);

      /**
       * If the current hour of the day = the hour to check AND the current minute of the hour = the
       * minute to check (plus a range of 2 minutes) AND the current time is later than the last
       * time we checked + the required interval
       */
      if (nowCal.get(Calendar.HOUR_OF_DAY) == hourToCheck
          && nowCal.get(Calendar.MINUTE) <= (minuteToCheck + 1)
          && (firstCheck
              || System.currentTimeMillis() > (lastCheckTime + maxCheckIntervalMillis))) {

        if (expireThreshold > 0) {
          log.info("Going to delete old announcements at " + now.toString());
          announcementService.deleteAnnouncementsPastExpirationThreshold(expireThreshold);
        } else {
          log.info("Going to delete expired announcements at " + now.toString());
          announcementService.deleteAnnouncementsPastCurrentTime();
        }

        lastCheckTime = System.currentTimeMillis();
        firstCheck = false;
      }
      try {
        log.trace("Waiting to see if we should check the time...");
        sleep(checkInterval * 1000);
      } catch (InterruptedException e) {
        break;
      }
    }
  }

  /** @param checkInterval the checkInterval to set */
  public void setCheckInterval(int checkInterval) {
    this.checkInterval = checkInterval;
  }

  /** @param hourToCheck the hourToCheck to set */
  public void setHourToCheck(int hourToCheck) {
    this.hourToCheck = hourToCheck;
  }

  /** @param minuteToCheck the minuteToCheck to set */
  public void setMinuteToCheck(int minuteToCheck) {
    this.minuteToCheck = minuteToCheck;
  }

  /** @param maxCheckIntervalMillis the maxCheckIntervalMillis to set */
  public void setMaxCheckIntervalMillis(long maxCheckIntervalMillis) {
    this.maxCheckIntervalMillis = maxCheckIntervalMillis;
  }

  public void setExpireThreshold(int expireThreshold) {
    this.expireThreshold = expireThreshold;
  }
}
