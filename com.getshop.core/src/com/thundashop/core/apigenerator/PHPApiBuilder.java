package com.thundashop.core.apigenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class PHPApiBuilder {

    private final GenerateApi generator;
    private String eventsPath = "../com.getshop.client/events/";
    private final List<Class> allManagers;
    private final List<Class> dataObjects;

    public PHPApiBuilder(GenerateApi generator, List<Class> allManagers, List<Class> dataObjects) {
        this.generator = generator;
        this.allManagers = allManagers;
        this.dataObjects = dataObjects;

    }

    private String getFileName(Class entry) {
        return entry.getName().split("\\.")[entry.getName().split("\\.").length - 1];
    }
    
    private String createPhpClassName(Class entry, String filename) {
        String[] paths = entry.getName().split("\\.");
        String classname = "WHAT_IS_THIS";

        if (paths.length == 5) {
            classname = paths[2] + "_" + paths[3] + "_" + filename;
        } else if (paths.length == 6) {
            classname = paths[2] + "_" + paths[3] + "_" + paths[4] + "_" + filename;
        } else {
            System.out.println(entry.getName());
        }
        return classname;
    }


    private String createPhpFileContent(Class entry) {
        String filePath = "";
        String classname = entry.getCanonicalName().replace("com.thundashop.","").replace(".", "_");
        Class superClassName = entry.getSuperclass();
        String extendsstring = "";

        if (superClassName != null) {
            String parentClassName = createPhpClassName(superClassName, getFileName(superClassName));
            if (!parentClassName.equals("") && superClassName.getName().contains("thundashop")) {
                extendsstring = " extends " + parentClassName + " ";
            }
        }

        Field[] fields = entry.getDeclaredFields();

        String phpResult = "<?php\r\n";
        phpResult += "class " + classname + extendsstring + " {\r\n";

        for (int j = 0; j < fields.length; j++) {
            String type = fields[j].getType().toString();
            String toType = type.getClass().getSimpleName();
            String varName = fields[j].getName();
            if (type.contains("thundashop")) {
                toType = createPhpClassName(fields[j].getType(), getFileName(fields[j].getType()));
            }
            
            if(varName.contains("$")) {
                varName = varName.replace("$", "");
            }
            phpResult += "\t/** @var " + toType + " */\n";
            phpResult += "\tpublic $" + varName + ";\n";
            phpResult += "\r\n";
        }

        phpResult += getAddons(filePath);
        phpResult += "}\r\n";

        phpResult += "?>";
        return phpResult;
    }

    String getAddons(String filePath) {
        String newPath = filePath.replace("events", "events_addon");
        File phpfile = new File(filePath);
        String addons = "";
        if (phpfile.exists()) {
            try {
                String strLine = "";
                BufferedReader reader = new BufferedReader(new FileReader(phpfile));
                while ((strLine = reader.readLine()) != null) {
                    addons += "\t" + strLine + "\n";
                }
            } catch (Exception ex) {
            }
        }
        return addons;
    }

    public void generate() throws IOException {
        String result = generatePHPApi();
        this.generator.writeFile(result, eventsPath + "API2.php");
        
        System.out.println("PATH: " + eventsPath);
        
        for (Class dataobject : dataObjects) {
            String dataPath = eventsPath + dataobject.getCanonicalName().replace("com.thundashop.", "").replace(".", "/");
            dataPath = dataPath+".php";
            if(dataPath.contains("/api/")) {
                continue;
            }
            String content = createPhpFileContent(dataobject);
            generator.writeFile(content, dataPath);
        }

        
    }

    public String generatePHPApi() {
        String result = "<?php\n\n/**\n"
                + " * This library is built by GetShop and are used to communicate with the GetShop backend. \n"
                + " * License: GPLv2\n"
                + " * License URI: http://www.gnu.org/licenses/gpl-2.0.html\n"
                + "*/\n\n";

        for (Class manager : allManagers) {
            LinkedList<GenerateApi.ApiMethod> methods = generator.getMethods(manager);
            result += generatePHPApiClass(manager, methods);
        }

        result += "class GetShopApi {\n"
                + "\n"
                + "      var $transport;\n"
                + "      function GetShopApi($port, $host=\"localhost\", $sessionId) {\n"
                + "           $this->transport = new CommunicationHelper();\n"
                + "           $this->transport->port = $port;\n"
                + "           $this->transport->sessionId = $sessionId;\n"
                + "           $this->transport->host = $host;\n"
                + "           $this->transport->connect();\n"
                + "      }\n";

        for (Class manager : allManagers) {
            result += "      /**\n"
                    + "      * @return " + manager.getSimpleName().substring(1) + "\n"
                    + "      */\n"
                    + "      public function get" + manager.getSimpleName().substring(1) + "() {\n"
                    + "           return new API" + manager.getSimpleName().substring(1) + "($this->transport);\n"
                    + "      }\n";
        }
        result += "}";


        return result + "\n?>";
    }

    public String generatePHPApiClass(Class manager, List<GenerateApi.ApiMethod> methods) {
        String phpClass = "class API" + manager.getSimpleName().substring(1) + " {\n";

        phpClass += "\n\tvar $transport;\n"
                + "\t\n"
                + "\tfunction API" + manager.getSimpleName().substring(1) + "($transport) {\n"
                + "\t\t$this->transport = $transport;\n"
                + "\t}\n\n";

        for (GenerateApi.ApiMethod method : methods) {
            String args = "";
            for (String value : method.arguments.keySet()) {
                if (method.arguments.get(value).contains("thundashop")) {
                    value = method.arguments.get(value).replace("com.thundashop.", "").replace(".", "_");
                }
                args += "$" + value + ", ";
            }
            if (!args.isEmpty()) {
                args = args.substring(0, args.length() - 2);
            }

            String returnvalue = method.method.getReturnType().getName().replace(".", "_");
            if (returnvalue.contains("com_thundashop")) {
                returnvalue = returnvalue.replace("com_thundashop_", "");
            } else {
                returnvalue = method.method.getReturnType().getSimpleName();
            }

            for (String comment : method.commentLines) {
                if (comment.trim().isEmpty()) {
                    continue;
                }
                if (comment.indexOf("@return") > 0) {
                    phpClass += "\t* @return " + returnvalue + "\n";
                } else {
                    phpClass += "\t" + comment + "\n";
                }
            }

            phpClass += "\n\tpublic function " + method.methodName + "(" + args + ") {\n";
            phpClass += "\t     $data = array();\n";
            phpClass += "\t     $data['args'] = array();\n";
            for (String arg : method.arguments.keySet()) {
                if (method.arguments.get(arg).contains("thundashop")) {
                    arg = method.arguments.get(arg).replace("com.thundashop.", "").replace(".", "_");
                }
                phpClass += "\t     $data['args'][\"" + arg + "\"] = json_encode($this->transport->object_unset_nulls($" + arg + "));\n";
            }
            phpClass += "\t     $data[\"method\"] = \"" + method.method.getName() + "\";\n";
            phpClass += "\t     $data[\"interfaceName\"] = \"" + method.manager.getCanonicalName().replace("com.thundashop.", "") + "\";\n";
            if (returnvalue.startsWith("core_") || returnvalue.startsWith("app_")) {
                phpClass += "\t     return $this->transport->cast(new " + returnvalue + "(), $this->transport->sendMessage($data));\n";
            } else {
                phpClass += "\t     return $this->transport->sendMessage($data);\n";
            }


            phpClass += "\t}\n\n";
        }

        phpClass += "}\n";

        return phpClass;

    }
}
