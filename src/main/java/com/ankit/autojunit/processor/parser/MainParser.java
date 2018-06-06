package com.ankit.autojunit.processor.parser;

import com.ankit.autojunit.processor.model.ParsedUnit;
import com.ankit.autojunit.processor.model.child.MyMethodDeclaration;
import com.ankit.autojunit.processor.model.child.Variable;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MainParser {

    public ParsedUnit parseFileWithName(String fileLocation, String fileName, String fileExtension) {

        try {
            CompilationUnit compilationUnit = JavaParser.parse(new FileReader(fileLocation + fileName + "." + fileExtension));
            Optional<ClassOrInterfaceDeclaration> classMain = compilationUnit.getClassByName(fileName);
            if (classMain.isPresent()) {
                ClassOrInterfaceDeclaration operationServicempl = classMain.get();
                return parseTheDeclaration(operationServicempl);
            } else {
                throw new NullPointerException("Not found : " + fileName + "!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ParsedUnit parseTheDeclaration(ClassOrInterfaceDeclaration clazz) {

        List<String> allImports = getRequiredImports(clazz);
        String currentPackageName = getCurrentPackageName(clazz);
        List<Variable> autowiredObjects = getAutowiredObjects(clazz, allImports, currentPackageName);
        ParsedUnit parsedUnit = new ParsedUnit();

        parsedUnit.setClassName(clazz.getNameAsString());
        parsedUnit.setImports(allImports);
        parsedUnit.setAutowiredObjects(autowiredObjects);
        parsedUnit.setClassVariables(getGlobalVariables(clazz, parsedUnit.getImports(), currentPackageName));
        parsedUnit.setExternalServices(getExternalServices(clazz, autowiredObjects, allImports, currentPackageName));

        return parsedUnit;
    }

    /**
     *  Gets all the imports
     *  ToDo : As of now, it takes all the imports. Later, change to only required ones.
     */
    private List<String> getRequiredImports(ClassOrInterfaceDeclaration clazz)
    {
        List<String> imports = new ArrayList<>();
        ((CompilationUnit)clazz.getParentNode().get()).getImports().stream().forEach(imp ->{
            imports.add(imp.getNameAsString());
        });
        return imports;
    }

    private String getCurrentPackageName(ClassOrInterfaceDeclaration clazz) {
        String currentPackageName = ((PackageDeclaration)((CompilationUnit) clazz.getParentNode().get())
                .getPackageDeclaration().get()).toString();
        currentPackageName = currentPackageName.contains("\n") ? currentPackageName.split("\n")[0] : currentPackageName;
        currentPackageName = currentPackageName.contains(";") ? currentPackageName.split(";")[0] : currentPackageName;
        return currentPackageName;
    }

    /**
     *  Gets following details about autowired objects:
     *      1. Class name
     *      2. Identifier name
     *      3. Class package
     *      4. Initialization string (if any)
     */
    private List<Variable> getAutowiredObjects( ClassOrInterfaceDeclaration clazz, List<String> allImports, String currentPackageName)
    {
        List<Variable> autowiredObjects = new ArrayList<>();
        for (Node node : clazz.getChildNodes()) {
            if(node instanceof FieldDeclaration) {
                for (AnnotationExpr annotation : ((FieldDeclaration) node).getAnnotations()) {
                    if(annotation.getNameAsString().equals("Autowired")) {
                        Variable autowiredVariable = new Variable();
                        autowiredVariable.setClassName(((FieldDeclaration) node).getVariables().get(0).getType().asString());
                        autowiredVariable.setIdentifierName(((FieldDeclaration) node).getVariables().get(0).getNameAsString());
                        autowiredVariable.setClassPackage(searchImportsForClass(allImports, currentPackageName, autowiredVariable.getClassName()));
                        autowiredObjects.add(autowiredVariable);
                    }
                }
            }
        }
        return autowiredObjects;
    }

    /**
     * Take only those field declarations which
     *   #1> do not have any annotations
     *   ToDo : what about those containing @Value
     */
    private List<Variable> getGlobalVariables(ClassOrInterfaceDeclaration clazz, List<String> allImports, String currentPackageName)
    {
        List<Variable> variables = new ArrayList<>();
        clazz.getChildNodes().stream().forEach(node -> {
            if (node instanceof FieldDeclaration && CollectionUtils.isEmpty(((FieldDeclaration) node).getAnnotations())) {
                Variable variable = new Variable();
                variable.setClassName(((FieldDeclaration) node).getVariables().get(0).getTypeAsString());
                variable.setClassPackage(searchImportsForClass(allImports, currentPackageName, variable.getClassName()));
                variable.setIdentifierName(((FieldDeclaration) node).getVariables().get(0).getNameAsString());
                variables.add(variable);
            }
        });
        return variables;
    }

    /**
     *  Searches imports for a particular class, Returns current package if not found.
     *  ToDo : This API will fail if any import is of type import blabla.bla.*
     */
    private String searchImportsForClass(List<String> allImports, String currentPackageName, String className)
    {
        if (isPrimitiveOrJavaLangClass(className)) {
            return null;
        }
        String searchImport = currentPackageName.split(" ")[1]; // removing "package" keyword
        List<String> anyImport = allImports.stream().filter(currentImport -> currentImport.contains(className))
                .collect(Collectors.toList());
        if (anyImport != null && anyImport.size() != 0)
        {
            searchImport = anyImport.get(0);
        }
        return searchImport;
    }

    private boolean isPrimitiveOrJavaLangClass(String className) {
        String primitives[] = new String[]{"int", "char", "double", "float", "byte", "short", "boolean", "void", "long"};
        String javaLangClasses[] = new String[]{"Integer", "Double", "Float", "String", "Long", "Object", "StringBuilder"};

        for(String primitive : primitives) {
            if (className.equalsIgnoreCase(primitive)) {
                return true;
            }
        }
        for(String javaLangClass : javaLangClasses) {
            if (className.equalsIgnoreCase(javaLangClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     *  This API summarizes all the calls that are made to external services.
     *  Keeps track of the class name, method name, return type, arguments, method access
     */
    private List<MyMethodDeclaration> getExternalServices(ClassOrInterfaceDeclaration clazz, List<Variable> autowiredObjects,
            List<String> allImports, String currentPackageName)
    {
        List<MyMethodDeclaration> externalServices = new ArrayList<>();

        //        Object obj = ((MethodCallExpr) ((ExpressionStmt) ((BlockStmt) ((MyMethodDeclaration)clazz.getChildNodes().get(6)).getBody().get()).getStatements().get(2)).getExpression());
        //((MethodCallExpr)((ExpressionStmt)((BlockStmt)((IfStmt)((BlockStmt)((ForStmt)((BlockStmt)((MyMethodDeclaration)clazz.getChildNodes().get(7)).getBody().get()).getStatements().get(2)).getBody()).getStatements().get(0)).getThenStmt()).getStatements().get(0)).getExpression())

        // iterate over all the methods :
            // if the method is public
                // iterate over every stmt

        clazz.getChildNodes().stream().forEach(node -> {

            if (node instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) node;
                Iterator<Modifier> iterator = method.getModifiers().iterator();
                if(iterator.next().compareTo(Modifier.PUBLIC) == 0) {
                    //  ToDo : if the method is empty, below line could throw NPE
                    BlockStmt methodBody = method.getBody().get();
                    methodBody.getStatements().stream().forEach(stmt -> {recFindExternalServices(clazz, autowiredObjects,
                            externalServices, stmt);});
                }
            }
        });

        return  externalServices;
    }

    private void recFindExternalServices(ClassOrInterfaceDeclaration clazz, List<Variable> autowiredObjects,
                                         List<MyMethodDeclaration> externalServices, Object block)
    {
//      ToDo :   See this :
//        ((VariableDeclarationExpr) ((ExpressionStmt) block).getExpression()).getVariables().get(0).getTypeAsString();
        if (block instanceof IfStmt) {
            IfStmt ifStmtBlock = (IfStmt) block;
            ((BlockStmt)ifStmtBlock.getThenStmt()).getStatements().stream().forEach(stmt -> {
                recFindExternalServices(clazz, autowiredObjects, externalServices, stmt);
            });
            if(ifStmtBlock.getElseStmt().isPresent()) {
                ifStmtBlock = (IfStmt) ifStmtBlock.getElseStmt().get();
                ((BlockStmt)ifStmtBlock.getThenStmt()).getStatements().stream().forEach(stmt -> {
                    recFindExternalServices(clazz, autowiredObjects, externalServices, stmt);
                });
            }
        }
        else if (block instanceof ForStmt) {
            ForStmt forStmt = (ForStmt) block;
            ((BlockStmt)forStmt.getBody()).getStatements().stream().forEach(stmt ->{
                recFindExternalServices(clazz, autowiredObjects, externalServices, stmt);
            });
        } else if (block instanceof WhileStmt) {

        } else if (block instanceof SwitchStmt) {

        } else if (block instanceof ExpressionStmt) {
            Object expressionStmt = ((ExpressionStmt) block).getExpression();
            if(expressionStmt instanceof MethodCallExpr) {
                MethodCallExpr methodCallExpr = (MethodCallExpr) expressionStmt;
                Optional scope = methodCallExpr.getScope();
                if (!scope.isPresent()){    // private method call
                    // ToDo : hey what about protected methods?
                    MethodDeclaration privateMethod = getMethodDeclarationByName(clazz, methodCallExpr.getNameAsString());
                    privateMethod.getBody().get().getStatements().stream().forEach(stmt -> {recFindExternalServices(clazz,
                            autowiredObjects, externalServices, stmt);});

                } else if (scope.get() instanceof MethodCallExpr) {       // ignore : car.getBlaBla().add(new shit());

                } else if (scope.get() instanceof NameExpr) {       //  external calls : docService.createDoc(blabl);
                    MyMethodDeclaration methodDeclaration = new MyMethodDeclaration();
                    Variable autowiredObject = getAutowiredObjectForExternalServiceName(autowiredObjects, ((NameExpr) scope.get()).getNameAsString());
                    methodDeclaration.setMethodClassName(autowiredObject.getClassName());
                    methodDeclaration.setMethodClassPackage(autowiredObject.getClassPackage());
                    methodDeclaration.setMethodName(methodCallExpr.getNameAsString());
                    List<Variable> parameters = new ArrayList<>();

                    // 5 things are pending

                    externalServices.add(methodDeclaration);
                }

            }
        }

    }

    /**
     * A Helper api to find the autowired object by the name of the identifier
     */
    private Variable getAutowiredObjectForExternalServiceName(List<Variable> autowiredObjects, String externalServiceName) {
        return autowiredObjects.stream().filter(obj ->
                obj.getIdentifierName().equals(externalServiceName)).collect(Collectors.toList()).get(0);
    }

    /**
     * Another helper api to find the method by its name
     * ToDo : What about overloaded methods
     */
    private MethodDeclaration getMethodDeclarationByName(ClassOrInterfaceDeclaration clazz, String methodName) {
        for(Node node : clazz.getChildNodes()) {
            if(node instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) node;
                if(methodName.equalsIgnoreCase(method.getNameAsString())){
                    return method;
                }
            }
        }
        return null;
    }




































}
