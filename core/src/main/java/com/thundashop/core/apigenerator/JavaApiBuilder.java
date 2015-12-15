/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.apigenerator;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.apigenerator.GenerateApi.ApiMethod;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author boggi
 */
public class JavaApiBuilder {

    private final GenerateApi generator;
    private String apiPath = "../javaapi/src/main/java/com/getshop/javaapi";
    private final List<Class> allManagers;
    private final List<Class> dataObjects;

    public JavaApiBuilder(GenerateApi generator, List<Class> allManagers, List<Class> dataObjects, String pathToSource) {
        this.generator = generator;
        this.allManagers = allManagers;
        this.dataObjects = dataObjects;
        if(pathToSource != null && !pathToSource.isEmpty()) {
            apiPath = pathToSource + "/" + apiPath;
        }
        
        File file = new File(apiPath);
        System.out.println("Writing java api classes to: " + file.getAbsolutePath());

    }

    void generate() throws IOException {
        BuildJavaApi(allManagers);
    }

    private String getFileName(Class entry) {
        return entry.getName().split("\\.")[entry.getName().split("\\.").length - 1];
    }

    private String buildJavaApiContent(HashMap<String, String> args, String methodName, String interfaceName, String returnClass, String generics) {
        String content = "";
        returnClass = returnClass.replace("[]", "");

        content += "          JsonObject2 gs_json_object_data = new JsonObject2();\r\n";
        content += "          gs_json_object_data.args = new LinkedHashMap();\r\n";
        for (String method : args.keySet()) {
            if(method.equals("multiLevelName")) {
                content += "          gs_json_object_data.multiLevelName = " + method + ";\n";
            } else {
                content += "          gs_json_object_data.args.put(\"" + method + "\",new Gson().toJson(" + method + "));\n";
            }
        }

        content += "          gs_json_object_data.method = \"" + methodName + "\";\r\n";
        content += "          gs_json_object_data.interfaceName = \"" + interfaceName + "\";\r\n";
        content += "          String result = transport.send(gs_json_object_data);\n";
        if (!returnClass.equals("void")) {
            content += "          Gson gson = new GsonBuilder().serializeNulls().create();\n";

            if (returnClass.equals("int")) {
                returnClass = "Integer";
            }
            if (returnClass.equals("boolean")) {
                returnClass = "Boolean";
            }

            String typetoke = returnClass;
            if(generics != null && !generics.isEmpty()) {
                typetoke += generics;
            }
            
            content += "          Type typeJson_3323322222_autogenerated = new TypeToken<" + typetoke + ">() {}.getType();\n";
            content += "          " + returnClass + " object = gson.fromJson(result, typeJson_3323322222_autogenerated);\n";
            content += "          return object;\r\n";
        }
        return content;
    }

    private String BuildJavaApi(List<Class> list) throws IOException {
        String content = "";
        List<String> apiclasses = new ArrayList();
        if(apiPath == null) {
            System.out.println("Not building java api, the path to source is not specified");
            return "";
        }
        System.out.println("Building java api to: " + apiPath + "/");

        for (Class entry : list) {
            String filename = getFileName(entry);
            if (filename.contains("<error>") || !filename.startsWith("I")) {
                continue;
            }
            content = "";
            List<Class> addedClasses = new ArrayList();
            Annotation[] annotations = entry.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof GetShopApi) {
                    content += "public class API" + entry.getSimpleName().substring(1) + " {\r\n\r\n";
                    content += "      public Transporter transport;\r\n\r\n";
                    content += "      public API" + entry.getSimpleName().substring(1) + "(Transporter transport){\r\n";
                    content += "           this.transport = transport;\r\n";
                    content += "      }\r\n\r\n";
                    apiclasses.add(entry.getSimpleName().substring(1));
                    
                    LinkedList<ApiMethod> methods = generator.getMethods(entry);
                    for (ApiMethod method : methods) {
                        String path = entry.getCanonicalName();
                        path = path.replace(".", "/") + ".java";

                        for(String cmnt : method.commentLines) {
                            content += "     " + cmnt + "\n";
                        }
                        addedClasses.add(method.method.getReturnType());
                        String args = "";
                       
                        boolean multiLevel = entry.getAnnotation(GetShopMultiLayerSession.class) != null;
                        if(multiLevel) {
                            LinkedHashMap res = method.arguments;
                            method.arguments = new LinkedHashMap();
                            method.arguments.put("multiLevelName", "String");
                            method.arguments.putAll(res);
                        }
                        for(String arg : method.arguments.keySet()) {
                            args += method.arguments.get(arg) + " " + arg + ", ";
                        }
                        if(args.length() > 0) {
                            args = args.substring(0, args.length()-2);
                        }
                        String returnvalue = method.method.getReturnType().getCanonicalName().toString();
                        
                        content += "     public " + returnvalue + " " + method.method.getName() +  "(" + args + ")  throws Exception  {\r\n";
                        String ifacename = entry.getCanonicalName();
                        ifacename = ifacename.replace("com.thundashop.", "");
                        content += buildJavaApiContent(method.arguments, method.method.getName(), ifacename, returnvalue, method.generics);
                        content += "     }\r\n\r\n";
                    }
                    content += "}\r\n";
                }
            }
            String resultHeader = "package com.getshop.javaapi;\n\n";
            resultHeader += "import com.google.gson.GsonBuilder;\n";
            resultHeader += "import com.google.gson.Gson;\n";
            resultHeader += "import com.google.gson.reflect.TypeToken;\n";
            resultHeader += "import java.lang.reflect.Type;\n";
            resultHeader += "import java.util.HashMap;\n";
            resultHeader += "import java.util.LinkedHashMap;\n";

            resultHeader += "import com.thundashop.core.common.JsonObject2;\n";
            resultHeader += "import com.getshop.common.Transporter;\n";

            String fname = "API" + entry.getSimpleName().substring(1) + ".java";
            
            File file = new File(apiPath + "/");
            file.mkdirs();
            generator.writeFile(resultHeader+content, apiPath + "/" + fname);
        }

        
        content = "";
        content = "package com.getshop.javaapi;\n\n";
        content += "import com.getshop.common.Transporter;\n";
        content += "public class GetShopApi {\r\n";
        content += "\r\n";
        content += "      public Transporter transport;\r\n";
        content += "      public GetShopApi(int port, String host, String sessionId, String webaddress) throws Exception {\r\n";
        content += "           this.transport = new Transporter();\r\n";
        content += "           this.transport.port = port;\r\n";
        content += "           this.transport.host = host;\r\n";
        content += "           this.transport.sessionId = sessionId;\r\n";
        content += "           this.transport.webaddress = webaddress;\r\n";
        content += "           this.transport.api = this;\r\n";
        content += "           this.transport.connect();\r\n";
        content += "           getStoreManager().initializeStore(webaddress, sessionId);\r\n";
        content += "      }\r\n";
        for (String data : apiclasses) {
            content += "     /**\r\n";
            content += "      * @return API" + data + "\r\n";
            content += "      */\r\n";
            content += "      public API" + data + " get" + data + "() {\r\n";
            content += "           return new API" + data + "(transport);\r\n";
            content += "      }\r\n";
        }
        content += "}\r\n";

        generator.writeFile(content, apiPath + "/GetShopApi.java");

        return content;
    }
}
