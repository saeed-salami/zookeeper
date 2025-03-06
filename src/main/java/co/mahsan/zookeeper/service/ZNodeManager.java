package co.mahsan.zookeeper.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

public interface ZNodeManager {
    void initialize(String resource) throws Exception;
    void handleNodeChange(CuratorFramework client, PathChildrenCacheEvent event, String resource) throws Exception;

}
