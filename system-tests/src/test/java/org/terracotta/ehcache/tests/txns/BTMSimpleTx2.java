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
package org.terracotta.ehcache.tests.txns;

import bitronix.tm.Configuration;
import bitronix.tm.TransactionManagerServices;
  
public class BTMSimpleTx2 extends SimpleTx2 {

  public BTMSimpleTx2(String[] args) {
    super(args);
    Configuration config = TransactionManagerServices.getConfiguration();
    config.setServerId("simpletx-2-" + Math.random());
    config.setJournal("null");
  }
  
  public static void main(String[] args) {
    new BTMSimpleTx2(args).run();
  }
}
