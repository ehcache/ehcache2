package net.sf.ehcache.store.compound;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import net.sf.ehcache.EhcacheDefaultClassLoader;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.DefaultElementValueComparator;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author teck
 */
public class TCCLReadWriteSerializationCopyStrategyTest {

    @Test
    public void test() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration().copyOnRead(true).copyOnWrite(true);
        ReadWriteCopyStrategy<Element> copyStrategy = cacheConfiguration.getCopyStrategy();
        DefaultElementValueComparator comparator = new DefaultElementValueComparator(cacheConfiguration);
        ClassLoader defaultLoader = EhcacheDefaultClassLoader.getInstance();
        
        
        {
            // loaded via TCCL
            Element storageValue = copyStrategy.copyForWrite(new Element(1, new Foo(42)), defaultLoader);
            Assert.assertTrue(storageValue.getObjectValue() instanceof byte[]);
            Assert.assertTrue(comparator.equals(copyStrategy.copyForWrite(new Element(1, new Foo(42)), defaultLoader), (storageValue)));
        }

        {
            // loaded via serialization class resolve
            Thread.currentThread().setContextClassLoader(null);
            Element storageValue = copyStrategy.copyForWrite(new Element(1, new Foo(42)), defaultLoader);
            Assert.assertTrue(storageValue.getObjectValue() instanceof byte[]);
            Assert.assertTrue(comparator.equals(copyStrategy.copyForWrite(new Element(1, new Foo(42)), defaultLoader), (storageValue)));
        }

        {
            // Type only in TCCL
            ClassLoader loader = newLoader();
            Thread.currentThread().setContextClassLoader(loader);

            Object foo = createFooInOtherLoader(loader);
            Assert.assertEquals(loader, foo.getClass().getClassLoader());

            Element storageValue = copyStrategy.copyForWrite(new Element(1, foo), loader);
            Assert.assertTrue(storageValue.getObjectValue() instanceof byte[]);
            Assert.assertTrue(comparator.equals(copyStrategy.copyForWrite(new Element(1, createFooInOtherLoader(loader)), defaultLoader), (storageValue)));
        }
    }

    private Object createFooInOtherLoader(ClassLoader loader) throws Exception {
        Class c = loader.loadClass(Foo.class.getName());
        return c.getConstructor(Integer.TYPE).newInstance(42);
    }

    public static class Foo implements Serializable {

        private final int val;

        public Foo(int val) {
            this.val = val;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Foo) {
                return ((Foo) obj).val == val;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return val;
        }
    }

    private static ClassLoader newLoader() {
        String pathSeparator = System.getProperty("path.separator");
        String[] classPathEntries = System.getProperty("java.class.path").split(pathSeparator);
        URL[] urls = Arrays.stream(classPathEntries).map(s -> {
            try {
                return new File(s).toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }).toArray(URL[]::new);
        return new URLClassLoader(urls, null);
    }

}
