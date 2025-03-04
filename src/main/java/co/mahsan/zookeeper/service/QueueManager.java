package co.mahsan.zookeeper.service;

import jakarta.annotation.PostConstruct;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED;
import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_REMOVED;

@Component
public class QueueManager {

    Logger logger = LogManager.getLogger(QueueManager.class);

    ZookeeperService zookeeperService;

    List<String> resources = new ArrayList<String>();

    @Autowired
    public QueueManager(ZookeeperService zookeeperService) {
        this.zookeeperService = zookeeperService;
    }

    @PostConstruct
    public void init() {
        resources.add("R1");
        resources.add("R2");
        resources.add("R3");

        for (String resource : resources) {
            String resourceExecutionPath = "/"+ resource +"/" + "execution";
            String resourceRequestPath = "/"+ resource +"/" + "request";
            try {
                zookeeperService.createNode(resourceExecutionPath);
                zookeeperService.createNode(resourceRequestPath);
            }catch (Exception e) {
                logger.error("Error creating node", e);
            }
            PathChildrenCache pathRequestCache = new PathChildrenCache(zookeeperService.getCuratorFramework(), resourceRequestPath, true);
            pathRequestCache.getListenable().addListener((client, event) -> {
            if (event.getType().equals(CHILD_ADDED)) {
                try {
                    List<String> executionChildren = client.getChildren().watched().forPath(resourceExecutionPath);
                    if (executionChildren.size()< 2) {
                        String child = getFirstChild(client, resourceRequestPath);

                        zookeeperService.addChild(resourceExecutionPath, child , child);
                    }
                }catch (Exception e) {
                    logger.error("getChildren exception", e);
                }
            }else if (event.getType().equals(CHILD_REMOVED)){
                try {
                    String child = getFirstChild(client, resourceRequestPath);
                    zookeeperService.addChild(resourceExecutionPath, child, child);
                }catch (Exception e) {
                    logger.error("getChildren exception", e);
                }
            }

        });
            }
    }

    private List<String> getChildren(CuratorFramework client, String path) throws Exception {
        return  client.getChildren().watched().forPath(path);
    }

    private String getFirstChild(CuratorFramework client, String path) throws Exception {
        List<String> children = getChildren(client, path);
        Collections.sort(children);
        return children.get(0);
    }

}
