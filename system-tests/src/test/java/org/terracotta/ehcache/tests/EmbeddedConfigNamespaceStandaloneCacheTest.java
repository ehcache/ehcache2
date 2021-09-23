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

import org.apache.commons.io.IOUtils;

import com.tc.test.config.model.TestConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author cdennis
 */
public class EmbeddedConfigNamespaceStandaloneCacheTest extends AbstractCacheTestBase {

  public EmbeddedConfigNamespaceStandaloneCacheTest(TestConfig testConfig) {
    super("embedded-config-cache-test-ns.xml", testConfig);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected String writeXmlFileWithPort(String resourcePath, String outputName, String nameSuffix) throws IOException {
    if (nameSuffix != null && outputName.indexOf(".xml") > 0) {
      outputName = outputName.substring(0, outputName.indexOf(".xml")) + "-" + nameSuffix + ".xml";
    }
    resourcePath = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
    // Slurp resourcePath file
    System.out.println("RESOURCE PATH: " + resourcePath);

    InputStream is = this.getClass().getResourceAsStream(resourcePath);

    List<String> lines = IOUtils.readLines(is);

    // Replace PORT token
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      line = line.replace("PORT", Integer.toString(getGroupsData()[0].getTsaPort(0)));
      line = line.replace("CONFIG", ehcacheConfigPath);
      line = line.replace("TEMP", tempDir.getAbsolutePath());
      line = line.replace("TERRACOTTA_URL", getTerracottaURL());

      String nameSuffixReplaceValue = nameSuffix == null ? "" : "-" + nameSuffix;
      line = line.replace("__NAME_SUFFIX__", nameSuffixReplaceValue);

      lines.set(i, line);
    }

    // Write
    File outputFile = new File(tempDir, outputName);
    FileOutputStream fos = new FileOutputStream(outputFile);
    IOUtils.writeLines(lines, IOUtils.LINE_SEPARATOR, fos);
    return tempDir.getAbsolutePath();
  }
}
