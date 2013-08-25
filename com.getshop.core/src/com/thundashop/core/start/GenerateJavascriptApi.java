/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.start;

import com.thundashop.core.common.GetShopApi;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
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

    private List<Class> interfaces = new ArrayList();

    public void start() throws ClassNotFoundException, IOException, URISyntaxException {
        loadInterfaces();
        generateJavascript();
    }

    private void loadInterfaces() throws ClassNotFoundException {
        File file = new File("../com.getshop.core/build/classes/");
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

    private String getHeader() throws URISyntaxException {
        String total = "";
        
        try (InputStream fis = this.getClass().getResourceAsStream("GetShopJavascriptApiHeader")) {
            if (fis == null) {
                System.out.println("Was not able to find header file, is the package not compile? please clean/build and try again");
                System.exit(-1);
            }
            int content;
            while ((content = fis.read()) != -1) {
                // convert to char and display it
                total += (char) content;
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return total;
    }

    private void generateJavascript() throws IOException, URISyntaxException {
        String javascriptFile = "";
        javascriptFile += getHeader();
        
        String createManagers = "GetShopApiWebSocket.prototype.createManagers = function() {\n";
        for (Class clazz : interfaces) {
            
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
                path = path.replace(".", "/") + ".java";
                Map<String, Object> parsed = GeneratePhpApiNew.parseMethod(path, method, "JAVA");
                Object args = parsed.get("splittedArgs");
                ArrayList<String> arguments = (ArrayList) args;
                String argstring = "";
                for (String arg : arguments) {
                    argstring += arg + ",";
                }
                if (argstring.length() > 0) {
                    argstring = argstring.substring(0, argstring.length() - 1);
                }
                javascriptFile += "    " + method.getName() + " : function(" + argstring + ") {\n";
                javascriptFile += "        data = {\n";
                javascriptFile += "            args : {\n";
                for (String arg : arguments) {
                    javascriptFile += "                " + arg + " : " + arg + ",\n";
                }
                javascriptFile += "            " + "},\n";
                javascriptFile += "            method: '" + method.getName() + "',\n";
                javascriptFile += "            interfaceName: '" + clazz.getCanonicalName().replace("com.thundashop.", "") + "',\n";
                javascriptFile += "        };\n";
                javascriptFile += "        return this.communication.send(data);\n";
                javascriptFile += "    },\n";
                javascriptFile += "\n";
            }
            javascriptFile += "}\n";
        }
        
        createManagers += "}";

        javascriptFile += "\n";
        javascriptFile += createManagers;
        
        System.out.println(javascriptFile);
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, URISyntaxException {
        GenerateJavascriptApi generate = new GenerateJavascriptApi();
        generate.start();
    }
}
