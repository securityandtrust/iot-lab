package lu.snt.iot.web.intra.core;

import org.kevoree.log.Log;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

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
public class AuthenticationHandler implements HttpHandler {

    private HashMap<String, String> logPassMap = new HashMap<>();
    private HashMap<UUID, Long> sessionsMap = new HashMap<>();

    public AuthenticationHandler() {
        logPassMap.put("iot", "iot");
    }

    public boolean isSessionActive(String sessionId) {
        UUID id = UUID.fromString(sessionId);
        Long timeout = sessionsMap.get(id);
        return timeout != null && timeout > System.currentTimeMillis();
    }

    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {
        if(httpRequest.method().equalsIgnoreCase("post")) {
            Log.trace("Received a request:" + httpRequest.toString());
            String login = httpRequest.postParam("login");
            String password = httpRequest.postParam("password");
            String storedPass = logPassMap.get(login);
            if(storedPass != null && storedPass.equals(password)) {
                Log.trace("Session creation");
                UUID sessionId = UUID.randomUUID();
                sessionsMap.put(sessionId, System.currentTimeMillis() + (1000 * 60 * 60)); // creates a session for an hour
                HttpCookie cookie = new HttpCookie("iot-token", sessionId.toString());
                //cookie.setDomain("www.sntiotlab.lu");
                cookie.setMaxAge(60*60);
                cookie.setPath("/");
                cookie.setSecure(false);
                httpResponse.cookie(cookie);
                httpResponse.header("Location", "/intra");
                httpResponse.status(302);
                httpResponse.end();
            } else {
                Log.warn("Unauthorized ! Login:" + login + " password:" + password + " stored:" + storedPass);
                httpResponse.status(401).content("<html><body><h2>Unauthorized !</h2></body></html>").end();
            }

        } else {
            Log.error("HttpRequest received with unsupported method:" + httpRequest.method());
            httpResponse.status(500).content("HttpRequest received with unsupported method:" + httpRequest.method()).end();
        }
    }
}
