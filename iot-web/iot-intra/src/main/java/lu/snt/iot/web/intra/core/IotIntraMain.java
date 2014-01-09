package lu.snt.iot.web.intra.core;

import lu.snt.iot.web.intra.cmp.MainPage;
import org.kevoree.log.Log;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 27/08/13
 * Time: 22:22
 */
public class IotIntraMain {

    private WebServer webServer;

    private int port = 8200;
    private String address = "http://localhost:";

    public IotIntraMain() {
        Log.TRACE();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void start(MainPage mainPageCmp) {
        File baseStaticDir = null;
        File staticDirFromRoot = new File("iot-intra/src/main/resources/static");
        if(staticDirFromRoot.exists() && staticDirFromRoot.isDirectory()){
            baseStaticDir = staticDirFromRoot;
        } else {
            File staticDirFromProject = new File("src/main/resources/static");
            if(staticDirFromProject.exists() && staticDirFromRoot.isDirectory()){
                baseStaticDir = staticDirFromProject;
            } else {
                baseStaticDir = null;
            }
        }

        AuthenticationHandler authHandler = new AuthenticationHandler();

        webServer = WebServers.createWebServer(Executors.newSingleThreadExecutor(), new InetSocketAddress(port), URI.create(address + port))
                .add("/authenticate", authHandler)
                .add("/deploy", new DemoDeploymentHandler(mainPageCmp, authHandler))
                .add("/intra", new IntraSecuredHandler(mainPageCmp, authHandler));


        if(baseStaticDir ==  null) {
            webServer.add(new EmbedHandler(mainPageCmp, "static"));
        } else {
            webServer.add(new StaticFileHandler(baseStaticDir));
        }

        webServer.start();
        Log.info("Store Server running at " + webServer.getUri());
    }

    public void stop() {
        webServer.stop();
    }


}
