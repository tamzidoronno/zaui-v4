/**
 * This class is a part of the thundashop project.
 *
 * All rights reserved
 *
 *
 */
package com.thundashop.core.start;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import java.io.*;
    import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType; 
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 *
 * @author ktonder
 */
public class GeneratePhpApiNew {

    private static List<Class> dataObjects = null;
    private static Iterable<Class> list;
    private static HashMap<String, String> allImportClasses = new HashMap();

    /*
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static List<Class> getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String fileName = resource.getFile();
            String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
            dirs.add(new File(fileNameDecoded));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     *
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base
     * directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClasses(file, packageName + "." + fileName));
            } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                Class _class;
                try {
                    String classname = packageName + '.' + fileName.substring(0, fileName.length() - 6);
                    classname = classname.replace('/', '.').substring(1);
                    _class = Class.forName(classname);
                } catch (ExceptionInInitializerError e) {
                    // happen, for example, in classes, which depend on 
                    // Spring to inject some beans, and which fail, 
                    // if dependency is not fulfilled
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6),
                            false, Thread.currentThread().getContextClassLoader());
                }
                classes.add(_class);
            }
        }
        return classes;
    }

    static String getPath(Class entry) {
        String[] paths = entry.getName().split("\\.");
        String thePath = "../com.getshop.client/events/";
        int i = 0;

        for (String path : paths) {
            i++;
            if (i > 2 && i < paths.length) {
                thePath += "/" + path;
            }
        }
        return thePath;
    }

    static void createFolders(Class entry) {
        String classPath = getPath(entry);
        File pathcreator = new File(classPath);
        pathcreator.mkdirs();
    }

    static String createPhpClassName(Class entry, String filename) {
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

    static String getAddons(Class entry, String classname, String filename, String filePath) {
        String newPath = filePath.replace("events", "events_addon");
        File phpfile = new File(newPath + "/" + filename + ".php");
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

    static String createPhpFileContent(Class entry, String classname, String filename, String filePath) {
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
            if (type.contains("getshop")) {
                toType = createPhpClassName(fields[j].getType(), getFileName(fields[j].getType()));
            }
            phpResult += "\t/** @var " + toType + " */\n";
            phpResult += "\tpublic $" + varName + ";\n";
            phpResult += "\r\n";
        }

        phpResult += getAddons(entry, classname, filename, filePath);
        phpResult += "}\r\n";

        phpResult += "?>";
        return phpResult;
    }

    public static void Generate(List<Class> list) throws IOException {
        for (Class entry : list) {
            String filename = getFileName(entry);
            if (filename.contains("<error>")) {
                continue;
            }

            createFolders(entry);
            String classname = createPhpClassName(entry, filename);
            System.out.println(classname);
        }
    }

    /**
     * Fetch All files and converts the data objects into php objects.
     *
     * @param args
     * @throws InterruptedException
     * @throws Exception
     */
    public static void main(String[] args) throws InterruptedException, Exception {
        GeneratePhpApi.main(args);
        File file = new File("../com.getshop.core/build/classes/");
        List<Class> classes = GeneratePhpApi.findClasses(file, "");
        classes = sortClasses(classes);
        String result = BuildApi(classes);
        writePHPApi(result);

        String apiFile = BuildJavaApi(classes);
        System.out.println(apiFile);
        writeApiFile(apiFile);

        File messages = new File("../com.getshop.messages/build/classes/");
        dataObjects = GeneratePhpApi.findClasses(messages, "");

        buildDocumentation(GeneratePhpApi.findClasses(file, ""));
    }

    private static String getFileName(Class entry) {
        return entry.getName().split("\\.")[entry.getName().split("\\.").length - 1];
    }

    private static String buildApiContent(List<String> obj, String methodName, String interfaceName, String returnClass) {
        String content = "";
        returnClass = returnClass.replace("[]", "");

        content += "          $data = array();\r\n";
        content += "          $data['args'] = array();\r\n";
        for (String method : obj) {
            content += "          $data['args'][\"" + method + "\"] = " + "json_encode($this->transport->object_unset_nulls($" + method + "));\r\n";
        }

        content += "          $data[\"method\"] = \"" + methodName + "\";\r\n";
        content += "          $data[\"interfaceName\"] = \"" + interfaceName + "\";\r\n";
        if (returnClass.contains("data")) {
            content += "          return $this->transport->cast(API::" + returnClass + "(), $this->transport->sendMessage($data));\r\n";
        } else {
            content += "          return $this->transport->sendMessage($data);\r\n";
        }
        return content;
    }

    private static String buildJavaApiContent(List<String> obj, String methodName, String interfaceName, String returnClass) {
        String content = "";
        returnClass = returnClass.replace("[]", "");

        content += "          JsonObject2 data = new JsonObject2();\r\n";
        content += "          data.args = new LinkedHashMap();\r\n";
        for (String method : obj) {
            content += "          data.args.put(\"" + method + "\",new Gson().toJson(" + method + "));\n";
        }

        content += "          data.method = \"" + methodName + "\";\r\n";
        content += "          data.interfaceName = \"" + interfaceName + "\";\r\n";
        content += "          String result = transport.send(data);\n";
        if (!returnClass.equals("void")) {
            content += "          Gson gson = new GsonBuilder().serializeNulls().create();\n";

            if (returnClass.equals("int")) {
                returnClass = "Integer";
            }
            if (returnClass.equals("boolean")) {
                returnClass = "Boolean";
            }

            content += "          Type typeJson_3323322222_autogenerated = new TypeToken<" + returnClass + ">() {}.getType();\n";
            content += "          " + returnClass + " object = gson.fromJson(result, typeJson_3323322222_autogenerated);\n";
            content += "          return object;\r\n";
        }
        return content;
    }

    private static String createClassDescription(String path) {
        String content = readFile(path, true);
        int start = content.indexOf("/*");
        int stop = content.indexOf("*/");

        content = content.substring(start, stop);
        content = content.replace("* ", "");
        content = content.replace("/**", "");

        return content;
    }

    private static void buildDocumentation(List<Class> list) throws IOException, ClassNotFoundException {
        String html = createHTMLHeader(list);
        String[] storedClassesStrings = createSortedArray(list);
        for (String name : storedClassesStrings) {
            if (name.equals("IGetShop")) {
                continue;
            }
            Class entry = findEntryByName(name, list);
            String path = entry.getCanonicalName();
            path = path.replace(".", "/") + ".java";
            String filename = getFileName(entry);
            if (filename.contains("<error>")) {
                continue;
            }
            String theClass = entry.getSimpleName().substring(1);
            html += "<div class='manager " + theClass + "'>\r\n";
            html += "<div class='manager_header_text' target='" + theClass + "'>" + theClass + "</div>";
            html += "<div class='class_description'>" + createClassDescription(path);

            html += "<br><br><b>Table of methods:</b><br>";
            Method[] sortedMethods = sortMethods(entry.getMethods());
            html += "<table width='100%' bgcolor='#BBBBBB' cellspacing='1' cellpadding='2'>";
            String converted = "";
            for (Method method : sortedMethods) {
                Map<String, Object> parsedMethod = parseMethod(path, method, "DOC");
                parsedMethod.put("manager", entry);
                String[] lines = (String[]) parsedMethod.get("commentLines");
                html += buildTableToc(method, lines);
                converted += convertMethodToHTML(parsedMethod);
            }
            html += "</table>";

            html += "<br><b>Dataobjects:</b><br>";
            html += createDataObjectTable(name);
            html += "</div>";
            html += converted;
            html += genrateDataObjects(name);
            html += "</div>\r\n";
        }



        writeDocumentation(html);

    }

    private static String genrateDataObjects(String name) {
        name = name.substring(1);
        System.out.println(name);
        String html = "<div class='dataObject'>";
        for (Class theClass : dataObjects) {
            if (theClass.getName().toLowerCase().contains(name.toLowerCase())) {
                html += "<div class=\"method_name_header\">Data object: " + theClass.getName() + "</span></div>";
                html += "<div class='method_container'>";
                Field[] fields = theClass.getDeclaredFields();
                html += "<table>";
                for (Field field : fields) {
                    html += "<tr>";
                    html += "<td width='100'>" + field.getType().getSimpleName() + "</td>";
                    html += "<td>" + field.getName() + "</td>";
                    html += "</tr>";
                }
                html += "</table>";
                html += "</div>";
            }
        }
        html += "</div>";

        return html;
    }

    private static String buildTableToc(Method method, String[] commentLines) {
        String html = "<tr bgcolor='#FFFFFF'>";
        html += "<td width='10'  valign='top'>" + method.getName() + "</td>";
        html += "<td valign='top'>";

        for (String line : commentLines) {
            if (!line.contains("@")) {
                html += line.replace("* ", "").replace("/**", "").replace("*/", "");
            }
        }

        html += "</td>";
        html += "<td width='100'  valign='top' align='center'>" + getUserLevel(method) + "</td>";
        html += "</tr>";
        return html;
    }

    private static String getUserLevel(Method method) {
        Annotation[] annotations = method.getAnnotations();
        for (Annotation anno : annotations) {
            if (anno instanceof Administrator) {
                return "Administrator";
            }
            if (anno instanceof Editor) {
                return "Editor";
            }
        }
        return "Everyone";
    }

    private static String convertMethodToHTML(Map<String, Object> parsedMethod) {
        List<String> splittedArgs = (List<String>) parsedMethod.get("splittedArgs");
        String[] commentLines = (String[]) parsedMethod.get("commentLines");
        String html = "";

        Method method = (Method) parsedMethod.get("method");
        html += "<div class='method_name_header' name='" + method.getName() + "'>" + method.getName();
        html += "<span class='userlevel'>" + getUserLevel(method) + "</span>";
        html += "</div>";
        html += "<div class='method_container'>";

        for (String commentLine : commentLines) {
            if (!commentLine.contains("@")) {
                html += commentLine.replace("* ", "").replace("/**", "").replace("*/", "").trim();
            }
        }
        html += "<br><br>";

        html += "<div class='method'><span class='method_name'>" + method.getName() + "(";
        boolean found = false;
        for (String arg : splittedArgs) {
            html += arg + ",";
            found = true;
        }
        if (found) {
            html = html.substring(0, html.length() - 1);
        }

        html += ") returns " + parsedMethod.get("returnvalue").toString().replace("_", ".") + "</span></div>";

        if (splittedArgs.size() > 0) {
            html += createArgumentTable(method, splittedArgs, commentLines);
        }

        html += "<div class='example_code_heading'>PHP Example code</div>";
        html += fetchExampleCode(parsedMethod);
        html += "</div>";
        return html;
    }

    private static String fetchExampleCode(Map<String, Object> parsedMethod) {
        Method method = (Method) parsedMethod.get("method");
        Class manager = (Class) parsedMethod.get("manager");
        String methodName = method.getName();
        String managerName = manager.getSimpleName().substring(1);
        String path = "../com.getshop.client/integrationtest/managers/" + managerName + ".php";
        File check = new File(path);
        String html = "";
        String comment = "";
        if (check.exists()) {
            String content = readFile(path, false);
            String[] lines = content.split("\n");
            boolean start = false;
            for (String line : lines) {
                if (line.contains("test_") && start) {
                    break;
                }
                if (line.contains("test_" + methodName + "(") || start) {
                    html += line;
                    start = true;
                } else {
                    if (line.contains("/**")) {
                        comment = "";
                    }
                    comment += line;
                }
            }
        }
        if (html.isEmpty()) {
            System.out.println("Warning example code / integration test for manager: " + managerName + " : method " + methodName + " not found!");
            return "No example found.";
        }

        String finalResult = comment + html;
        finalResult = finalResult.substring(0, finalResult.lastIndexOf("}") + 1);
        finalResult = finalResult.replace("test_" + methodName, methodName);
        return "<pre>" + finalResult + "</pre>";
    }

    private static String createArgumentTable(Method method, List<String> splittedArgs, String[] commentLines) {
        String html = "";
        int i = 0;
        Class<?>[] types = method.getParameterTypes();
        html += "<br><table>";
        html += "<tr><th align='left'>Parameter Type</th><th align='left'>Arg</th><th align='left' style='padding-left:10px;'>Description</th></tr>";
        for (String arg : splittedArgs) {
            String type = types[i].getCanonicalName()
                    .replace("java.lang.", "")
                    .replace("com.thundashop.core.", "")
                    .replace("com.thundashop.app.", "");
            type = type.replace("java.util.", "");
            html += "<tr>";
            html += "<td valign='top'><span class='parameter_type'>" + type + "</span></td>";
            html += "<td valign='top'><span class='parameter_text'>" + arg + "</span></td>";
            html += "<td valign='top'><span class='parameter_desc'>" + findDescription(arg, commentLines) + "</span></td>";
            html += "</tr>";
            i++;
        }
        html += "</table>";
        return html;

    }

    private static String BuildApi(List<Class> list) throws ClassNotFoundException {
        String content = "";
        List<String> apiclasses = new ArrayList();

        for (Class entry : list) {
            String filename = getFileName(entry);
            if (filename.contains("<error>")) {
                continue;
            }
            Annotation[] annotations = entry.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof GetShopApi) {
                    content += "class API" + entry.getSimpleName().substring(1) + " {\r\n\r\n";
                    content += "      var $transport;\r\n\r\n";
                    content += "      function API" + entry.getSimpleName().substring(1) + "($transport) {\r\n";
                    content += "           $this->transport = $transport;\r\n";
                    content += "      }\r\n\r\n";
                    apiclasses.add(entry.getSimpleName().substring(1));
                    Method[] methods = entry.getMethods();
                    methods = sortMethods(methods);
                    for (Method method : methods) {
                        String path = entry.getCanonicalName();
                        path = path.replace(".", "/") + ".java";
                        Map<String, Object> doc = parseMethod(path, method, "PHP");

                        content += "     " + doc.get("comment") + "\n";
                        content += "     public function " + method.getName() + "(" + doc.get("args") + ") {\r\n";
                        String ifacename = entry.getCanonicalName();
                        ifacename = ifacename.replace("com.thundashop.", "");
                        content += buildApiContent((List<String>) doc.get("splittedArgs"), method.getName(), ifacename, (String) doc.get("return"));
                        content += "     }\r\n\r\n";
                    }
                    content += "}\r\n";
                }
            }
        }

        content += "class GetShopApi {\r\n";
        content += "\r\n";
        content += "      var $transport;\r\n";
        content += "      function GetShopApi($port, $host=\"localhost\", $sessionId) {\r\n";
        content += "           $this->transport = new CommunicationHelper();\r\n";
        content += "           $this->transport->port = $port;\r\n";
        content += "           $this->transport->sessionId = $sessionId;\r\n";
        content += "           $this->transport->host = $host;\r\n";
        content += "           $this->transport->connect();\r\n";
        content += "      }\r\n";
        for (String data : apiclasses) {
            content += "     /**\r\n";
            content += "      * @return " + data + "\r\n";
            content += "      */\r\n";
            content += "      public function get" + data + "() {\r\n";
            content += "           return new API" + data + "($this->transport);\r\n";
            content += "      }\r\n";
        }
        content += "}\r\n";
        return content;
    }

    private static void writePHPApi(String result) throws IOException {
        String thePath = "../com.getshop.client/events/API2.php";

        String content = "<?php\n\r";
        content += "/**\r\n";
        content += " * This library is built by GetShop and are used to communicate with the GetShop backend. \n";
        content += " * License: GPLv2\n";
        content += " * License URI: http://www.gnu.org/licenses/gpl-2.0.html\r\n";
        content += "*/\r\n\r\n";
        content += result;  
        content += "?>";

        File phpfile = new File(thePath);

        FileWriter fstream = new FileWriter(phpfile);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(content);
        out.flush();
        out.close();
    }

    private static String readFile(String path, boolean includeSource) {
        String strLine;
        String output = "";
        try {
            FileInputStream fstream = null;
            if (includeSource) {
                fstream = new FileInputStream("src/" + path);
            } else {
                fstream = new FileInputStream(path);
            }
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((strLine = br.readLine()) != null) {
                output += strLine + "\r\n";
            }
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return output;
    }

    public static Map<String, Object> parseMethod(String path, Method method, String type) {
        String content = readFile(path, true);

        List<String> splittedArgs = new ArrayList();
        String newArgs = createArgumentString(content, method, splittedArgs, type);

        newArgs = newArgs.trim();
        String[] commentLines = createCommentLines(content, content.indexOf(method.getName() + "("));
        String returnvalue = createReturnValue(method, type);

        String finalComment = "";
        for (String commentLine : commentLines) {
            if (commentLine.toLowerCase().contains("@return")) {
                finalComment += "     * @return " + returnvalue + "\n";
            } else {
                finalComment += commentLine + "\n";
            }
        }

        Map<String, Object> res = new HashMap();
        res.put("return", returnvalue);
        res.put("args", newArgs);
        res.put("splittedArgs", splittedArgs);
        res.put("comment", finalComment);
        res.put("commentLines", commentLines);
        res.put("returnvalue", returnvalue);
        res.put("method", method);

        return res;
    }

    private static String createParameterString(String paramType) {
        if (paramType.contains("core")) {
            paramType = paramType.substring(paramType.indexOf("core"));
            paramType = paramType.replace(".", "_");
        } else if (paramType.contains("app")) {
            paramType = paramType.substring(paramType.indexOf("app"));
            paramType = paramType.replace(".", "_");
        } else {
            paramType = "";
        }
        return paramType;
    }

    private static String createReturnValue(Method method, String type) {
        //Change the return value for the comment.
        String returnvalue = method.getReturnType().getCanonicalName();

        allImportClasses.put(method.getReturnType().getCanonicalName(), "");

        if (type.equals("JAVA")) {
            Type returnType = method.getGenericReturnType();
            String generics = "";
            if (returnType instanceof ParameterizedType) {
                generics = buildGenerics(returnType);
            }
            return method.getReturnType().getSimpleName() + generics;
        }

        if (returnvalue.contains("core")) {
            returnvalue = returnvalue.substring(returnvalue.indexOf("core"));
            returnvalue = returnvalue.replace(".", "_");
        } else if (returnvalue.contains("app")) {
            returnvalue = returnvalue.substring(returnvalue.indexOf("app"));
            returnvalue = returnvalue.replace(".", "_");
        } else {
            returnvalue = method.getReturnType().getSimpleName();
        }

        return returnvalue;
    }

    private static String[] createCommentLines(String readedContent, int methodStarting) {
        String cuttedText = readedContent.substring(0, methodStarting);
        String comment = cuttedText.substring(cuttedText.lastIndexOf("/**"));
        comment = comment.substring(0, comment.indexOf("*/") + 2);
        String[] commentLines = comment.split("\n");

        return commentLines;
    }

    private static void validateMethod(Method method, String[] splittedArgs) {
        if ((splittedArgs.length / 2) > method.getParameterTypes().length || (splittedArgs.length / 2) < method.getParameterTypes().length) {
            System.out.println("################################### ERROR ###############################");
            System.out.println("# The method " + method.getName() + " seems to have invalid parameters, make sure there is not spaces in collections! #");
            System.out.println("Splitted args:");
            for (String arg : splittedArgs) {
                System.out.print(arg + " ");
            }
            System.out.println();
            System.out.println("Number of args supposed to be: " + method.getParameterTypes().length);
            System.out.println("################################### ERROR ###############################");
            System.exit(0);
        }
    }

    private static String createArgumentString(String content, Method method, List<String> splittedArgs, String type) {

        int methodStart = content.indexOf(method.getName() + "(");
        int argStart = content.indexOf("(", methodStart) + 1;
        int argStop = content.indexOf(")", argStart);

        String args = content.substring(argStart, argStop);

        String newArgs = "";
        if (args.indexOf(" ") > 0) {
            String[] argSplitted = args.split(" ");
            Class<?>[] params = method.getParameterTypes();
            validateMethod(method, argSplitted);
            int i = 1;
            for (Class obj : params) {
                allImportClasses.put(obj.getCanonicalName(), "");
                String tmpArg = createParameterString(obj.getCanonicalName());
                if (type.equals("JAVA")) {
                    tmpArg = "";
                }
                if (tmpArg.trim().length() > 0) {
                    newArgs += "$" + tmpArg.toString();
                    splittedArgs.add(tmpArg.toString());
                } else {
                    String arg = argSplitted[i].toString().replace(",", "");
                    newArgs += "$" + arg;
                    splittedArgs.add(arg);
                }
                newArgs += ", ";
                i = i + 2;
            }
            newArgs = newArgs.substring(0, newArgs.length() - 2);
        }

        if (type.equals("JAVA")) {
            return args;
        }

        return newArgs;
    }

    private static void writeDocumentation(String html) throws IOException {
        File path = new File("../com.getshop.client/ROOT/documentation/index.html");

        html = "<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\" />" + html;
        html = "<script type=\"text/javascript\" src=\"doc.js\"></script>" + html;
        html = "<script type=\"text/javascript\" src=\"jquery-1.7.1.min.js\"></script>" + html;

        FileWriter fstream = new FileWriter(path);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(html);
        out.flush();
        out.close();
    }

    private static String createHTMLHeader(List<Class> list) {
        String html = "<div class='header'>\r\n";
        String[] storedClassesStrings = createSortedArray(list);

        for (String name : storedClassesStrings) {
            if (name.equals("IGetShop")) {
                continue;
            }
            Class entry = null;
            for (Class tmpEntry : list) {
                if (tmpEntry.getSimpleName().equals(name)) {
                    entry = tmpEntry;
                    break;
                }
            }
            name = name.substring(1);

            html += "<span class='manager_header' target='" + name + "'>" + name + "</span>\r\n";
            Method[] methods = entry.getMethods();
            String[] sorted = new String[methods.length];
            int i = 0;
            for (Method method : methods) {
                sorted[i] = method.getName();
                i++;
            }
            Arrays.sort(sorted);

            for (String methodName : sorted) {
                html += "<span class='manager_header_function' parent='" + name + "' target='" + methodName + "'>" + methodName + "</span>\r\n";
            }
        }
        html += "</div>";
        return html;
    }

    private static String findDescription(String arg, String[] commentLines) {
        for (String argument : commentLines) {
            if (argument.contains("@param " + arg)) {
                argument = argument.replace("* @param " + arg, "").trim();
                return argument;
            }
        }

        return "";
    }

    private static String[] createSortedArray(List<Class> list) {
        List<String> sortedClasses = new ArrayList();
        for (Class entry : list) {
            Annotation[] annotations = entry.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof GetShopApi) {
                    sortedClasses.add(entry.getSimpleName());
                }
            }
        }

        String[] storedClassesStrings = new String[sortedClasses.size()];
        int i = 0;
        for (String tmp : sortedClasses) {
            storedClassesStrings[i] = tmp;
            i++;
        }
        Arrays.sort(storedClassesStrings);
        return storedClassesStrings;
    }

    private static Class findEntryByName(String name, List<Class> list) {
        for (Class tmpclass : list) {
            if (tmpclass.getSimpleName().equals(name)) {
                return tmpclass;
            }
        }
        return null;
    }

    public static Method[] sortMethods(Method[] methods) {
        List<String> entries = new ArrayList();
        for (Method method : methods) {
            entries.add(method.getName());
        }

        String[] methodsnames = new String[entries.size()];
        int i = 0;
        for (String name : entries) {
            methodsnames[i] = name;
            i++;
        }
        Arrays.sort(methodsnames);

        Method[] sortedMethods = new Method[methodsnames.length];
        i = 0;
        for (String name : methodsnames) {
            for (Method method : methods) {
                if (method.getName().equals(name)) {
                    sortedMethods[i] = method;
                }
            }
            i++;
        }

        return sortedMethods;
    }

    public static String createDataObjectTable(String name) {
        name = name.substring(1);
        System.out.println(name);
        String html = "";
        for (Class theClass : dataObjects) {
            if (theClass.getName().toLowerCase().contains(name.toLowerCase())
                    && theClass.getName().contains(".data.")) {
                String translatedClass = theClass.getName().replace("com.thundashop.", "");
                html += translatedClass + ", php: " + translatedClass.replace(".", "_") + "<br>";
            }
        }
        return html;
    }

    public static List<Class> sortClasses(List<Class> classes) {
        List<String> toSort = new ArrayList();
        HashMap<String, Class> map = new HashMap();
        for (Class theClass : classes) {
            toSort.add(theClass.getName());
            map.put(theClass.getName(), theClass);
        }

        java.util.Collections.sort(toSort);
        List<Class> sortedList = new ArrayList();
        for (String key : toSort) {
            sortedList.add(map.get(key));
        }

        return sortedList;
    }

    private static String BuildJavaApi(List<Class> list) throws IOException {
        String content = "";
        List<String> apiclasses = new ArrayList();

        for (Class entry : list) {
            String filename = getFileName(entry);
            if (filename.contains("<error>") || !filename.startsWith("I")) {
                continue;
            }
            content = "";
            allImportClasses = new HashMap();
            Annotation[] annotations = entry.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof GetShopApi) {
                    content += "public class API" + entry.getSimpleName().substring(1) + " {\r\n\r\n";
                    content += "      public Transporter transport;\r\n\r\n";
                    content += "      public API" + entry.getSimpleName().substring(1) + "(Transporter transport){\r\n";
                    content += "           this.transport = transport;\r\n";
                    content += "      }\r\n\r\n";
                    apiclasses.add(entry.getSimpleName().substring(1));
                    Method[] methods = entry.getMethods();
                    methods = sortMethods(methods);
                    for (Method method : methods) {
                        String path = entry.getCanonicalName();
                        path = path.replace(".", "/") + ".java";
                        Map<String, Object> doc = parseMethod(path, method, "JAVA");

                        content += "     " + doc.get("comment") + "\n";
                        content += "     public " + doc.get("return") + " " + method.getName() + "(" + doc.get("args") + ")  throws Exception  {\r\n";
                        String ifacename = entry.getCanonicalName();
                        ifacename = ifacename.replace("com.thundashop.", "");
                        content += buildJavaApiContent((List<String>) doc.get("splittedArgs"), method.getName(), ifacename, (String) doc.get("return"));
                        content += "     }\r\n\r\n";
                    }
                    content += "}\r\n";
                }
            }
            writeJavaApi(content, filename.substring(1));
        }

        content = "package com.thundashop.api.managers;\n\n";
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

        return content;
    }

    private static void writeJavaApi(String result, String filename) throws IOException {
        String thePath = "../com.getshop.messages/src/com/thundashop/api/managers/API" + filename + ".java";

        String resultHeader = "package com.thundashop.api.managers;\n\n";
        resultHeader += "import com.google.gson.GsonBuilder;\n";
        resultHeader += "import com.google.gson.Gson;\n";
        resultHeader += "import com.google.gson.reflect.TypeToken;\n";
        resultHeader += "import java.lang.reflect.Type;\n";
        resultHeader += "import java.util.HashMap;\n";
        resultHeader += "import java.util.LinkedHashMap;\n";

        resultHeader += "import com.thundashop.core.common.JsonObject2;\n";
        for (String importName : allImportClasses.keySet()) {
            if (!importName.contains(".") || importName.startsWith("java.lang")) {
                continue;
            }
            resultHeader += "import " + importName + ";\n";
        }

        result = resultHeader + "\n" + result;

        File phpfile = new File(thePath);
        FileWriter fstream = new FileWriter(phpfile);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(result);
        out.flush();
        out.close();
    }

    private static void writeApiFile(String content) throws IOException {
        String thePath = "../com.getshop.messages/src/com/thundashop/api/managers/GetShopApi.java";
        File apifile = new File(thePath);
        FileWriter fstream = new FileWriter(apifile);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(content);
        out.flush();
        out.close();

    }

    private static String buildGenerics(Type returnType) {
        String generics = "";
        ParameterizedType type2 = (ParameterizedType) returnType;
        Type[] typeArguments = type2.getActualTypeArguments();
        for (Type typeArgument : typeArguments) {
            if (typeArgument instanceof Class) {
                Class typeArgClass = (Class) typeArgument;
                generics += typeArgClass.getSimpleName() + ",";
                allImportClasses.put(typeArgClass.getCanonicalName(), "");
            }
            if (typeArgument instanceof ParameterizedType) {
                ParameterizedType tmpType = (ParameterizedType)typeArgument;
                Class rawType = (Class)tmpType.getRawType();
                generics += rawType.getSimpleName() + buildGenerics(typeArgument) + ",";
            }
        }
        generics = "<" + generics.substring(0, generics.length() - 1) + ">";
        return generics;
    }
}
