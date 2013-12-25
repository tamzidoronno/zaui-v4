package com.thundashop.core.start;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.GetShopApi;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class GenerateApi {

    private LinkedList<Class> filterDataObjects(List<Class> coreClasses) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public class ApiMethod {
        public String methodName;
        public Class manager;
        public String generics;
        public Method method;
        public String[] commentLines;
        public HashMap<String, String> arguments;
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        GenerateApi ga = new GenerateApi();
        ga.generate();

    }
    
    private LinkedHashMap<String, String> findArguments(String javafile, Method method) {
        int methodStart = javafile.indexOf(method.getName() + "(");
        int argStart = javafile.indexOf("(", methodStart) + 1;
        int argStop = javafile.indexOf(")", argStart);

        String args = javafile.substring(argStart, argStop);
        if (args.trim().isEmpty()) {
            return new LinkedHashMap();
        }

        LinkedHashMap map = new LinkedHashMap();
        String[] argresults = args.split(", ");
        int i = 0;
        for (String argresult : argresults) {
            String[] argmap = argresult.split(" ");
            if ((argmap.length != 2)) {
                System.out.println("Failed to parse method: " + method.getName() + " arguments: " + args);
                System.out.println("Number of arguments is incorrect (this is being splitted on spaces, make sure HashMap<String, String> is equal to HashMap<String,String> for example.");
                System.exit(0);
            }
            map.put(argmap[1], method.getParameterTypes()[i].getCanonicalName());
            i++;
        }
        return map;
    }

    private static String buildGenerics(Type returnType) {
        String generics = "";
        ParameterizedType type2 = (ParameterizedType) returnType;
        Type[] typeArguments = type2.getActualTypeArguments();
        for (Type typeArgument : typeArguments) {
            if (typeArgument instanceof Class) {
                Class typeArgClass = (Class) typeArgument;
                generics += typeArgClass.getSimpleName() + ",";
            }
            if (typeArgument instanceof ParameterizedType) {
                ParameterizedType tmpType = (ParameterizedType) typeArgument;
                Class rawType = (Class) tmpType.getRawType();
                generics += rawType.getSimpleName() + buildGenerics(typeArgument) + ",";
            }
        }
        generics = "<" + generics.substring(0, generics.length() - 1) + ">";
        return generics;
    }

    private String buildGenericsString(Method method) {
        Type returnType = method.getGenericReturnType();
        String generics = "";
        if (returnType instanceof ParameterizedType) {
            generics = buildGenerics(returnType);
        }

        return generics;
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
    public static List<Class> findClasses(File directory) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClasses(file));
            } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                Class _class;
                try {
                    if (file.getAbsolutePath().contains("/build/classes/")) {
                        String offset = "/build/classes/";
                        String className = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(offset) + offset.length());
                        className = className.replace("/", ".");
                        className = className.replace(".class", "");
                        _class = Class.forName(className);
                        classes.add(_class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }
        return classes;
    }

    private void generate() throws ClassNotFoundException, IOException {
        File core = new File("../com.getshop.core/");
        File messages = new File("../com.getshop.messages/");
        List<Class> coreClasses = findClasses(core);
        List<Class> messageClasses = findClasses(messages);
        LinkedList<Class> allManagers = filterClasses(coreClasses);
        
        //Generate the php api.
        PHPApiBuilder phpapi = new PHPApiBuilder(this, allManagers, messageClasses);
        phpapi.generate();
        
        //Generate the java api.
        JavaApiBuilder javaapi = new JavaApiBuilder(this, coreClasses, messageClasses);
        javaapi.generate();
    }

    public LinkedList<Class> filterClasses(List<Class> apiClasses) {
        LinkedList<Class> filteredApiClasses = new LinkedList();
        for (Class apiClass : apiClasses) {
            for (Annotation anno : apiClass.getAnnotations()) {
                if (anno instanceof GetShopApi) {
                    filteredApiClasses.add(apiClass);
                }
                if (anno instanceof DataCommon) {
                    filteredApiClasses.add(apiClass);
                }
            }
        }
        
        if (filteredApiClasses.size() > 0) {
            Collections.sort(filteredApiClasses, new Comparator<Class>() {
                @Override
                public int compare(final Class object1, final Class object2) {
                    return object1.getSimpleName().compareTo(object2.getSimpleName());
                }
            });
        }
        
        System.out.println("Number of annotated api classes: " + filteredApiClasses.size());
        return filteredApiClasses;
    }

    private static String[] createCommentLines(String readedContent, int methodStarting) {
        String cuttedText = readedContent.substring(0, methodStarting);
        String comment = cuttedText.substring(cuttedText.lastIndexOf("/**"));
        comment = comment.substring(0, comment.indexOf("*/") + 2);
        String[] commentLines = comment.split("\n");
        for (int i = 0; i < commentLines.length; i++) {
            commentLines[i] = commentLines[i].trim();
        }
        return commentLines;
    }

    public void writeFile(String content, String path) throws IOException {
         File file = new File(path);
        FileWriter fstream = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(content);
        out.flush();
        out.close();
    }
    
    public LinkedList<ApiMethod> getMethods(Class filteredClass) {
        LinkedList<ApiMethod> methods = new LinkedList();
        Method[] managermethods = filteredClass.getMethods();
        for (Method method : managermethods) {
            ApiMethod result = new ApiMethod();
            result.manager = filteredClass;
            result.method = method;
            result.methodName = method.getName();

            String path = new File(".").getAbsolutePath() + "/src/" + filteredClass.getName().replace(".", "/") + ".java";
            String javafile = readContent(path);
            result.arguments = findArguments(javafile, method);
            result.generics = buildGenericsString(method);
            result.commentLines = createCommentLines(javafile, javafile.indexOf(method.getName() + "("));
            methods.add(result);
        }
        String spaces = "   ";
        if (methods.size() >= 10) {
            spaces = "  ";
        }
        if (methods.size() >= 100) {
            spaces = " ";
        }
        System.out.println("Number of api calls in total: " + methods.size() + spaces + "for manager: " + filteredClass.getSimpleName());


        if (methods.size() > 0) {
            Collections.sort(methods, new Comparator<ApiMethod>() {
                @Override
                public int compare(final ApiMethod object1, final ApiMethod object2) {
                    return object1.methodName.compareTo(object2.methodName);
                }
            });
        }
        return methods;
    }

    private String readContent(String path) {
        String strLine;
        String output = "";
        try {
            FileInputStream fstream = null;
            fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((strLine = br.readLine()) != null) {
                output += strLine + "\r\n";
            }
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
            System.exit(0);
        }
        return output;
    }
}
