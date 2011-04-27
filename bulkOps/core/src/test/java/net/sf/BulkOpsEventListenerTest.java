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

package net.sf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.sf.ehcache.AbstractCacheTest;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.junit.Test;

public class BulkOpsEventListenerTest extends AbstractCacheTest {

    @Test
    public void testBulkOpsEventListener() throws Throwable{
        Cache cache = new Cache("cache", 1000, true, false, 100000, 200000, false, 1);
        manager.addCache(cache);

        TestCacheEventListener eventListener = new TestCacheEventListener();
        cache.getCacheEventNotificationService().registerListener(eventListener);

        int numOfElements = 100;
        Set<Element> elements = new HashSet<Element>();
        for(int i = 0; i < numOfElements; i++){
            elements.add(new Element("key" + i, "value" + i));
        }
        cache.putAll(elements);
        assertEquals(numOfElements, cache.getSize());
        assertEquals(numOfElements, eventListener.elementsPut.size());
        assertEquals(elements, eventListener.elementsPut);

        Set keySet1 = new HashSet<String>();
        for(int i = 0; i < numOfElements; i++){
            keySet1.add("key"+i);
        }

        Map<Object, Element> rv = cache.getAll(keySet1);
        assertEquals(numOfElements, rv.size());

        for(Element element : rv.values()){
            assertTrue(elements.contains(element));
        }

        Collection<Element> values = rv.values();
        for(Element element : elements){
            assertTrue(values.contains(element));
        }

        Random rand = new Random();
        Set keySet2 = new HashSet<String>();
        for(int i = 0; i < numOfElements/2; i++){
            keySet2.add("key" + rand.nextInt(numOfElements));
        }

        rv = cache.getAll(keySet2);
        assertEquals(keySet2.size(), rv.size());

        for(Element element : rv.values()){
            assertTrue(elements.contains(element));
        }

        assertEquals(keySet2, rv.keySet());

        cache.removeAll(keySet2);
        assertEquals(numOfElements - keySet2.size(), cache.getSize());
        assertEquals(keySet2.size(), eventListener.elementsRemoved.size());
        Set<String> removedKeySet = new HashSet<String>();
        for(Element element : eventListener.elementsRemoved){
            removedKeySet.add(element.getKey().toString());
        }
        assertEquals(keySet2, removedKeySet);

        for(Object key : keySet2){
            assertNull(cache.get(key));
        }

        cache.removeAll();
        assertEquals(0, cache.getSize());
    }


    private static class TestCacheEventListener implements CacheEventListener{
        Set<Element> elementsPut = Collections.synchronizedSet(new HashSet<Element>());
        Set<Element> elementsUpdated = Collections.synchronizedSet(new HashSet<Element>());
        Set<Element> elementsRemoved = Collections.synchronizedSet(new HashSet<Element>());

        public void dispose() {
            // TODO Auto-generated method stub

        }

        public void notifyElementEvicted(Ehcache cache, Element element) {
            // TODO Auto-generated method stub

        }

        public void notifyElementExpired(Ehcache cache, Element element) {
            // TODO Auto-generated method stub

        }

        public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
            elementsPut.add(element);
        }

        public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
            elementsRemoved.add(element);
        }

        public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
            elementsUpdated.add(element);
        }

        public void notifyRemoveAll(Ehcache cache) {
            // TODO Auto-generated method stub

        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

    }
}
