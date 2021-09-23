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

import java.io.Serializable;

public class CopyOnWriteClient extends ClientBase {

  public CopyOnWriteClient(String[] args) {
    super("test", args);
  }

  @Override
  protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
    final Foo foo = new Foo();
    Element e = new Element("foo", foo);
    cache.put(e);
    Object o = cache.get("foo").getObjectValue();

    if (o == foo) { throw new AssertionError(); }
  }

  private static class Foo implements Serializable {
    //
  }

}
