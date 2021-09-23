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
package org.terracotta.modules.ehcache.writebehind;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

public class SerializationWriteBehindType implements Serializable {
  private final String value;
  private final Date moment;
  private final SerializationWriteBehindSubType subType;

  public SerializationWriteBehindType(String value) {
    this.value = value;
    this.moment = new Date();
    this.subType = new SerializationWriteBehindSubType(new BigInteger(String.valueOf(value.hashCode())));
  }

  public String toString() {
    return value + ", " + moment + ", " + subType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SerializationWriteBehindType that = (SerializationWriteBehindType) o;

    if (value != null ? !value.equals(that.value) : that.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

  public static class SerializationWriteBehindSubType implements Serializable {
    private final BigInteger value;

    public SerializationWriteBehindSubType(BigInteger value) {
      this.value = value;
    }

    public BigInteger getValue() {
      return value;
    }
  }
}
