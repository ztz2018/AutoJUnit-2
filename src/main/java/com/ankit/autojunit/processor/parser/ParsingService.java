package com.ankit.autojunit.processor.parser;

import com.ankit.autojunit.processor.model.ParsedUnit;
import com.ankit.autojunit.processor.model.child.MyMethodDeclaration;
import com.ankit.autojunit.processor.model.child.Variable;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.List;

public interface ParsingService {

    ParsedUnit parseTheDeclaration(ClassOrInterfaceDeclaration clazz);

    /**
     *  Gets all the imports
     */
    List<String> getImports(ClassOrInterfaceDeclaration clazz);

    String getCurrentPackageName(ClassOrInterfaceDeclaration clazz);

    /**
     *  Gets following details about autowired objects:
     *      1. Class name
     *      2. Identifier name
     *      3. Class package
     *      4. Initialization string (if any)
     */
    List<Variable> getAutowiredObjects(ClassOrInterfaceDeclaration clazz, List<String> allImports, String currentPackageName);

    /**
     * Take only those field declarations which
     *   #1> do not have any annotations
     */
    List<Variable> getGlobalVariables(ClassOrInterfaceDeclaration clazz, List<String> allImports, String currentPackageName);

    /**
     *  Searches imports for a particular class, Returns current package if not found.
     */
    String searchImportsForClass(List<String> allImports, String currentPackageName, String className);

    boolean isPrimitiveOrJavaLangClass(String className);

    /**
     *  This API summarizes all the calls that are made to external services.
     *  Keeps track of the class name, method name, return type, arguments, method access
     *  This api calls a recursive method which iterates over all the statements in a block recursively.
     */
    List<MyMethodDeclaration> getExternalServices(ClassOrInterfaceDeclaration clazz, List<Variable> autowiredObjects);

    void recFindExternalServices(ClassOrInterfaceDeclaration clazz, List<Variable> autowiredObjects,
                List<MyMethodDeclaration> externalServices, Object block);

    /**
     * A Helper api to find the autowired object by the name of the identifier
     */
    Variable getAutowiredObjectForExternalServiceName(List<Variable> autowiredObjects, String externalServiceName);

    /**
     * Another helper api to find the method by its name
     */
    MethodDeclaration getMethodDeclarationByName(ClassOrInterfaceDeclaration clazz, String methodName);

    void addExternalServiceDetails(MyMethodDeclaration myMethodDeclaration);

}
