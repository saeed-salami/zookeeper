package co.mahsan.zookeeper.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExecutionZNodeManager implements ZNodeManager{
    private static final int MAX_EXECUTION_CHILDREN = 5;
    private final CuratorFramework client;

    @Autowired
    public ExecutionZNodeManager(CuratorFramework client) {
        this.client = client;
    }

    @Override
    public void initialize(String resource) throws Exception {
        String path = "/" + resource + "/execution";
        if (client.checkExists().forPath(path) == null) {
            client.create().creatingParentsIfNeeded().forPath(path);
        }
    }

    @Override
    public void handleNodeChange(CuratorFramework client, PathChildrenCacheEvent event, String resource) throws Exception {
        if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
            System.out.println("Child Removed");
        }
    }

    public List<String> getChildren(String resource) throws Exception {
        return client.getChildren().forPath("/" + resource + "/execution");
    }

    public void addChild(String resource, String childName) throws Exception {
        client.create().forPath("/" + resource + "/execution/" + childName);
    }

    public boolean hasCapacity(String resource) throws Exception {
        return getChildren(resource).size() < MAX_EXECUTION_CHILDREN;
    }

    public void removeChild(String resource) throws Exception {
        List<String> children = getChildren(resource);
        children.remove(0);
    }
}
