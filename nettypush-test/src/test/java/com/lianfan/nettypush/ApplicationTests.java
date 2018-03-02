package com.lianfan.nettypush;

import org.caizs.nettypush.core.bootstrap.LinkBootstrap;
import org.caizs.nettypush.core.bootstrap.LinkClient;
import org.caizs.nettypush.core.common.ConfigLoader;
import org.caizs.nettypush.core.msg.Msg;
import com.lianfan.nettypush.test.demo.DemoBean;
import com.lianfan.nettypush.test.demo.DemoClientMsgHandler;
import com.lianfan.nettypush.test.demo.DemoServerMsgHandler;
import org.junit.Test;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ApplicationTests {

    @Test
    public void startClient() throws InterruptedException {
        ConfigLoader.load("application1.properties");

        new LinkBootstrap().addClientHandler(new DemoClientMsgHandler()).start();

        DemoBean user = new DemoBean("request", "request id");
        Msg msg = new Msg("query", user);
        for (int i = 0; i < 10000; i++) {
            Thread.sleep(4000);
            LinkClient.push(msg);
        }
    }

    @Test
    public void startBridge() throws InterruptedException {
        ConfigLoader.load("application2.properties");

        new LinkBootstrap().addClientHandler(new DemoClientMsgHandler())
                           .addServerHandler(new DemoServerMsgHandler()).start();
        Thread.sleep(1000000000);
    }

    @Test
    public void startServer() throws Exception {
        ConfigLoader.load("application3.properties");

        new LinkBootstrap().addServerHandler(new DemoServerMsgHandler()).start();

        Thread.sleep(1000000000);
    }


}
