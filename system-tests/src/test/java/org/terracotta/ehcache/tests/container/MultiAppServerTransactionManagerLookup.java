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
package org.terracotta.ehcache.tests.container;

import net.sf.ehcache.transaction.manager.TransactionManagerLookup;
import net.sf.ehcache.transaction.xa.EhcacheXAResource;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;

/**
 * @author lorban
 */
public class MultiAppServerTransactionManagerLookup implements TransactionManagerLookup {

    private static final String[] JNDI_NAMES = new String[] {
            "java:/TransactionManager",             // JBoss 5.1 & Resin 3
            "java:appserver/TransactionManager",    // Glassfish 2
            "javax.transaction.TransactionManager"  // Weblogic
    };


    @Override
    public TransactionManager getTransactionManager() {
        for (String jndiName : JNDI_NAMES) {
            TransactionManager tm = lookup(jndiName);
            if (tm != null)
                return tm;
        }
        return null;
    }

    private TransactionManager lookup(String jndiName) {
        Context ctx = null;
        try {
            ctx = new InitialContext();

            TransactionManager tm = (TransactionManager) ctx.lookup(jndiName);

            return tm;
        } catch (NamingException e) {
            return null;
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
  public void register(EhcacheXAResource resource, boolean forRecovery) {
        // no-op
    }

    @Override
  public void unregister(EhcacheXAResource resource, boolean forRecovery) {
        // no-op
    }

    @Override
    public void setProperties(Properties properties) {
        // no-op
    }

  @Override
  public void init() {
    // no-op
  }

}
