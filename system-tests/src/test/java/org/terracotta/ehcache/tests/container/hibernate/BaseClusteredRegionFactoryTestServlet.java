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
package org.terracotta.ehcache.tests.container.hibernate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class BaseClusteredRegionFactoryTestServlet extends HttpServlet {

  @Override
  public final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession(true);
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    String server = request.getParameter("server");
    if ("server0".equals(server)) {
      try {
        doServer0(session, request.getParameterMap());
        out.println("OK");
      } catch (Exception e) {
        e.printStackTrace(out);
      }
    } else if ("server1".equals(server)) {
      try {
        doServer1(session, request.getParameterMap());
        out.println("OK");
      } catch (Exception e) {
        e.printStackTrace(out);
      }
    }
    out.flush();
  }
  
  protected abstract void doServer0(HttpSession session, Map<String, String[]> parameters) throws Exception;
  
  protected abstract void doServer1(HttpSession session, Map<String, String[]> parameters) throws Exception;
}
