package co.mahsan.zookeeper.service;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED;

@Service
public class ZookeeperService {
    private final CuratorFramework curatorFramework;

    @Autowired
    public ZookeeperService(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
        curatorFramework.start();
    }
    public void createNode(String path) throws Exception {
        if (curatorFramework.checkExists().forPath(path) == null){
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        }
    }
    public void addChild(String parentPath, String child, String data) throws Exception {
        String fullPath = parentPath + "/" + child;
        curatorFramework.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(fullPath, data.getBytes());
    }

//    public void addWatcher(String path) throws Exception {
//        PathChildrenCache cache = new PathChildrenCache(curatorFramework, path, true);
//        cache.start();
//
//        cache.getListenable().addListener((curatorFramework, event) -> {
//            if (event.getType().equals(CHILD_ADDED))
//
//        });
//    }

    public CuratorFramework getCuratorFramework() {
        return curatorFramework;
    }
}
