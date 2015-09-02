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

package net.sf.ehcache.store;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;
import net.sf.ehcache.transaction.SoftLockID;

/**
 * @author Alex Snaps
 */
public class TxCopyStrategyHandler extends CopyStrategyHandler {

    /**
     * Creates a TxCopyStrategyHandler based on the copy configuration
     *
     * @param copyOnRead   copy on read flag
     * @param copyOnWrite  copy on write flag
     * @param copyStrategy the copy strategy to use
     * @param loader
     */
    public TxCopyStrategyHandler(final boolean copyOnRead, final boolean copyOnWrite,
                                 final ReadWriteCopyStrategy<Element> copyStrategy, final ClassLoader loader) {
        super(copyOnRead, copyOnWrite, copyStrategy, loader);
    }

    @Override
    public Element copyElementForReadIfNeeded(final Element element) {
        final Object objectValue = element.getObjectValue();
        if (objectValue instanceof SoftLockID) {
            return super.copyElementForReadIfNeeded(((SoftLockID)objectValue).getOldElement());
        }
        return super.copyElementForReadIfNeeded(element);
    }
}
