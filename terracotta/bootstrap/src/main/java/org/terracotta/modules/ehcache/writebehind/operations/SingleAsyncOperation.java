/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 */
package org.terracotta.modules.ehcache.writebehind.operations;

import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;

import java.io.IOException;
import java.io.Serializable;

/**
 * Interface to implement single operations that are performed in the write behind implementation that using an
 * AsyncCoordinator underneath
 * 
 * @author Abhishek Maheshwari
 */
public interface SingleAsyncOperation extends Serializable {
  /**
   * Perform this operation as a single execution with the provided cache writer
   * 
   * @param cacheWriter the cache writer this operation should be performed upon
   */
  public void performSingleOperation(CacheWriter cacheWriter) throws ClassNotFoundException, IOException;

  /**
   * Retrieves the key for this operation.
   * 
   * @return this operation's key
   */
  Object getKey();

  Element getElement();

  /**
   * Retrieves the moment when the operation was created.
   * 
   * @return the creation time in milliseconds
   */
  public long getCreationTime();

  /**
   * This method will be called to throw the item away.
   * 
   * @param cacheWriter
   * @param e
   */
  void throwAwayElement(CacheWriter cacheWriter, RuntimeException e);

}
