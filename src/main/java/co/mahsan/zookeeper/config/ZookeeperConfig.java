package co.mahsan.zookeeper.config;

import co.mahsan.zookeeper.service.ExecutionZNodeManager;
import co.mahsan.zookeeper.service.RequestZNodeManager;
import co.mahsan.zookeeper.service.ResourceConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfig {

    @Value("${spring.cloud.zookeeper.connect-string}")
    private String connectString;

    @Value("${spring.cloud.zookeeper.baseSleepTimeMs}")
    private int baseSleepTimeMs;

    @Value("${spring.cloud.zookeeper.maxRetries}")
    private int maxRetries;

    @Bean
    public CuratorFramework curatorFramework() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                connectString,
                new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries));
        client.start();
        return client;
    }

    private void setupWatchers(CuratorFramework client, RequestZNodeManager requestManager, ExecutionZNodeManager executionManager, String resource) throws Exception {
        PathChildrenCache requestCache = new PathChildrenCache(client, "/" + resource + "/request", true);
        requestCache.start();
        requestCache.getListenable().addListener((c, event) -> requestManager.handleNodeChange(c, event, resource));

        PathChildrenCache executionCache = new PathChildrenCache(client, "/" + resource + "/execution", true);
        executionCache.start();
        executionCache.getListenable().addListener((c, event) -> executionManager.handleNodeChange(c, event, resource));
    }
}
