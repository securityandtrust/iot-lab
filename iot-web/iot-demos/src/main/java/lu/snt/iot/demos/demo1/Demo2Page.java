package lu.snt.iot.demos.demo1;

import org.kevoree.annotation.*;
import org.kevoree.api.Context;
import org.kevoree.library.web.nano.NanoHTTPD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by gregory.nain on 06/12/2013.
 */
@ComponentType
@Library(name = "IoTLab :: Demos")
public class Demo2Page extends NanoHTTPD {

    @Param(defaultValue = "6002")
    Integer http_port;

    @KevoreeInject
    Context context;

    String pageCache = "";
    //String[] colors = {"#e67e22", "#8e44ad", "#1abc9c", "#3498db", "#2c3e50"};

    @Start
    public void startBlog() throws IOException {
        this.myPort = http_port;
        pageCache = read(this.getClass().getClassLoader().getResourceAsStream("demo2.html"));
        /*
        int randomColorIndice = new Random().nextInt(colors.length);
        pageCache = pageCache.replace("${background.color}", colors[randomColorIndice]);
        pageCache = pageCache.replace("${name}", context.getInstanceName());
        pageCache = pageCache.replace("${node.name}", context.getNodeName());
        */
        setTempFileManagerFactory(new TempFileManagerFactory() {
            @Override
            public TempFileManager create() {
                return new DefaultTempFileManager();
            }
        });
        setAsyncRunner(new DefaultAsyncRunner());
        start();
    }

    @Stop
    public void stopBlog() {
        stop();
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) {
        return new Response(pageCache);
    }


    public static String read(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }
            br.close();
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
