package com.thundashop.core.start;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.start.GenerateApi.ApiMethod;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DocumentationBuilder {

    private final GenerateApi generator;
    private final List<Class> messageClasses;
    private final List<Class> coreClasses;
    private String path = "../com.getshop.client/ROOT/documentation/index.html";

    DocumentationBuilder(GenerateApi generator, List<Class> coreClasses, List<Class> messageClasses) {
        this.generator = generator;
        this.coreClasses = coreClasses;
        this.messageClasses = messageClasses;
    }

    void generate() throws IOException, ClassNotFoundException {
        String html = buildDocumentation();
        html = "<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\" />" + html;
        html = "<script type=\"text/javascript\" src=\"doc.js\"></script>" + html;
        html = "<script type=\"text/javascript\" src=\"jquery-1.7.1.min.js\"></script>" + html;
       generator.writeFile(html, path);
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

    private String createHTMLHeader(List<Class> list) {
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

    private String getFileName(Class entry) {
        return entry.getName().split("\\.")[entry.getName().split("\\.").length - 1];
    }

    private Class findEntryByName(String name, List<Class> list) {
        for (Class tmpclass : list) {
            if (tmpclass.getSimpleName().equals(name)) {
                return tmpclass;
            }
        }
        return null;
    }

    private String createClassDescription(String path) {

        String content = generator.readContent(path);
        int start = content.indexOf("/*");
        int stop = content.indexOf("*/");

        content = content.substring(start, stop);
        content = content.replace("* ", "");
        content = content.replace("/**", "");

        return content;
    }

    private String buildDocumentation() throws IOException, ClassNotFoundException {
        String html = createHTMLHeader(coreClasses);
        String[] storedClassesStrings = createSortedArray(coreClasses);
        for (String name : storedClassesStrings) {
            if (name.equals("IGetShop")) {
                continue;
            }
            Class entry = findEntryByName(name, coreClasses);
            String path = entry.getCanonicalName();
            path = "../com.getshop.core/src/"+path.replace(".", "/") + ".java";
            String filename = getFileName(entry);
            if (filename.contains("<error>")) {
                continue;
            }
            String theClass = entry.getSimpleName().substring(1);
            html += "<div class='manager " + theClass + "'>\r\n";
            html += "<div class='manager_header_text' target='" + theClass + "'>" + theClass + "</div>";
            html += "<div class='class_description'>" + createClassDescription(path);

            html += "<br><br><b>Table of methods:</b><br>";
            LinkedList<ApiMethod> sortedMethods = generator.getMethods(entry);
            html += "<table width='100%' bgcolor='#BBBBBB' cellspacing='1' cellpadding='2'>";
            String converted = "";
            for (ApiMethod method : sortedMethods) {
                String[] lines = method.commentLines;
                html += buildTableToc(method);
                converted += convertMethodToHTML(method);
            }
            html += "</table>";

            html += "<br><b>Dataobjects:</b><br>";
            html += createDataObjectTable(name);
            html += "</div>";
            html += converted;
            html += genrateDataObjects(name);
            html += "</div>\r\n";
        }
        
        return html;
    }
    
    private String genrateDataObjects(String name) {
        name = name.substring(1);
        System.out.println(name);
        String html = "<div class='dataObject'>";
        for (Class theClass : messageClasses) {
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
    
    public String createDataObjectTable(String name) {
        name = name.substring(1);
        System.out.println(name);
        String html = "";
        for (Class theClass : messageClasses) {
            if (theClass.getName().toLowerCase().contains(name.toLowerCase())
                    && theClass.getName().contains(".data.")) {
                String translatedClass = theClass.getName().replace("com.thundashop.", "");
                html += translatedClass + ", php: " + translatedClass.replace(".", "_") + "<br>";
            }
        }
        return html;
    }

    
    private String convertMethodToHTML(ApiMethod methodsummary) {
        String[] commentLines = methodsummary.commentLines;
        String html = "";

        Method method = methodsummary.method;
        html += "<div class='method_name_header' name='" + method.getName() + "'>" + method.getName();
        html += "<span class='userlevel'>" + methodsummary.userLevel + "</span>";
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
        for (String arg : methodsummary.arguments.keySet()) {
            html += arg + ",";
            found = true;
        }
        if (found) {
            html = html.substring(0, html.length() - 1);
        }

        html += ") returns " + method.getReturnType().getCanonicalName().toString().replace("_", ".") + "</span></div>";

        if (methodsummary.arguments.size() > 0) {
            html += createArgumentTable(methodsummary);
        }

        html += "<div class='example_code_heading'>PHP Example code</div>";
        html += fetchExampleCode(methodsummary);
        html += "</div>";
        return html;
    }
    
    
       private String createArgumentTable(ApiMethod methodsummary) {
        String html = "";
        int i = 0;
        Class<?>[] types = methodsummary.method.getParameterTypes();
        html += "<br><table>";
        html += "<tr><th align='left'>Parameter Type</th><th align='left'>Arg</th><th align='left' style='padding-left:10px;'>Description</th></tr>";
        for (String arg : methodsummary.arguments.keySet()) {
            String type = types[i].getCanonicalName()
                    .replace("java.lang.", "")
                    .replace("com.thundashop.core.", "")
                    .replace("com.thundashop.app.", "");
            type = type.replace("java.util.", "");
            html += "<tr>";
            html += "<td valign='top'><span class='parameter_type'>" + type + "</span></td>";
            html += "<td valign='top'><span class='parameter_text'>" + arg + "</span></td>";
            html += "<td valign='top'><span class='parameter_desc'>" + findDescription(arg, methodsummary.commentLines) + "</span></td>";
            html += "</tr>";
            i++;
        }
        html += "</table>";
        return html;

    }
      private String findDescription(String arg, String[] commentLines) {
        for (String argument : commentLines) {
            if (argument.contains("@param " + arg)) {
                argument = argument.replace("* @param " + arg, "").trim();
                return argument;
            }
        }

        return "";
    }

    private String buildTableToc(ApiMethod method) {
        String html = "<tr bgcolor='#FFFFFF'>";
        html += "<td width='10'  valign='top'>" + method.method.getName() + "</td>";
        html += "<td valign='top'>";

        for (String line : method.commentLines) {
            if (!line.contains("@")) {
                html += line.replace("* ", "").replace("/**", "").replace("*/", "");
            }
        }

        html += "</td>";
        html += "<td width='100'  valign='top' align='center'>" + method.userLevel + "</td>";
        html += "</tr>";
        return html;
    }
    
    
    private String fetchExampleCode(ApiMethod methodsummary) {
        Method method = (Method) methodsummary.method;
        Class manager = (Class) methodsummary.manager;
        String methodName = method.getName();
        String managerName = manager.getSimpleName().substring(1);
        String path = "../com.getshop.client/integrationtest/managers/" + managerName + ".php";
        File check = new File(path);
        String html = "";
        String comment = "";
        if (check.exists()) {
            String content = generator.readContent(path);
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
}
