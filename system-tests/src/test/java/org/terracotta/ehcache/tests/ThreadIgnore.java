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

public class ThreadIgnore {
  private final String firstFramePackage;
  private final String threadNamePrefix;

  public ThreadIgnore(String threadNamePrefix, String firstFramePackage) {
    this.threadNamePrefix = threadNamePrefix;
    this.firstFramePackage = firstFramePackage;
  }

  public boolean canIgnore(SimpleThreadInfo info) {
    if (info.getName().startsWith(threadNamePrefix)) {

      String[] stack = info.getStackTraceArray();
      if (stack.length > 1) {
        String frame = stack[stack.length - 2].trim().replaceFirst("at ", "");
        if (frame.startsWith(firstFramePackage)) { return true; }
      } else {
        return true;
      }
    }

    return false;
  }
}
