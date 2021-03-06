/**
 *  Copyright Terracotta, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.sf.ehcache.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates increasing identifiers (in a single VM only).
 * Not valid across multiple VMs. Yet, the identifier is based on time, so that the drifting
 * across a cluster should not ever be large...
 *
 * @author Alex Snaps
 */
public final class Timestamper {

    /**
     * Value for left shifting System.currentTimeMillis, freeing some space for the counter
     */
    public static final int BIN_DIGITS = Integer.getInteger("net.sf.ehcache.util.Timestamper.shift", 12);

    /**
     * What is one milliseconds, based on "counter value reserved space", for this Timestamper
     */
    public static final int ONE_MS = 1 << BIN_DIGITS;

    private static final Logger LOG     = LoggerFactory.getLogger(Timestamper.class);
    private static final int    MAX_LOG = Integer.getInteger("net.sf.ehcache.util.Timestamper.log.max", 1) * 1000;

    private static final AtomicLong VALUE  = new AtomicLong();
    private static final AtomicLong LOGGED = new AtomicLong();


    private Timestamper() {
        //
    }

    /**
     * Returns an increasing unique value based on the {@code System.currentTimeMillis()}
     * with some additional reserved space for a counter.
     *
     * @see net.sf.ehcache.util.Timestamper#BIN_DIGITS
     * @return uniquely &amp; increasing value
     */
    public static long next() {
        int runs = 0;
        while (true) {
            long base = SlewClock.timeMillis() << BIN_DIGITS;
            long maxValue = base + ONE_MS - 1;

            for (long current = VALUE.get(), update = Math.max(base, current + 1); update < maxValue;
                 current = VALUE.get(), update = Math.max(base, current + 1)) {
                if (VALUE.compareAndSet(current, update)) {
                    if (runs > 1) {
                        log(base, "Thread spin-waits on time to pass. Looped "
                                  + "{} times, you might want to increase -Dnet.sf.ehcache.util.Timestamper.shift", runs);
                    }
                    return update;
                }
            }
            ++runs;
        }
    }

    private static void log(final long base, final String message, final Object... params) {
        if (LOG.isInfoEnabled()) {
            long thisLog = (base >> BIN_DIGITS) / MAX_LOG;
            long previousLog = LOGGED.get();
            if (previousLog != thisLog) {
                if (LOGGED.compareAndSet(previousLog, thisLog)) {
                    LOG.info(message, params);
                }
            }
        }
    }
}
