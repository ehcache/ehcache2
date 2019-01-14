package net.sf.ehcache.management;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ManagementRESTServiceConfiguration;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Ludovic Orban
 */
public class ManagementServerLoaderTest {

    @Test
    public void testRegistrationLifecycleStandalone() throws Exception {
        boolean managementAvailable = ManagementServerLoader.isManagementAvailable();
        assertThat(managementAvailable, is(true));

        CacheManager cacheManager1 = createCacheManager("cm-one", "localhost:1234");
        CacheManager cacheManager2 = createCacheManager("cm-two", "localhost:1234");

        try {
            ManagementServerLoader.register(cacheManager1, null, cacheManager1.getConfiguration().getManagementRESTService());
            DummyManagementServerImpl dms = (DummyManagementServerImpl)ManagementServerLoader.MGMT_SVR_BY_BIND.get("localhost:1234").getManagementServer();
            assertThat(dms.status, is(DummyManagementServerImpl.Status.STARTED));
            ManagementServerLoader.register(cacheManager2, null, cacheManager2.getConfiguration().getManagementRESTService());
            assertThat(dms.status, is(DummyManagementServerImpl.Status.STARTED));

            ManagementServerLoader.unregister(cacheManager2.getConfiguration()
                .getManagementRESTService()
                .getBind(), cacheManager2);
            assertThat(dms.status, is(DummyManagementServerImpl.Status.STARTED));
            ManagementServerLoader.unregister(cacheManager1.getConfiguration()
                .getManagementRESTService()
                .getBind(), cacheManager1);
            assertThat(dms.status, is(DummyManagementServerImpl.Status.STOPPED));
        } finally {
            cacheManager2.shutdown();
            cacheManager1.shutdown();
        }
    }

    @Test
    public void testRegistrationLifecycleClustered() throws Exception {
        boolean managementAvailable = ManagementServerLoader.isManagementAvailable();
        assertThat(managementAvailable, is(true));

        CacheManager cacheManager1 = createCacheManager("cm-one", "");
        CacheManager cacheManager2 = createCacheManager("cm-two", "");

        try {
            ManagementServerLoader.register(cacheManager1, "uuid1", cacheManager1.getConfiguration().getManagementRESTService());
            ManagementServerLoader.ManagementServerHolder managementServerHolder = ManagementServerLoader.MGMT_SVR_BY_BIND.get("");
            DummyManagementServerImpl dms = (DummyManagementServerImpl)managementServerHolder.getManagementServer();
            assertThat(dms.status, is(DummyManagementServerImpl.Status.STARTED));

            ManagementServerLoader.register(cacheManager2, "uuid2", cacheManager2.getConfiguration().getManagementRESTService());
            assertThat(dms.status, is(DummyManagementServerImpl.Status.STARTED));

            ManagementServerLoader.unregister("", cacheManager1);
            assertThat(dms.status, is(DummyManagementServerImpl.Status.STARTED));

            ManagementServerLoader.unregister("", cacheManager2);
            assertThat(dms.status, is(DummyManagementServerImpl.Status.STOPPED));
        } finally {
            cacheManager2.shutdown();
            cacheManager1.shutdown();
        }
    }

    private static net.sf.ehcache.CacheManager createCacheManager(String name, String bind) {
        Configuration cfg = new Configuration().name(name);
        ManagementRESTServiceConfiguration managementRESTServiceConfiguration = new ManagementRESTServiceConfiguration();
        managementRESTServiceConfiguration.setBind(bind);
        cfg.managementRESTService(managementRESTServiceConfiguration);
        return new net.sf.ehcache.CacheManager(cfg);
    }

}
