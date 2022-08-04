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

package net.sf.ehcache.pool.sizeof;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.pool.sizeof.filter.PassThroughFilter;
import net.sf.ehcache.pool.sizeof.filter.SizeOfFilter;

/**
 * All constructors will throw UnsupportedOperationException if theUnsafe isn't accessible on this platform
 * @author Chris Dennis
 */
@SuppressWarnings("restriction")
public class UnsafeSizeOf extends SizeOf {


    private static final Logger LOGGER = LoggerFactory.getLogger(UnsafeSizeOf.class);

    /**
     * Builds a new SizeOf that will not filter fields and will cache reflected fields
     *
     * @throws UnsupportedOperationException If Unsafe isn't accessible
     * @see #UnsafeSizeOf(net.sf.ehcache.pool.sizeof.filter.SizeOfFilter, boolean)
     */
    public UnsafeSizeOf() throws UnsupportedOperationException {
        this(new PassThroughFilter());
    }

    /**
     * Builds a new SizeOf that will filter fields according to the provided filter and will cache reflected fields
     *
     * @param filter The filter to apply
     * @throws UnsupportedOperationException If Unsafe isn't accessible
     * @see #UnsafeSizeOf(net.sf.ehcache.pool.sizeof.filter.SizeOfFilter, boolean)
     * @see SizeOfFilter
     */
    public UnsafeSizeOf(SizeOfFilter filter) throws UnsupportedOperationException {
        this(filter, true);
    }

    /**
     * Builds a new SizeOf that will filter fields according to the provided filter
     *
     * @param filter The filter to apply
     * @param caching     whether to cache reflected fields
     * @throws UnsupportedOperationException If Unsafe isn't accessible
     * @see SizeOfFilter
     */
    public UnsafeSizeOf(SizeOfFilter filter, boolean caching) throws UnsupportedOperationException {
        super(filter, caching);
        throw new UnsupportedOperationException("sun.misc.Unsafe instance not accessible");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long sizeOf(Object obj) {
        return -1;
    }
}
