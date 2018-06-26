package com.ankit.autojunit.processor.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestMethodDefinition {

    private String testMethodName;
    private String originalMethodName;
    private List<Variable> originalMethodParameters = new ArrayList<>();

}
