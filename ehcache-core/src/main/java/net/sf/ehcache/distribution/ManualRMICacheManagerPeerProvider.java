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

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A provider of Peer RMI addresses based off manual configuration.
 * <p>
 * Because there is no monitoring of whether a peer is actually there, the list of peers is dynamically
 * looked up and verified each time a lookup request is made.
 * <p>
 *
 * @author Greg Luck
 * @version $Id$
 */
public class ManualRMICacheManagerPeerProvider extends RMICacheManagerPeerProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ManualRMICacheManagerPeerProvider.class.getName());

    /**
     * Contains a RMI URLs of the form: "//" + hostName + ":" + port + "/" + cacheName;
     */
    protected final Set<String> peerUrls = Collections.synchronizedSet(new HashSet<>());

    /**
     * Empty constructor.
     */
    public ManualRMICacheManagerPeerProvider() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        //nothing to do here
    }

    /**
     * Time for a cluster to form. This varies considerably, depending on the implementation.
     *
     * @return the time in ms, for a cluster to form
     */
    public long getTimeForClusterToForm() {
        return 0;
    }

    @Override
    public synchronized void registerPeer(String rmiUrl) {
        peerUrls.add(rmiUrl);
    }


    @Override
    public synchronized List<CachePeer> listRemoteCachePeers(Ehcache cache) throws CacheException {
        List<CachePeer> remoteCachePeers = new ArrayList<>();
        for (String rmiUrl : peerUrls) {
            String rmiUrlCacheName = extractCacheName(rmiUrl);

            if (!rmiUrlCacheName.equals(cache.getName())) {
                continue;
            }

            try {
                remoteCachePeers.add(lookupRemoteCachePeer(rmiUrl));
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Looking up rmiUrl {} through exception. This may be normal if a node has gone offline. Or it may indicate network connectivity difficulties",
                            rmiUrl, e);
                }
            }
        }

        return remoteCachePeers;
    }

    @Override
    public void unregisterPeer(String rmiUrl) {
        peerUrls.remove(rmiUrl);
    }
}
