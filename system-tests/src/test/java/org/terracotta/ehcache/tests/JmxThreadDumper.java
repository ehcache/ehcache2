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

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import javax.management.MBeanServerConnection;

public class JmxThreadDumper {
  private ThreadMXBean threadMxBean;

  public JmxThreadDumper(MBeanServerConnection server) throws IOException {
    this.threadMxBean = ManagementFactory.newPlatformMXBeanProxy(server, ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
  }

  /**
   * Prints the thread dump information to System.out.
   */
  public void threadDump() {
    System.out.println("Full Java thread dump");
    long[] threadIds = threadMxBean.getAllThreadIds();
    ThreadInfo[] threadInfos = threadMxBean.getThreadInfo(threadIds, Integer.MAX_VALUE);
    for (ThreadInfo threadInfo : threadInfos) {
      printThreadInfo(threadInfo);
    }
  }

  private void printThreadInfo(ThreadInfo threadInfo) {
    printThread(threadInfo);

    StackTraceElement[] stackTrace = threadInfo.getStackTrace();
    for (StackTraceElement stackTraceElement : stackTrace) {
      System.out.println("    at " + stackTraceElement.toString());
    }
    System.out.println();
  }

  private void printThread(ThreadInfo threadInfo) {
    StringBuilder sb = new StringBuilder("\"" + threadInfo.getThreadName() + "\"" + " Id=" + threadInfo.getThreadId() +
            " in " + threadInfo.getThreadState());
    if (threadInfo.getLockName() != null) {
        sb.append(" on lock=").append(threadInfo.getLockName());
    }
    if (threadInfo.isSuspended()) {
      sb.append(" (suspended)");
    }
    if (threadInfo.isInNative()) {
      sb.append(" (running in native)");
    }
    System.out.println(sb.toString());
    if (threadInfo.getLockOwnerName() != null) {
      System.out.println("     owned by " + threadInfo.getLockOwnerName() + " Id=" + threadInfo.getLockOwnerId());
    }
  }

}
