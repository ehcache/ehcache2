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
package net.sf.ehcache.store.compound;

import net.sf.ehcache.Element;

/**
 * @author Alex Snaps
 * @author Ludovic Orban
 */
public class ImmutableValueElementCopyStrategy implements ReadWriteCopyStrategy<Element> {

    private static final long serialVersionUID = 6938731518478806173L;
    
    private final ReadWriteSerializationCopyStrategy copyStrategy = new ReadWriteSerializationCopyStrategy();

    /**
     * Deep copies some object and returns an internal storage-ready copy
     *
     * @param value the value to copy
     * @return the storage-ready copy
     */
    public Element copyForWrite(Element value, ClassLoader loader) {
        if (value == null) {
            return null;
        }
        return copyStrategy.duplicateElementWithNewValue(value, value.getObjectValue());
    }

    /**
     * Reconstruct an object from its storage-ready copy.
     *
     * @param storedValue the storage-ready copy
     * @return the original object
     */
    public Element copyForRead(Element storedValue, ClassLoader loader) {
        if (storedValue == null) {
            return null;
        }
        return copyStrategy.duplicateElementWithNewValue(storedValue, storedValue.getObjectValue());
    }
}
