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
package org.terracotta.ehcache.tests.scheduledrefresh;

/**
 * Created with IntelliJ IDEA.
 * User: cschanck
 * Date: 5/31/13
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddingCacheLoader implements net.sf.ehcache.loader.CacheLoader {

  @java.lang.Override
  public java.lang.Object load(java.lang.Object key) throws net.sf.ehcache.CacheException {
    if (key instanceof java.lang.Number) {
      int was = ((java.lang.Number) key).intValue();
      Integer ii = new java.lang.Integer(was + 1);
      System.err.println("Was: "+was+" is: "+ii);
      return ii;
    }
    return key;
  }

  @java.lang.Override
  public java.util.Map loadAll(java.util.Collection keys) {

    java.util.HashMap ret = new java.util.HashMap();
    for (java.lang.Object k : keys) {
      java.lang.Object got = load(k);
      if (got != null) {
        ret.put(k, got);
      }
    }
     try {
        Thread.sleep(500);
     } catch (InterruptedException e) {
     }
     return ret;
  }

  @java.lang.Override
  public java.lang.Object load(java.lang.Object key, java.lang.Object argument) {
    return load(key);
  }

  @java.lang.Override
  public java.util.Map loadAll(java.util.Collection keys, java.lang.Object argument) {
    return loadAll(keys);
  }

  @java.lang.Override
  public java.lang.String getName() {
    return "scheduled refresh incrementing cache loader";
  }

  @java.lang.Override
  public net.sf.ehcache.loader.CacheLoader clone(net.sf.ehcache.Ehcache cache) throws java.lang.CloneNotSupportedException {
    return new AddingCacheLoader();
  }

  @java.lang.Override
  public void init() {
  }

  @java.lang.Override
  public void dispose() throws net.sf.ehcache.CacheException {
  }

  @java.lang.Override
  public net.sf.ehcache.Status getStatus() {
    return null;
  }
}

