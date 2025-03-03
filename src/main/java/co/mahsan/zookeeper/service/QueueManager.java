package co.mahsan.zookeeper.service;

import org.apache.curator.framework.CuratorFramework;

public class QueueManager {
    CuratorFramework curatorFramework;

    public QueueManager(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }
}
