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

package net.sf.ehcache.distribution;

import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


/**
 * A provider of Peer RMI addresses.
 *
 * @author Greg Luck
 * @version $Id$
 */
public abstract class RMICacheManagerPeerProvider implements CacheManagerPeerProvider {

    private static final Logger LOG = LoggerFactory.getLogger(RMICacheManagerPeerProvider.class.getName());

    /**
     * The CacheManager this peer provider is associated with.
     */
    protected CacheManager cacheManager;


    /**
     * @param cacheManager The CacheManager to associate to for this provider.
     */
    public RMICacheManagerPeerProvider(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Empty constructor
     */
    public RMICacheManagerPeerProvider() {
        //nothing to do
    }

    /**
     * Gets the cache name out of the url
     * <p>
     *
     * @param rmiUrl the URL of the node.
     * @return the cache name as it would appear in ehcache.xml
     */
    static String extractCacheName(String rmiUrl) {
        return rmiUrl.substring(rmiUrl.lastIndexOf('/') + 1);
    }

    /**
     * The use of one-time registry creation and Naming.rebind should mean we can create as many listeneres as we like.
     * They will simply replace the ones that were there.
     */
    public CachePeer lookupRemoteCachePeer(String url) throws MalformedURLException, NotBoundException, RemoteException {
        LOG.debug("Lookup URL {}", url);

        return (CachePeer) Naming.lookup(url);
    }

    @Override
    public void dispose() {
        //nothing to do.
    }

    /**
     * The cacheManager this provider is bound to
     */
    public final CacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * The replication scheme. Each peer provider has a scheme name, which can be used to specify
     * the scheme for replication and bootstrap purposes. Each <code>CacheReplicator</code> should lookup
     * the provider for its scheme type during replication. Similarly a <code>BootstrapCacheLoader</code>
     * should also look up the provider for its scheme.
     * <p>
     *
     * @return the well-known scheme name, which is determined by the replication provider author.
     * @since 1.6 introduced to permit multiple distribution schemes to be used in the same CacheManager
     */
    public String getScheme() {
        return "RMI";
    }
}
