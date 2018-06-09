package com.ankit.autojunit.processor.model.child;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MyMethodDeclaration {

    private String methodClassName;
    private String methodClassPackage;
    private String methodName;
    private String methodReturnTypeClass;
    private String methodReturnTypeClassPackage;
    private List<Variable> methodArguments = new ArrayList<>();

}
