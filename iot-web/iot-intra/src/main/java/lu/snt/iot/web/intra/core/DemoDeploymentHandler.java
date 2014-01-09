package lu.snt.iot.web.intra.core;

import lu.snt.iot.web.intra.cmp.MainPage;
import org.kevoree.ContainerRoot;
import org.kevoree.api.KevScriptService;
import org.kevoree.api.ModelService;
import org.kevoree.api.handler.UUIDModel;
import org.kevoree.api.handler.UpdateCallback;
import org.kevoree.cloner.DefaultModelCloner;
import org.kevoree.log.Log;
import org.kevoree.modeling.api.ModelCloner;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import java.io.InputStream;
import java.net.HttpCookie;
import java.util.Arrays;

/**
 * Created by gregory.nain on 06/12/2013.
 */
public class DemoDeploymentHandler implements HttpHandler {

    private KevScriptService kevScriptService;
    private ModelService modelService;
    private AuthenticationHandler authenticationHandler;
    private ModelCloner cloner = new DefaultModelCloner();

    private String last = null;

    public DemoDeploymentHandler(MainPage mainPageCmp, AuthenticationHandler authManager) {
        kevScriptService = mainPageCmp.getKevScriptService();
        modelService = mainPageCmp.getModelService();
        this.authenticationHandler = authManager;
    }

    public void handleHttpRequest(HttpRequest httpRequest, final HttpResponse httpResponse, HttpControl httpControl) throws Exception {

        HttpCookie cookie = httpRequest.cookie("iot-token");
        if(cookie != null && !cookie.hasExpired()) {
            Log.trace("Got the cookie");
            String sessionId = cookie.getValue();
            if(authenticationHandler.isSessionActive(sessionId)) {

                String targetDemo = httpRequest.postParam("targetdemo");

                UUIDModel model = modelService.getCurrentModel();
                ContainerRoot localModel = cloner.clone(model.getModel());

                //InputStream file = getClass().getClassLoader().getResourceAsStream("secured/kevs/"+targetDemo+".kevs");
                //Log.debug("Looked for :"+"secured/kevs/"+targetDemo+".kevs"+" found:" + file);

                if(last != null) {
                    try {
                        kevScriptService.execute("remove host." + last, localModel);
                    } catch(Exception e) {}//ignore

                }
                last = targetDemo;
                String addCompScript = "include mvn:lu.snt.iot.web:lu.snt.iot.web.display:1-SNAPSHOT\n" +
                        "add host."+targetDemo+" : "+targetDemo.substring(0,1).toUpperCase() + targetDemo.substring(1)+"Page\n" +
                        "set host."+targetDemo+".http_port = \"6002\"\n";

                Log.debug(addCompScript);
                kevScriptService.execute(addCompScript, localModel);

                modelService.update(localModel, new UpdateCallback() {
                    @Override
                    public void run(Boolean aBoolean) {
                        if(aBoolean) {
                            httpResponse.status(200).end();
                        } else
                            httpResponse.status(500).end();
                    }
                });

            } else {
                Log.trace("Session Not Active");
            }
        } else {
            Log.error("Cookie not found " + Arrays.toString(httpRequest.cookies().toArray()));
            httpResponse.status(401).content("Cookie not found").end();
        }
    }
}
