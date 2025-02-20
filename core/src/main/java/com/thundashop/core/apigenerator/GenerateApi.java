package com.thundashop.core.apigenerator;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopLogHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;

public class GenerateApi {

    private final List<Class> coreClasses;
    private final List<Class> messageClasses;
    private final LinkedList<Class> allManagers;
    private HashMap<String, Integer> methodProcessed = new HashMap();
    private HashMap<String, String> md5sums = new HashMap();
    private HashMap<String, String> md5sumsnew = new HashMap();
    private String type = "defualt";
    private final String pathToClients;
    private boolean addMain = true;
    private final String pathToSource;
    private final boolean debug;
    private final LinkedList<Class> changedManagers;

    public GenerateApi(String pathToCore, String pathToMessages, String pathToClients, String pathToSource, boolean debug) throws ClassNotFoundException {
        if (GetShopLogHandler.isDeveloper) {
            loadMd5Sums();
        }
        
        File core = null;
        File messages = null;
        
        this.debug = debug;
        this.pathToClients = pathToClients;
        this.addMain = pathToClients == null;
        this.pathToSource = pathToSource;
        
        if (pathToCore == null && pathToMessages == null) {
            core = new File("../core/");
            
            messages = new File("../messages/");
        } else {
            core = new File(pathToCore);
            messages = new File(pathToMessages);
        }
        System.out.println( this.getClass().getName() + " generateapi core " + core.getAbsolutePath());
        
        
        coreClasses = findClasses(core, true);
        List<Class> coreClassesChanged = findClasses(core, false);
        messageClasses = findClasses(messages, true);
        allManagers = filterClasses(coreClasses);
        changedManagers = filterClasses(coreClassesChanged);
        GetShopLogHandler.logPrintStatic("Done finding datasource", null);

    }

    public void analyseApplications() throws UnknownHostException, IOException, ClassNotFoundException {
        AnalyseApplications analyser = new AnalyseApplications(this, allManagers, messageClasses, pathToClients);
        analyser.generate();
        ImportApiCallsToApplications importer = new ImportApiCallsToApplications();
        importer.run();
    }

    public void buildDocumentation() throws IOException, ClassNotFoundException {
        DocumentationBuilder docbuilder = new DocumentationBuilder(this, coreClasses, messageClasses);
        docbuilder.generate();
    }

    public void generateJavaApi() throws IOException {
        JavaApiBuilder javaapi = new JavaApiBuilder(this, coreClasses, messageClasses, pathToSource, changedManagers);
        javaapi.generate();
    }

    public void generatePHPApi() throws Exception {
        PHPApiBuilder phpapi = new PHPApiBuilder(this, allManagers, messageClasses, pathToClients, changedManagers);
        phpapi.generate();
    }

    private void generatePythonApi() {
        PythonApiBuilder pythonApiBuilder = new PythonApiBuilder(this, allManagers, messageClasses);
        pythonApiBuilder.generate();
    }

    private LinkedList<ApiMethod> removeOverLoadedMethods(LinkedList<ApiMethod> methods) {
        HashMap<String, ApiMethod> toKeep = new HashMap();
        List<ApiMethod> toRemove = new ArrayList();
        for(ApiMethod method : methods) {
            if(toKeep.containsKey(method.methodName)) {
                if(toKeep.get(method.methodName).arguments.size() < method.arguments.size()) {
                    toRemove.add(toKeep.get(method.methodName));
                    toKeep.put(method.methodName, method);
                }
            } else {
                toKeep.put(method.methodName, method);
            }
        }

        for(ApiMethod remove : toRemove) {
            GetShopLogHandler.logPrintStatic("Removing: " + remove.methodName + " with : " + remove.arguments.size() + " arguments", null);
            methods.remove(remove);
        }
        
        return methods;
    }

    private boolean hasChanged(File file) {
        if (!GetShopLogHandler.isDeveloper)
            return true;

        String md5sum = getMd5Sum(file);
        String oldMd5Sum = md5sums.get(file.getAbsolutePath());
        
        md5sumsnew.put(file.getAbsolutePath(), md5sum);
        
        if (md5sum.equals(oldMd5Sum)) {
            return false;
        }
        
        return true;
    }
    
    public String getChecksum(Serializable object) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(baos.toByteArray());
            return DatatypeConverter.printHexBinary(thedigest);
        } catch (IOException x2) {
            x2.printStackTrace();
        } catch (NoSuchAlgorithmException x2) {
            x2.printStackTrace();
        } finally {
            try {
                oos.close();
                baos.close();
            } catch (IOException ex) {
                Logger.getLogger(GenerateApi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return null;
    }

    private String getMd5Sum(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            fis.close();
            return md5;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }

    private void loadMd5Sums() {
        try (ObjectInputStream oos = new ObjectInputStream(new FileInputStream("/tmp/javaGeneratorMd5Sums.ser"))) {
            md5sums = (HashMap)oos.readObject();
        } catch (Exception ex) {
        }
    }

    private void saveMd5Sums() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/tmp/javaGeneratorMd5Sums.ser"))) {
            oos.writeObject(md5sumsnew);
        } catch (Exception ex) {
        }
    }

    void makeEmptyFile(String eventsPath, String core_getshoplocksystem_LockGroup, String coregetshoplocksystem, String lockGroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public class ApiMethod {

        public String methodName;
        public Class manager;
        public String generics;
        public Method method;
        public String[] commentLines;
        public LinkedHashMap<String, String> arguments;
        String userLevel;
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, Exception {
        GenerateApi ga = new GenerateApi(null, null, null, null, false);
        ga.generate();
    }

    void setType(String type) {
        this.type = type;
    }

    private LinkedHashMap<String, String> findArguments(String javafile, Method method, Class filteredClass) {
        int offset = 0;
        String key = filteredClass.getSimpleName()+"_" + method.getName();
        if(methodProcessed.containsKey(key)) {
            offset = methodProcessed.get(key) + 1;
        }
        int methodStart = javafile.indexOf(method.getName() + "(", offset);
        methodProcessed.put(key, methodStart);
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
                GetShopLogHandler.logPrintStatic("Failed to parse method: " + method.getName() + " arguments: " + args, null);
                GetShopLogHandler.logPrintStatic("Number of arguments is incorrect (this is being splitted on spaces, make sure HashMap<String, String> is equal to HashMap<String,String> for example.", null);
                System.exit(0);
            }
            
            if (i >= method.getParameterTypes().length) {
                GetShopLogHandler.logPrintStatic("Problem with ( is it overloading?, remember to order it with first least arguments first in both interface and java class) ; " + method.getName() + " ( " + filteredClass + ")", null);
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
                generics += typeArgClass.getCanonicalName()+ ",";
            }
            if (typeArgument instanceof ParameterizedType) {
                ParameterizedType tmpType = (ParameterizedType) typeArgument;
                Class rawType = (Class) tmpType.getRawType();
                generics += rawType.getCanonicalName() + buildGenerics(typeArgument) + ",";
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
    public List<Class> findClasses(File directory, boolean ignoreCache) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClasses(file, ignoreCache));
            } else if (fileName.endsWith(".class") && !fileName.contains("$") && (hasChanged(file) || ignoreCache)) {
                Class _class;
                try {
                    String addText = addMain ? "java/main/" : "";
                    if (file.getAbsolutePath().contains("/build/classes/" + addText)) {
                        String offset = "/build/classes/" + addText;
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

    public void generate() throws ClassNotFoundException, IOException, Exception {
        GetShopLogHandler.logPrintStatic("Starting generate api", null);
        generatePHPApi();
        generateJavaApi();
//        generatePythonApi();
//        buildDocumentation();
        if (!GetShopLogHandler.isDeveloper) {
            analyseApplications();
        }
        
        if (GetShopLogHandler.isDeveloper) {
            saveMd5Sums();
        }
    }
    
    

    public LinkedList<Class> filterClasses(List<Class> apiClasses) {
        GetShopLogHandler.logPrintStatic("Filtering classes", null);
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

        if (debug) {
            GetShopLogHandler.logPrintStatic("Number of annotated api classes: " + filteredApiClasses.size(), null);
        }
        return filteredApiClasses;
    }

    private String getUserLevel(Method method) {
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

    private static String[] createCommentLines(String readedContent, int methodStarting) {
        String cuttedText = readedContent.substring(0, methodStarting);
        if(cuttedText.lastIndexOf("/**") < 0) {
            GetShopLogHandler.logPrintStatic("Failed to find comment in : " + readedContent, null);
            System.exit(0);
        }
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
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileWriter fstream = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(content);
        out.flush();
        out.close();
    }

    public LinkedList<ApiMethod> getMethods(Class filteredClass) {
        LinkedList<ApiMethod> methods = new LinkedList();
        methodProcessed = new HashMap();
        Method[] managermethods = filteredClass.getMethods();
        for (Method method : managermethods) {
            ApiMethod result = new ApiMethod();
            result.manager = filteredClass;
            result.method = method;
            result.methodName = method.getName();
            File filePath = pathToSource == null ? new File(".") :  new File(pathToSource);
            String path = filePath.getAbsolutePath() + "/src/main/java/" + filteredClass.getName().replace(".", "/") + ".java";
            String javafile = readContent(path);
            if(javafile.isEmpty()) {
                GetShopLogHandler.logPrintStatic("Unable to load java file : " + path, null);
                System.exit(0);
            }
            result.arguments = findArguments(javafile, method, filteredClass);
            result.generics = buildGenericsString(method);
            result.commentLines = createCommentLines(javafile, javafile.indexOf(method.getName() + "("));
            result.userLevel = getUserLevel(method);
            methods.add(result);
        }
        
        
        if(type.equals("php")) {
            methods = removeOverLoadedMethods(methods);
        }
        
        String spaces = "   ";
        if (methods.size() >= 10) {
            spaces = "  ";
        }
        if (methods.size() >= 100) {
            spaces = " ";
        }
        
        if (debug)
            GetShopLogHandler.logPrintStatic("Number of api calls in total: " + methods.size() + spaces + "for manager: " + filteredClass.getSimpleName(), null);


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

    public String readContent(String path) {
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
            e.printStackTrace();
            System.exit(0);
        }
        return output;
    }
}
