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

public class ClientArrayValues2 extends ClientBase {

  public ClientArrayValues2(String[] args) {
    super("test", args);
  }

  public static void main(String[] args) {
    new ClientArrayValues2(args).run();
  }

  @Override
  protected void runTest(Cache cache, Toolkit toolkit) throws Throwable {
      Element elem = cache.get("key");
      if(elem == null) {
        throw new AssertionError("No element!");
      }
      String[] value = (String[])elem.getValue();
      if(value.length != 3 || !value[0].equals("a") || !value[1].equals("b") || !value[2].equals("c")) {
        throw new AssertionError("Didn't get String[] { \"a\", \"b\", \"c\"");
      }
      
  }
}
