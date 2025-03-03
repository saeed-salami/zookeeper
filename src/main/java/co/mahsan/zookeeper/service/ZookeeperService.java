package co.mahsan.zookeeper.service;


import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZookeeperService {
    private final CuratorFramework curatorFramework;

    @Autowired
    public ZookeeperService(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
        curatorFramework.start();
    }
    public void createNode(String path, String data) throws Exception {
        if (curatorFramework.checkExists().forPath(path) == null){
            curatorFramework.create().creatingParentsIfNeeded().forPath(path, data.getBytes());
        }
    }
    public void addChild(String parentPath, String childPath, String data) throws Exception {
        String fullPath = parentPath + "/" + childPath;
        curatorFramework.create().forPath(fullPath, data.getBytes());
    }
}
