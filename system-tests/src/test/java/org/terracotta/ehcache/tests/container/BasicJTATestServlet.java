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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.transaction.manager.DefaultTransactionManagerLookup;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

public class BasicJTATestServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    try {
    final TransactionManagerLookup lookup = new DefaultTransactionManagerLookup();
    final TransactionManager txManager = lookup.getTransactionManager();
    if(txManager == null) {
      throw new AssertionError("txnManager is null, this test container test requires a txnManager");
    }
    System.out.println("txnManager class: " + txManager);
    String cmd = req.getParameter("cmd");
    debug("Doing command: " + cmd);
    if ("insert".equals(cmd)) {
      doInsert(txManager);
    } else if ("query".equals(cmd)) {
      doQuery(txManager);
    } else {
      out.println("Unknown command: " + cmd);
      return;
    }
    } catch(Exception e) {
      throw new IOException(e);
    }
    out.println("OK");
  }

  private void doInsert(TransactionManager txnManager) throws IllegalStateException, SecurityException {
    CacheManager mgr = CacheManager.getInstance();
    Cache cache = mgr.getCache("test");
   
    try {
      txnManager.setTransactionTimeout(300);
      txnManager.begin();
  
      cache.put(new Element("1", "one"));
  
      Transaction tx1 = txnManager.suspend();
  
      txnManager.begin();
      cache.put(new Element("2", "two"));
      
      txnManager.rollback();
  
      txnManager.resume(tx1);
  
      if (cache.get("2") != null && "two".equals(cache.get("2").getValue()))
        cache.put(new Element("1-2", "one-two"));
  
      txnManager.commit();
  
    } catch(Exception e) {
      throw new AssertionError(e);
    }
  }

  private void doQuery(TransactionManager txnManager) {
    CacheManager mgr = CacheManager.getInstance();
    Cache cache = mgr.getCache("test");
    //validate
    
    try {
      txnManager.begin();
      
      Element elementOne = cache.get("1");
      
      if(elementOne == null) {
        throw new AssertionError("element one should exist!");
      }
      
      Element elementTwo = cache.get("2");
      if(elementTwo != null) {
        throw new AssertionError("element two shouldn't exist!");
      }
      
      Element elementOneTwo = cache.get("1-2");
      if(elementOneTwo != null) {
        throw new AssertionError("element one-two shouldn't exist!");
      }
      
      txnManager.commit();
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private void debug(String string) {
    System.out.println(string);

  }

}
