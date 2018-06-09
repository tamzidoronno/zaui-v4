/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.start;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.pmsbookingprocess.IPmsBookingProcess;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class GenerateJavascriptApi {
    public static String pathToBuildClasses = "build/classes/main/";
    public static String pathToJavaSource = "src/main/java/";
    public static String storeFileIn = "build/getshopapi.js";
    public boolean createOnlyForbookingProcess = false;
    private List<Class> interfaces = new ArrayList();

    public void start() throws ClassNotFoundException, IOException, URISyntaxException {
        loadInterfaces();
        generateJavascript();
    }

    private void loadInterfaces() throws ClassNotFoundException {
        File file = new File(pathToBuildClasses);
        List<Class> classes = GeneratePhpApi.findClasses(file, "");
        classes = GeneratePhpApiNew.sortClasses(classes);
  
        for (Class clazz : classes) {
            GetShopApi annotation = (GetShopApi) clazz.getAnnotation(GetShopApi.class);
            if (annotation != null) {
                interfaces.add(clazz);
            }
        }
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

    private String getHeader() throws URISyntaxException, IOException {
        String total = "";
        
        List<String> lines = Files.readAllLines(Paths.get(GenerateJavascriptApi.pathToJavaSource+"com/thundashop/core/start/GetShopJavascriptApiHeader2"));
//        List<String> lines = Files.readAllLines(Paths.get(GenerateJavascriptApi.pathToJavaSource+"com/thundashop/core/start/GetShopJavascriptApiHeader"));

        for (String line : lines) {
            total += line + "\n";
        }

        return total;
    }

    private void generateJavascript() throws IOException, URISyntaxException {
        String javascriptFile = "";
        javascriptFile += getHeader() + "\n\n";
        
        String createManagers = "GetShopApiWebSocket.prototype.createManagers = function() {\n";
        for (Class clazz : interfaces) {
            if(!clazz.getName().contains("IPmsBookingProcess") && createOnlyForbookingProcess) {
                continue;
            }
            String fileName = clazz.getSimpleName().substring(1);
            
            createManagers += "    this." + fileName + " = new GetShopApiWebSocket."+fileName+"(this);\n";
            
            javascriptFile += "GetShopApiWebSocket." + fileName + " = function(communication) {\n";
            javascriptFile += "    this.communication = communication;\n";
            javascriptFile += "}\n";
            javascriptFile += "\n";

            Method[] methods = clazz.getMethods();
            methods = GeneratePhpApiNew.sortMethods(methods);
            javascriptFile += "GetShopApiWebSocket." + fileName + ".prototype = {\n";
            for (Method method : methods) {

                String path = clazz.getCanonicalName();
                path =  pathToJavaSource+path.replace(".", "/") + ".java";
                GetShopLogHandler.logPrintStatic(method.getName(), null);
                if(method.getName().equals("getAllBookings")) {
                    GetShopLogHandler.logPrintStatic("found", null);
                }
                
                Map<String, Object> parsed = GeneratePhpApiNew.parseMethod(path, method, "JAVA", false);
                Object args = parsed.get("splittedArgs");
                ArrayList<String> arguments = (ArrayList) args;
                boolean multiLevel = clazz.getAnnotation(GetShopMultiLayerSession.class) != null;
                String argstring = "";
                if(multiLevel) {
                    argstring = "multilevelname, ";
                }
                for (String arg : arguments) {
                    argstring += arg + ",";
                }
                if (argstring.length() > 0) {
                    argstring = argstring.substring(0, argstring.length() - 1);
                }
                
                argstring = argstring.equals("") ? "gs_silent" : argstring + ", gs_silent";
                argstring = argstring.replace(",,", ",");
                javascriptFile += "    '" + method.getName() + "' : function(" + argstring + ") {\n";
                javascriptFile += "        var data = {\n";
                javascriptFile += "            args : {\n";
                for (String arg : arguments) {
                    javascriptFile += "                " + arg + " : JSON.stringify(" + arg + "),\n";
                }
                javascriptFile += "            " + "},\n";
                javascriptFile += "            method: '" + method.getName() + "',\n";
                if(multiLevel) {
                    javascriptFile += "            multiLevelName: multilevelname,\n";
                }
                javascriptFile += "            interfaceName: '" + clazz.getCanonicalName().replace("com.thundashop.", "") + "',\n";
                javascriptFile += "        };\n";
                javascriptFile += "        return this.communication.send(data, gs_silent);\n";
                javascriptFile += "    },\n";
                javascriptFile += "\n";
            }
            javascriptFile += "}\n";
        }
        
        createManagers += "}";

        javascriptFile += "\n";
        javascriptFile += createManagers;
        
        if(!createOnlyForbookingProcess) {
            javascriptFile += createErrorArray();
        }

        
        Files.write(Paths.get(storeFileIn), javascriptFile.getBytes());
        GetShopLogHandler.logPrintStatic("file stored in : " + storeFileIn, null);
    }
    
    private String createErrorArray() throws IOException {
        File fileToUse = new File("/source/getshop/3.0.0/com.getshop.client/language/en/errors.csv");
        String pathToUse = "/source/getshop/3.0.0/com.getshop.client/language/en/errors.csv";
        
        if (!fileToUse.exists()) {
            pathToUse = "/var/lib/jenkins/jobs/GetShop/workspace/com.getshop.client/language/en/errors.csv";
        }
        List<String> lines = Files.readAllLines(Paths.get(pathToUse));
        String ret = "errorTextMatrix = {\n";
        for (String line : lines) {
            if (!line.contains("\",\"")) {
                continue;
            }
            ret += line.replaceAll("\",\"", "\":\"")+",\n";
        }
        ret += "}";
        
        
        return "\n"+ret+"\n";
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, URISyntaxException {
        GenerateJavascriptApi generate = new GenerateJavascriptApi();
        generate.start();
    }
    
    public static void generateJavascriptForBookingProcess() throws Exception {
        GenerateJavascriptApi generate = new GenerateJavascriptApi();
        generate.createOnlyForbookingProcess = true;
        generate.start();
    }
}
