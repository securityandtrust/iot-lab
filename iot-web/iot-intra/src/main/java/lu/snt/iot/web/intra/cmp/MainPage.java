package lu.snt.iot.web.intra.cmp;

import lu.snt.iot.web.intra.core.IotIntraMain;
import org.kevoree.annotation.*;
import org.kevoree.api.KevScriptService;
import org.kevoree.api.ModelService;
import org.kevoree.log.Log;

/**
 * Created by gregory.nain on 06/12/2013.
 */

@ComponentType
@Library(name = "IoT :: Web")
public class MainPage {


    private IotIntraMain app;

    @Param(defaultValue = "WARN")
    private String logLevel;

    @Param(defaultValue = "8200")
    private int port;

    @Param(defaultValue = "http://0.0.0.0:")
    private String address;

    @KevoreeInject
    private KevScriptService kevScriptService;

    @KevoreeInject
    private ModelService modelService;

    @Start
    public void startComponent() {
        setLogLevel();
        app = new IotIntraMain();
        app.setPort(port);
        app.setAddress(address + (address.endsWith(":")?"":":"));
        app.start(this);
    }

    @Stop
    public void stopComponent() {
        app.stop();
        app = null;
    }

    @Update
    public void updateComponent() {
        setLogLevel();
    }

    private void setLogLevel() {
        switch(logLevel) {
            case "WARN" : Log.WARN();break;
            case "DEBUG" : Log.DEBUG();break;
            case "TRACE" : Log.TRACE();break;
            case "ERROR" : Log.ERROR();break;
            case "INFO" : Log.INFO();break;
            case "NONE" : Log.NONE();break;
            default:Log.WARN();
        }
    }

    public KevScriptService getKevScriptService() {
        return kevScriptService;
    }

    public ModelService getModelService() {
        return modelService;
    }
}


