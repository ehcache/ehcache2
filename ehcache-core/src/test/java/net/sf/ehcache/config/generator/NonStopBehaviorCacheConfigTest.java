/**
 *  Copyright Terracotta, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.sf.ehcache.config.generator;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.config.NonstopConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.config.TimeoutBehaviorConfiguration;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.terracotta.test.categories.CheckShorts;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

@Category(CheckShorts.class)
public class NonStopBehaviorCacheConfigTest {

    @Test
    public void testNonStopBehaviorCacheConfig() throws Exception {
      // Load the config
      Configuration cacheMgrConfig = ConfigurationFactory.parseConfiguration(getClass().getClassLoader().getResource("ehcache-nonstop-behavior-test.xml"));

      // Change the nonstop timeout behavior
      CacheConfiguration cacheConfiguration = cacheMgrConfig.getCacheConfigurations().get("A1");
      TerracottaConfiguration tccfg = cacheConfiguration.getTerracottaConfiguration();
      NonstopConfiguration nscfg = tccfg.getNonstopConfiguration();
      TimeoutBehaviorConfiguration tobcfg = nscfg.getTimeoutBehavior();
      tobcfg.setType(TimeoutBehaviorConfiguration.NOOP_TYPE_NAME);

      // Re-generate the config
      String cfgText = ConfigurationUtil.generateCacheManagerConfigurationText(cacheMgrConfig);

      // Make sure the generated XML contains the updated nonstop timeout behavior
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(new ByteArrayInputStream(cfgText.getBytes(Charset.forName("UTF-8"))));
      XPath xpath = XPathFactory.newInstance().newXPath();
      XPathExpression expr = xpath.compile("/ehcache/cache[@name='A1']/terracotta/nonstop/timeoutBehavior/@type");

      NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
      assertThat(result.getLength(), is(1));
      assertThat(result.item(0).getNodeValue(), equalTo("noop"));
    }
}
