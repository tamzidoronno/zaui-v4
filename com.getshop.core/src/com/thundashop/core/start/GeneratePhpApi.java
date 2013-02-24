/**
 * This class is a part of the thundashop project.
 *
 * All rights reserved
 *
 *
 */
package com.thundashop.core.start;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class GeneratePhpApi {

    private static Iterable<Class> list;

    /**
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
//                    System.out.println(packageName);
//                    if (packageName.startsWith("."))
//                        packageName = packageName.substring(1);
                            
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
        
        //Delete all files.
        File file = new File(thePath + "/app");
        file.delete();
        file = new File(thePath + "/core");
        file.delete();
        
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
            if (type.contains("thundashop")) {
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
            String filePath = getPath(entry);
            String classname = createPhpClassName(entry, filename);
            System.out.println("generating class: "+classname);
            String fileContent = createPhpFileContent(entry, classname, filename, filePath);

            File phpfile = new File(filePath + "/" + filename + ".php");

            FileWriter fstream = new FileWriter(phpfile);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(fileContent);
            out.flush();
            out.close();
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
        File file = new File("../com.getshop.messages/build/classes/");
        File apps = new File("../com.getshop.app/build/classes/");

        List<Class> classes = new ArrayList<Class>();
        for (Class classfile : GeneratePhpApi.findClasses(apps, "")) {
            if (classfile.getName().contains("data")
                    || classfile.getName().contains("answer")
                    || classfile.getName().contains("events")) {
                classes.add(classfile);
            }
        }
        Generate(classes);

        Generate(GeneratePhpApi.findClasses(file, ""));
        String core = BuildApi(GeneratePhpApi.findClasses(file, ""));
        String app = BuildApi(GeneratePhpApi.findClasses(apps, ""));

        String result = core;
        result += "\t\r\n\r\n\r\n";
        result += "\t/*###############################################\r\n";
        result += "\tApp classes\r\n";
        result += "\t###############################################*/\r\n";
        result += app;
        writePHPApi(result);
    }

    private static String getFileName(Class entry) {
        return entry.getName().split("\\.")[entry.getName().split("\\.").length - 1];
    }

    private static String BuildApi(List<Class> list) throws ClassNotFoundException {

        System.out.println("----------------------------------------");
        System.out.println("GENERATING PHP API FILE");
        System.out.println("----------------------------------------");

        String content = "";

        for (Class entry : list) {
            String filename = getFileName(entry);
            if (filename.contains("<error>")) {
                continue;
            }

            String filePath = getPath(entry);
            String classname = createPhpClassName(entry, filename);
            content += "\t/**\r\n";
            content += "\t * @return \\" + classname + "\r\n";
            content += "\t */\r\n";
            content += "\tpublic static function " + classname + "() {\r\n";
            content += "\t\treturn new \\" + classname + "();\r\n";
            content += "\t}\r\n";
            content += "\t\r\n";
        }
        return content;
    }

    private static void writePHPApi(String result) throws IOException {
        String thePath = "../com.getshop.client/events/API.php";

        String content = "<?php\n\r\n\r";
        content += "class API {\r\n\r\n";
        content += result;
        content += "}\r\n";
        content += "?>";

        File phpfile = new File(thePath);

        FileWriter fstream = new FileWriter(phpfile);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(content);
        out.flush();
        out.close();
    }
}
