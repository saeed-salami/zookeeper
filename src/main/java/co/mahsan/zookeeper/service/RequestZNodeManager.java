package co.mahsan.zookeeper.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
public class RequestZNodeManager implements ZNodeManager{
    private final CuratorFramework client;
    private ExecutionZNodeManager executionZNodeManager;

    @Autowired
    public RequestZNodeManager(CuratorFramework client, ExecutionZNodeManager executionZNodeManager) {
        this.client = client;
        this.executionZNodeManager = executionZNodeManager;
    }

    @Override
    public void initialize(String resource) throws Exception {
        String path = "/" + resource + "/request";
        if (client.checkExists().forPath(path) == null) {
            client.create().creatingParentsIfNeeded().forPath(path);
        }
    }

    @Override
    public void handleNodeChange(CuratorFramework client, PathChildrenCacheEvent event, String resource) throws Exception {
        if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
            System.out.println("Child added: " + event.getData().getPath());
        }
    }

    public List<String> getChildren(String resource) throws Exception {
        return client.getChildren().forPath("/" + resource + "/request");
    }

    public void removeChild(String resource, String childName) throws Exception {
        client.delete().forPath("/" + resource + "/request/" + childName);
    }

    public void addChild(String resource, String childName, byte[] data) throws Exception {
        String path = "/" + resource + "/request/" + childName;
        if (client.checkExists().forPath(path) == null)
            client.create().forPath(path, data);

//
//        }else {
//            throw new RuntimeException("Node already exists: " + path);
//        }
    }

}
