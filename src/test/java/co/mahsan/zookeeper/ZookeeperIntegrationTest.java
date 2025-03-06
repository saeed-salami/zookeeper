//package co.mahsan.zookeeper;
//
//import co.mahsan.zookeeper.service.ExecutionZNodeManager;
//import co.mahsan.zookeeper.service.RequestZNodeManager;
//import co.mahsan.zookeeper.service.ZookeeperService;
//import org.apache.curator.framework.CuratorFramework;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@TestPropertySource(properties = {"app.resources=testResource6"})
//public class ZookeeperIntegrationTest {
//    @Autowired
//    private CuratorFramework client;
//
//    @Autowired
//    private ZookeeperService zookeeperService;
//
//    @Autowired
//    private RequestZNodeManager requestZNodeManager;
//
//    @Autowired
//    private ExecutionZNodeManager executionZNodeManager;
//
//    private static final int SESSION_TIMEOUT_MS = 60000;
//    private static final int CONNECTION_TIMEOUT_MS = 15000;
//
//
//    @Test
//    public void testDynamicEventHandling() throws Exception {
//        String resource = "testResource6";
//        String requestPath = "/" + resource + "/request";
//        String executionPath = "/" + resource + "/execution";
//
//        CountDownLatch addLatch = new CountDownLatch(1);
//        CountDownLatch removeLatch = new CountDownLatch(1);
//
//        zookeeperService.initialize(); // اطمینان از ایجاد مسیرها
//
//        String childName = "testChild7";
//        client.blockUntilConnected(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
////        client.create().forPath(requestPath + "/" + childName, "testData".getBytes());
//        zookeeperService.addChild(resource,childName,"testData".getBytes());
//
//
//        Thread.sleep(10000);
//
//        // بررسی انتقال نود از request به execution
//        assertNull(client.checkExists().forPath(requestPath + "/" + childName), "Child should be removed from request");
//        assertNotNull(client.checkExists().forPath(executionPath + "/" + childName), "Child should be added to execution");
//
//        // حذف نود از execution
//        client.delete().forPath(executionPath + "/" + childName);
//
//        // انتظار برای پردازش رویداد CHILD_REMOVED
//        Thread.sleep(1000); // صبر برای پردازش رویدادها
//
//        // بررسی وضعیت نهایی
//        assertTrue(client.getChildren().forPath(requestPath).isEmpty(), "Request node should be empty");
//        assertTrue(client.getChildren().forPath(executionPath).isEmpty(), "Execution node should be empty");
//
//    }
//}