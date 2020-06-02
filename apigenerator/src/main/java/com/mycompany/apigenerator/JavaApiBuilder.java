/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.apigenerator;

import com.mycompany.apigenerator.GenerateApi.ApiMethod;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author boggi
 */
public class JavaApiBuilder {

    private final GenerateApi generator;
    private String apiPath = "src/main/java/com/getshop/javaapi";
    private final List<Class> allManagers;
    private final String systemName;
    private final String apiClassSystemName;
    
    public JavaApiBuilder(GenerateApi generator, List<Class> allManagers, String systemName, String apiClassSystemName) {
        this.generator = generator;
        this.allManagers = allManagers;
        this.systemName = systemName;
        this.apiPath += "/"+systemName;
        this.apiClassSystemName = apiClassSystemName;
        
//        File file = new File(apiPath+"/"+systemName);
//        GetShopLogHandler.logPrintStatic("Writing java api classes to: " + file.getAbsolutePath(), null);

    }

    void generate() throws IOException {
        BuildJavaApi();
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
            if(method.equals("gs_multiLevelName")) {
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
            
            content += "          JsonElement object = gson.fromJson(result, JsonElement.class);\n";
            content += "          return object;\r\n";
        }
        return content;
    }

    private String BuildJavaApi() throws IOException {
        
        List<String> apiclasses = new ArrayList();
        if(apiPath == null) {
            GetShopLogHandler.logPrintStatic("Not building java api, the path to source is not specified", null);
            return "";
        }
        GetShopLogHandler.logPrintStatic("Building java api to: " + apiPath + "/", null);

        for (Class entry : allManagers) {
            String filename = getFileName(entry);
            if (filename.contains("<error>") || !filename.startsWith("I")) {
                continue;
            }
            
            String content = "";
            
            content = getContent(entry, apiclasses);
            
            String fname = "API" + entry.getSimpleName().substring(1) + ".java";
            File file = new File(apiPath + "/");
            file.mkdirs();
            generator.writeFile(content, apiPath + "/" + fname);
        }

        
        String content = "";
        content = "package com.getshop.javaapi."+systemName+";\n\n";
        content += "import com.thundashop.core.common.Communicator;\n";
        content += "public class "+apiClassSystemName+" {\r\n";
        content += "\r\n";
        content += "      public Communicator transport;\r\n";
        content += "      public "+apiClassSystemName+"(Communicator transporter) throws Exception {\r\n";
        content += "           this.transport = transporter;\r\n";
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

        generator.writeFile(content, apiPath + "/"+apiClassSystemName+".java");

        return content;
    }

    private String getContent(Class entry, List<String> apiclasses) {
        String content = "";
        List<Class> addedClasses = new ArrayList();
        Annotation[] annotations = entry.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof GetShopApi) {
                content += "public class API" + entry.getSimpleName().substring(1) + " {\r\n\r\n";
                content += "      public Communicator transport;\r\n\r\n";
                content += "      public API" + entry.getSimpleName().substring(1) + "(Communicator transport){\r\n";
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
                        method.arguments.put("gs_multiLevelName", "String");
                        method.arguments.putAll(res);
                    }
                    for(String arg : method.arguments.keySet()) {
                        if (arg.equals("gs_multiLevelName")) {
                            args += "String " + arg + ", ";
                        } else {
                            args += "Object " + arg + ", ";
                        }
                    }
                    if(args.length() > 0) {
                        args = args.substring(0, args.length()-2);
                    }
                    String returnvalue = method.method.getReturnType().getCanonicalName().toString();
                    
                    if (!returnvalue.equals("void")) {
                        content += "     public JsonElement " + method.method.getName() +  "(" + args + ")  throws Exception  {\r\n";
                    } else {
                        content += "     public void " + method.method.getName() +  "(" + args + ")  throws Exception  {\r\n";
                    }
                    
                    String ifacename = entry.getCanonicalName();
                    ifacename = ifacename.replace("com.thundashop.", "");
                    content += buildJavaApiContent(method.arguments, method.method.getName(), ifacename, returnvalue, method.generics);
                    content += "     }\r\n\r\n";
                }
                content += "}\r\n";
            }
        }
        
        String resultHeader = "package com.getshop.javaapi."+systemName+";\n\n";
        resultHeader += "import com.google.gson.GsonBuilder;\n";
        resultHeader += "import com.google.gson.Gson;\n";
        resultHeader += "import java.util.LinkedHashMap;\n";
        resultHeader += "import com.google.gson.JsonElement;\n";

        resultHeader += "import com.thundashop.core.common.JsonObject2;\n";
        resultHeader += "import com.thundashop.core.common.Communicator;\n";

        String returnContent = resultHeader+content;
        String filePathString = "/tmp/cached_java_"+entry.getCanonicalName()+".cache";
        
        Path path = Paths.get(filePathString);
 
        //Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(path))
        {
            writer.write(returnContent);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
            
        return returnContent;
    }

    private String getCachedContent(Class manager, List<String> apiclasses) {
//        if (!GetShopLogHandler.isDeveloper) {
//            return getContent(manager, apiclasses);
//        }
        
        String filePathString = "/tmp/cached_java_"+manager.getCanonicalName()+".cache";
        File f = new File(filePathString);
        if (!f.exists()) {
            return getContent(manager, apiclasses);
        }
        
        try {
            String contentLoaded = new String(Files.readAllBytes(Paths.get(filePathString)));
            return contentLoaded;
        } catch (IOException ex) {
            return getContent(manager, apiclasses);
        }
        
    }
}
