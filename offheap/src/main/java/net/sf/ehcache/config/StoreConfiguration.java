/**
 *  Copyright 2003-2010 Terracotta, Inc.
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

package net.sf.ehcache.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Class to hold the Terracotta configuration - either a pointer to the real config or a
 * container for embedded config.
 *
 * @author Ludovic Orban
 */
public class StoreConfiguration implements Cloneable {

    private String fullyQualifiedClassPath;
    private final Properties properties = new Properties();

    /**
     * Sets the class name.
     *
     * @param fullyQualifiedClassPath
     */
    public final void setClass(String fullyQualifiedClassPath) {
        this.fullyQualifiedClassPath = fullyQualifiedClassPath;
    }

    /**
     * @return this configuration instance
     * @see #setClass(String)
     */
    public StoreConfiguration className(String fullyQualifiedClassPath) {
        setClass(fullyQualifiedClassPath);
        return this;
    }

    /**
     * Getter.
     */
    public final String getFullyQualifiedClassPath() {
        return fullyQualifiedClassPath;
    }

    public void addAnyProperty(String name, String value) {
        properties.setProperty(name, value);
    }

    public StoreConfiguration anyProperty(String name, String value) {
        addAnyProperty(name, value);
        return this;
    }

    public Properties getAnyProperties() {
        return properties;
    }

    /**
     * Clones this object, following the usual contract.
     *
     * @return a copy, which independent other than configurations than cannot change.
     */
    @Override
    public StoreConfiguration clone() {
        try {
            return (StoreConfiguration) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
