package com.ankit.autojunit.processor;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class CommonUtil {

    /**
     * @param rootDir = src/main/java
     * @param companyDomain = com.ankit.autojunit
     * @param classPackage = sample_project
     * @param className = OperationServiceImpl
     * @param isReading = true when reading the file, false when writing
     * @return
     */
    public static String processFilePath(String rootDir, String companyDomain, String classPackage, String className, boolean isReading) {

        StringBuilder path = new StringBuilder();
        if (classPackage.contains(companyDomain)) {
            classPackage = classPackage.replace(companyDomain+".", "");
        }
        rootDir = rootDir.contains(".") ? rootDir.replaceAll("[/.]", "/") : rootDir;
        classPackage = isReading && classPackage.contains(".") ? classPackage.replaceAll("[/.]", "/") : classPackage;
        companyDomain = isReading && companyDomain.contains(".") ? companyDomain.replaceAll("[/.]", "/") : companyDomain;
        path.append(rootDir);
        path.append("/");
        path.append(companyDomain);
        path.append("/");
        path.append(classPackage);
        path.append("/");
        path.append(className);
        if (!isReading) {
            path.append("Test");
        }
        path.append(".java");
        return path.toString();
    }

    public static String processFilePath1(String rootDir, String companyDomain, String classPackage, String className, boolean isReading) {

        StringBuilder path = new StringBuilder();

        rootDir = rootDir.contains(".") ? rootDir.replaceAll("[/.]", "/") : rootDir;
        path.append(rootDir);
        path.append("/");

        companyDomain = isReading && companyDomain.contains(".") ? companyDomain.replaceAll("[/.]", "/") : companyDomain;

        path.append(companyDomain);
        path.append("/");
        path.append(classPackage);
        if (isReading) path.append(".");
        else path.append("/");
        path.append(className);
        path.append(".java");

//        return path.toString();
        return "src/main/java/com/ankit/autojunit/sample_project/OperationsServiceImpl.java";
    }

    public static void main(String [] args) throws Exception{
        String fileName = "src/main/java/com/ankit/autojunit/sample_project/OperationsServiceImpl.java";
        new FileReader(fileName);


    }

}
