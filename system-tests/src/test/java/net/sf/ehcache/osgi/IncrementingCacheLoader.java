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
package net.sf.ehcache.osgi;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.loader.CacheLoader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IncrementingCacheLoader implements CacheLoader {
  private final int     incrementalAmount;
  private final boolean matchEvens;
  private long          msDelay = 0L;

  public IncrementingCacheLoader(boolean matchEvens, int inc) {
    this.incrementalAmount = inc;
    this.matchEvens = matchEvens;
  }

  @Override
  public Map loadAll(Collection keys, Object argument) {
    return loadAll(keys);
  }

  public long getMsDelay() {
    return msDelay;
  }

  public void setMsDelay(long msDelay) {
    this.msDelay = msDelay;
  }

  @Override
  public Map loadAll(Collection keys) {

    HashMap ret = new HashMap();
    for (Object k : keys) {
      Object got = load(k);
      if (got != null) {
        ret.put(k, got);
      }
    }
    return ret;
  }

  @Override
  public Object load(Object key, Object argument) {
    return load(key);
  }

  @Override
  public Object load(Object key) throws CacheException {
    if (getMsDelay() > 0L) {
      try {
        Thread.sleep(getMsDelay());
      } catch (InterruptedException e) {
      }
    }
    if (key instanceof Number) {
      int ivalue = ((Number) key).intValue();
      if ((((ivalue & 0x01) != 0) && !matchEvens) || ((ivalue & 0x01) == 0) && matchEvens) {
        int next = ivalue + incrementalAmount;
        return next + "";
      } else {
        return null;
      }
    }
    return key.toString();
  }

  @Override
  public void init() {
    // TODO Auto-generated method stub

  }

  @Override
  public Status getStatus() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void dispose() throws CacheException {
    // TODO Auto-generated method stub

  }

  @Override
  public CacheLoader clone(Ehcache cache) throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String toString() {
    return "IncrementingCacheLoader [matchEvens=" + matchEvens + ", incrementalAmount=" + incrementalAmount + "]";
  }
}
