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

public class NativeConfiguration {

    private volatile int concurrency = -1;
    private volatile long initialTableSize = -1;
    private volatile long initialDataSize = -1;
    private volatile long maximalDataSize = -1;

    public int getConcurrency() {
        return concurrency;
    }
    
    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public long getInitialTableSize() {
        return initialTableSize;
    }
    
    public void setInitialTableSize(long initialTableSize) {
        this.initialTableSize = initialTableSize;
    }

    public long getInitialDataSize() {
        return initialDataSize;
    }
    
    public void setInitialDataSize(long initialDataSize) {
        this.initialDataSize = initialDataSize;
    }
    
    public long getMaximalDataSize() {
        return maximalDataSize;
    }
    
    public void setMaximalDataSize(long maximalDataSize) {
        this.maximalDataSize = maximalDataSize;
    }
}
