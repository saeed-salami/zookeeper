package co.mahsan.zookeeper;

import co.mahsan.zookeeper.service.ZookeeperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ZookeeperApplicationTests {

    @Autowired
    ZookeeperService zookeeperService;

    @Test
    void contextLoads() {
    }

    @Test
    void testZookeeper() {
        try {
            zookeeperService.addChild("/R1/execution", "C1", "123");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
