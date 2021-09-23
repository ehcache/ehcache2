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
package org.terracotta.ehcache.tests;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.terracotta.toolkit.Toolkit;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Chris Dennis
 */
public class GetKeysClient extends ClientBase {

  public static void main(String[] args) {
    new GetKeysClient(args).run();
  }

  public GetKeysClient(String[] args) {
    super("test", args);
  }

  @Override
  protected void runTest(Cache cache, Toolkit toolkit) {
    cache.put(new Element(new Date(), "now"));

    List keys = cache.getKeys();
    boolean interrupted = false;
    try {
      long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(30);
      while (System.nanoTime() < end && keys.isEmpty()) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          interrupted = true;
        }
        keys = cache.getKeys();
      }
    } finally {
      if (interrupted) {
        Thread.currentThread().interrupt();
      }
    }
    if (keys.isEmpty()) {
      throw new AssertionError();
    }
    for (Object key : keys) {
      if (!(key instanceof Date)) {
        throw new AssertionError("Expected Date type for key");
      }
    }
  }
}
