package lu.snt.iot.web.intra.core;

import lu.snt.iot.web.intra.cmp.MainPage;
import org.kevoree.log.Log;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.handler.AbstractResourceHandler;
import org.webbitserver.handler.StaticFileHandler;

import java.io.File;
import java.net.HttpCookie;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: gregory.nain
 * Date: 11/11/2013
 * Time: 16:09
 * To change this template use File | Settings | File Templates.
 */
public class IntraSecuredHandler implements HttpHandler {

    private AuthenticationHandler authManager;
    private AbstractResourceHandler securedStaticHandler;


    public IntraSecuredHandler(MainPage mainPage, AuthenticationHandler authManager) {
        this.authManager = authManager;

        File baseStaticDir = null;
        File staticDirFromRoot = new File("iot-intra/src/main/resources/secured");
        if(staticDirFromRoot.exists() && staticDirFromRoot.isDirectory()){
            baseStaticDir = staticDirFromRoot;
        } else {
            File staticDirFromProject = new File("src/main/resources/secured");
            if(staticDirFromProject.exists() && staticDirFromRoot.isDirectory()){
                baseStaticDir = staticDirFromProject;
            } else {
                baseStaticDir = null;
            }
        }

        if(baseStaticDir ==  null) {
            securedStaticHandler = new EmbedHandler(mainPage, "secured");
        } else {
            securedStaticHandler = new StaticFileHandler(baseStaticDir);
        }

    }

    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {
        Log.trace("Received a Secured request:" + httpRequest.toString());
        Log.debug("Cookies:" + Arrays.toString(httpRequest.cookies().toArray()));
        HttpCookie cookie = httpRequest.cookie("iot-token");
        if(cookie != null && !cookie.hasExpired()) {
            Log.trace("Got the cookie");
            String sessionId = cookie.getValue();
            if(authManager.isSessionActive(sessionId)) {
                Log.trace("Session Active :-)");

                String uri = httpRequest.uri();
                Log.trace("Request Secured Uri:" + uri);
                httpRequest.uri(uri.replace("/intra","/"));
                securedStaticHandler.handleHttpRequest(httpRequest, httpResponse, httpControl);
            } else {
                Log.trace("Session Not Active");
            }
//            httpResponse.status(200).content("Welcome").end();
        } else {
            Log.error("Cookie not found " + Arrays.toString(httpRequest.cookies().toArray()));
            httpResponse.status(401).content("Cookie not found").end();
        }
    }
}
