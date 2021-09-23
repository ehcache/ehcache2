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
package org.terracotta.ehcache.tests.xa;

import java.util.Arrays;

import javax.transaction.xa.Xid;

public class DummyXid implements Xid {

    private int formatId = 123456;
    private byte[] gtrid;
    private byte[] bqual;

    public DummyXid(long gtrid, long bqual) {
        this.gtrid = longToBytes(gtrid);
        this.bqual = longToBytes(bqual);
    }

    public DummyXid(Xid xid) {
        this.formatId = xid.getFormatId();
        this.gtrid = xid.getGlobalTransactionId();
        this.bqual = xid.getBranchQualifier();
    }

    public int getFormatId() {
        return formatId;
    }

    public byte[] getGlobalTransactionId() {
        return gtrid;
    }

    public byte[] getBranchQualifier() {
        return bqual;
    }

    @Override
    public int hashCode() {
        return formatId + arrayHashCode(gtrid) + arrayHashCode(bqual);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DummyXid) {
            DummyXid otherXid = (DummyXid) o;
            return  formatId == otherXid.formatId &&
                    Arrays.equals(gtrid, otherXid.gtrid) &&
                    Arrays.equals(bqual, otherXid.bqual);
        }
        return false;
    }

    @Override
    public String toString() {
        return "DummyXid [" + hashCode() + "]";
    }

    private static int arrayHashCode(byte[] uid) {
        int hash = 0;
        for (int i = uid.length -1; i > 0 ;i--) {
           hash <<= 1;

           if ( hash < 0 ) {
              hash |= 1;
           }

           hash ^= uid[i];
        }
        return hash;
    }

    public static byte[] longToBytes(long aLong) {
        byte[] array = new byte[8];

        array[7] = (byte) (aLong & 0xff);
        array[6] = (byte) ((aLong >> 8) & 0xff);
        array[5] = (byte) ((aLong >> 16) & 0xff);
        array[4] = (byte) ((aLong >> 24) & 0xff);
        array[3] = (byte) ((aLong >> 32) & 0xff);
        array[2] = (byte) ((aLong >> 40) & 0xff);
        array[1] = (byte) ((aLong >> 48) & 0xff);
        array[0] = (byte) ((aLong >> 56) & 0xff);

        return array;
    }

}
