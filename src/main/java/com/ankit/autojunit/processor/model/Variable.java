package com.ankit.autojunit.processor.model;

import lombok.Data;

@Data
public class Variable {

    private String className;
    private String classPackage;
    private String identifierName;
    private String initializationString;
    private boolean isPrimitive;

}
