package co.mahsan.zookeeper;

import co.mahsan.zookeeper.service.ZookeeperService;
import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ZookeeperApplicationTests {

    @Autowired
    ZookeeperService zookeeperService;

    @Autowired
    CuratorFramework client;

    private static final int CONNECTION_TIMEOUT_MS = 30000;

//    @Test
//    void contextLoads() {
//    }

    @Test
    void testZookeeper() {
        String childName = "C10";
        String resource = "resource1";
        try {
            zookeeperService.addChild(resource, childName, "c3".getBytes());
            String requestPath = "/" + resource + "/request";
            String executionPath = "/" + resource + "/execution";
            Thread.sleep(10000);

            client.blockUntilConnected(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            assertNull(client.checkExists().forPath(requestPath + "/" + childName), "Child should be removed from request");
            assertNotNull(client.checkExists().forPath(executionPath + "/" + childName), "Child should be added to execution");


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
//    void testZookeeper2() {
//        try {
//            zookeeperService.remove("resource1");
//        }catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

}
