package co.mahsan.zookeeper.service;


import jakarta.annotation.PostConstruct;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED;

@Service
public class ZookeeperService {
//    private final CuratorFramework curatorFramework;
//
//    @Autowired
//    public ZookeeperService(CuratorFramework curatorFramework) {
//        this.curatorFramework = curatorFramework;
//        curatorFramework.start();
//    }
//    public void createNode(String path) throws Exception {
//        if (curatorFramework.checkExists().forPath(path) == null){
//            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
//        }
//    }
//    public void addChild(String parentPath, String child, String data) throws Exception {
//        String fullPath = parentPath + "/" + child;
//        curatorFramework.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(fullPath, data.getBytes());
//    }
//
//    private List<String> getChildren( String path) throws Exception {
//        return  curatorFramework.getChildren().forPath(path);
//    }
//
//    public String getFirstChild(String path) throws Exception {
//        List<String> children = getChildren(path);
//        Collections.sort(children);
//        return children.get(0);
//    }
//
////    public void addWatcher(String path) throws Exception {
////        PathChildrenCache cache = new PathChildrenCache(curatorFramework, path, true);
////        cache.start();
////
////        cache.getListenable().addListener((curatorFramework, event) -> {
////            if (event.getType().equals(CHILD_ADDED))
////
////        });
////    }
//
//    public CuratorFramework getCuratorFramework() {
//        return curatorFramework;
//    }
    ///////////////////////////////////////////////////////////////////////
    private final RequestZNodeManager requestManager;
    private final ExecutionZNodeManager executionManager;
    private final ResourceConfig resourceConfig;
    private final CuratorFramework client;

    private final Map<String, PathChildrenCache> caches = new ConcurrentHashMap<>();
    private final Map<String, Boolean> listenerStatus = new ConcurrentHashMap<>();

    @Autowired
    public ZookeeperService(@Lazy RequestZNodeManager requestManager, @Lazy ExecutionZNodeManager executionManager, ResourceConfig resourceConfig, CuratorFramework client) {
        this.requestManager = requestManager;
        this.executionManager = executionManager;
        this.resourceConfig = resourceConfig;
        this.client = client;
    }

    @PostConstruct
    public void initialize() throws Exception {
        for (String resource : resourceConfig.getResources()) {


            String requestPath = "/" + resource + "/request";
            String executionPath = "/" + resource + "/execution";


            if (client.checkExists().forPath(requestPath) == null) {
                client.create().creatingParentsIfNeeded().forPath(requestPath);
//                PathChildrenCache requestCache = new PathChildrenCache(client, requestPath, true);
//                requestCache.start();
//                requestCache.getListenable().addListener((c, event) -> {
//                    if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
//                        System.out.println("Child added to request: " + event.getData().getPath());
////                        requestManager.handleNodeChange(c, event, resource);
//                        handleNewRequest(resource);
//                    }
//                });
            }
            if (client.checkExists().forPath(executionPath) == null) {
                client.create().creatingParentsIfNeeded().forPath(executionPath);
//                PathChildrenCache executionCache  = new PathChildrenCache(client, executionPath, true);
//                executionCache.start();
//                executionCache.getListenable().addListener((c, event) -> {
//                   if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
//                       System.out.println("Child removed from execution: " + event.getData().getPath());
//                       handleExecutionRemoval(resource);
//                   }
//                });
            }
            if (!listenerStatus.getOrDefault(requestPath, false)) {
                PathChildrenCache cache = caches.computeIfAbsent(requestPath,
                        k -> new PathChildrenCache(client, k, true));

                cache.getListenable().addListener((c, event) -> {
                    if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                        System.out.println("Child added to request: " + event.getData().getPath());
                        handleNewRequest(resource);
                    }
                });

                cache.start();
                listenerStatus.put(requestPath, true);
            }
            if (!listenerStatus.getOrDefault(executionPath, false)) {
                PathChildrenCache cache = caches.computeIfAbsent(executionPath,
                        k -> new PathChildrenCache(client, k, true));

                cache.getListenable().addListener((c, event) -> {
                    if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                        System.out.println("Child removed from execution: " + event.getData().getPath());
                        handleExecutionRemoval(resource);
                    }
                });

                cache.start();
                listenerStatus.put(executionPath, true);
            }
        }
    }

    public void handleNewRequest(String resource) throws Exception {
        if (executionManager.hasCapacity(resource)) {
            List<String> children = client.getChildren().forPath("/" + resource + "/request");
            if (children != null) {
                Collections.sort(children);

//            String nodeName = "/" + resource + "/request/"+children.get(0);
                requestManager.removeChild(resource, children.get(0));
                executionManager.addChild(resource, children.get(0));
            }
        }
    }

    public void handleExecutionRemoval(String resource) throws Exception {
        List<String> requestChildren = requestManager.getChildren(resource);
        if (!requestChildren.isEmpty()) {
            String oldestChild = requestChildren.stream().min(String::compareTo).orElse(null);
            if (oldestChild != null) {
                requestManager.removeChild(resource, oldestChild);
                executionManager.addChild(resource, oldestChild);
            }
        }
    }

    public void addChild(String resource, String childName, byte[] data) throws Exception {
        requestManager.addChild(resource, childName, data);
    }

    public void processNewRequestNode(String resource) throws Exception {
        handleNewRequest(resource);
    }

    public void remove(String resource) throws Exception {
        executionManager.removeChild(resource);
    }

    public void removeResource(String resource) throws Exception {
        String requestPath = "/" + resource + "/request";
        String executionPath = "/" + resource + "/execution";
        if (client.checkExists().forPath(requestPath) != null) {
            removeListener(requestPath);
            client.delete().forPath(requestPath);
        }
        if (client.checkExists().forPath(executionPath) != null) {
            removeListener(executionPath);
            client.delete().forPath(executionPath);
        }
    }

    public void removeListener(String nodePath) throws Exception {
        PathChildrenCache cache = caches.remove(nodePath);
        if (cache != null) {
            cache.close();
            listenerStatus.remove(nodePath);
        }
    }
}
