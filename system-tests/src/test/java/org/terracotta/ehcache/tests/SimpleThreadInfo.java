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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleThreadInfo {
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");
  private final String       name;
  private final List<String> stacktrace;

  public SimpleThreadInfo(String name) {
    this.name = name;
    this.stacktrace = new ArrayList<String>();
  }

  void addFrame(String frame) {
    stacktrace.add(frame);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SimpleThreadInfo other = (SimpleThreadInfo) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    return true;
  }

  @Override
  public String toString() {
    return name + LINE_SEPARATOR + getStackTrace();
  }

  public String getStackTrace() {
    String s = "";
    for (String frame : stacktrace) {
      s += frame + LINE_SEPARATOR;
    }
    return s;
  }

  public String[] getStackTraceArray() {
    return stacktrace.toArray(new String[] {});
  }

  public String getName() {
    return name;
  }
  
  public static Set<SimpleThreadInfo> parseThreadInfo(String text) {
    Set<SimpleThreadInfo> set = new HashSet<SimpleThreadInfo>();
    String[] lines = text.split(LINE_SEPARATOR);
    boolean startNewThread = false;
    SimpleThreadInfo threadInfo = null;
    for (String line : lines) {
      if (line.startsWith("Thread name")) {
        if (threadInfo != null) set.add(threadInfo);
        startNewThread = true;
        threadInfo = new SimpleThreadInfo(line.substring("Thread name".length() + 2));
      } else {
        if (startNewThread) {
          if (line.trim().length() > 0) {
            threadInfo.addFrame(line);
          }
        }
      }
    }
    set.add(threadInfo);
    return set;
  }
}
