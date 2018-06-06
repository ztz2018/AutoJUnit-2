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
import com.github.javaparser.ast.stmt.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MainParser implements ParsingService{

    public ParsedUnit parseTheDeclaration(ClassOrInterfaceDeclaration clazz) {

        List<String> allImports = getRequiredImports(clazz);
        String currentPackageName = getCurrentPackageName(clazz);
        List<Variable> autowiredObjects = getAutowiredObjects(clazz, allImports, currentPackageName);
        ParsedUnit parsedUnit = new ParsedUnit();

        parsedUnit.setClassName(clazz.getNameAsString());
        parsedUnit.setImports(allImports);
        parsedUnit.setAutowiredObjects(autowiredObjects);
        parsedUnit.setClassVariables(getGlobalVariables(clazz, parsedUnit.getImports(), currentPackageName));
        parsedUnit.setExternalServices(getExternalServices(clazz, autowiredObjects));

        return parsedUnit;
    }

    //   ToDo : As of now, it takes all the imports. Later, change to only required ones.
    public List<String> getRequiredImports(ClassOrInterfaceDeclaration clazz)
    {
        List<String> imports = new ArrayList<>();
        ((CompilationUnit)clazz.getParentNode().get()).getImports().stream().forEach(imp ->{
            imports.add(imp.getNameAsString());
        });
        return imports;
    }

    public String getCurrentPackageName(ClassOrInterfaceDeclaration clazz) {
        String currentPackageName = ((PackageDeclaration)((CompilationUnit) clazz.getParentNode().get())
                .getPackageDeclaration().get()).toString();
        currentPackageName = currentPackageName.contains("\n") ? currentPackageName.split("\n")[0] : currentPackageName;
        currentPackageName = currentPackageName.contains(";") ? currentPackageName.split(";")[0] : currentPackageName;
        return currentPackageName;
    }

    public List<Variable> getAutowiredObjects( ClassOrInterfaceDeclaration clazz, List<String> allImports, String currentPackageName)
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

    //  ToDo : what about those containing @Value
    public List<Variable> getGlobalVariables(ClassOrInterfaceDeclaration clazz, List<String> allImports, String currentPackageName)
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

     // ToDo : This API will fail if any import is of type import blabla.bla.*
    public String searchImportsForClass(List<String> allImports, String currentPackageName, String className)
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

    public boolean isPrimitiveOrJavaLangClass(String className) {
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

    public List<MyMethodDeclaration> getExternalServices(ClassOrInterfaceDeclaration clazz, List<Variable> autowiredObjects)
    {
        List<MyMethodDeclaration> externalServices = new ArrayList<>();

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

                    //  ToDo : we need to visit the external (autowired) class and locate the method in order to find its return type and arguments type

                    List<Variable> parameters = new ArrayList<>();


                    externalServices.add(methodDeclaration);
                }

            }
        }

    }

    public Variable getAutowiredObjectForExternalServiceName(List<Variable> autowiredObjects, String externalServiceName) {
        return autowiredObjects.stream().filter(obj ->
                obj.getIdentifierName().equals(externalServiceName)).collect(Collectors.toList()).get(0);
    }

    //  ToDo : What about overloaded methods
    public MethodDeclaration getMethodDeclarationByName(ClassOrInterfaceDeclaration clazz, String methodName) {
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
