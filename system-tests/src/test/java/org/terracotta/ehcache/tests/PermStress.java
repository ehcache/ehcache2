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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PermStress {

  public void stress(int num) {
    List<Loader> loaders = new ArrayList<Loader>();
    String name = PermStress.class.getName();
    byte[] clazz = getBytes(name);

    for (int i = 0; i < num; i++) {
      try {
        Loader loader = new Loader();
        loaders.add(loader);
        loader.defineClass(name, clazz);
      } catch (OutOfMemoryError error) {
        loaders.clear();
        error.printStackTrace();
        break;
      }
    }
  }

  private byte[] getBytes(String name) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      InputStream in = getClass().getClassLoader().getResourceAsStream(name.replace('.', '/').concat(".class"));
      int b;
      while ((b = in.read()) >= 0) {
        out.write(b);
      }

      return out.toByteArray();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  private static class Loader extends ClassLoader {
    public Class<?> defineClass(String name, byte[] clazz) {
      return defineClass(name, clazz, 0, clazz.length);
    }
  }

}
