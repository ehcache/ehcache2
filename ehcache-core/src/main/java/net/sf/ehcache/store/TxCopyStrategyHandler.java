package net.sf.ehcache.store;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;
import net.sf.ehcache.transaction.SoftLockID;

/**
 * @author Alex Snaps
 */
public class TxCopyStrategyHandler extends CopyStrategyHandler {
  /**
   * Creates a CopyStrategyHandler based on the copy configuration
   *
   * @param copyOnRead   copy on read flag
   * @param copyOnWrite  copy on write flag
   * @param copyStrategy the copy strategy to use
   * @param loader
   */
  public TxCopyStrategyHandler(final boolean copyOnRead, final boolean copyOnWrite, final ReadWriteCopyStrategy<Element> copyStrategy, final ClassLoader loader) {
    super(copyOnRead, copyOnWrite, copyStrategy, loader);
  }

  @Override
  public Element copyElementForReadIfNeeded(final Element element) {
    final Object objectValue = element.getObjectValue();
    if(objectValue instanceof SoftLockID) {
      return super.copyElementForReadIfNeeded(((SoftLockID)objectValue).getOldElement());
    }
    return super.copyElementForReadIfNeeded(element);
  }
}
