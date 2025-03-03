package co.mahsan.zookeeper.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class ZookeeperConfig {

    @Value("${spring.cloud.zookeeper.connect-string}")
    private String connectString;

    @Value("${spring.cloud.zookeeper.baseSleepTimeMs}")
    private int baseSleepTimeMs;

    @Value("${spring.cloud.zookeeper.maxRetries}")
    private int maxRetries;

    @Bean
    public CuratorFramework curatorFramework() {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(
                connectString, new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries)
        );
        curatorFramework.start();
        return curatorFramework;
    }
}
