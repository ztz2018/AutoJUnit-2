package com.ankit.autojunit.processor.arranger;

import com.ankit.autojunit.processor.model.ParsedUnit;

import java.util.List;

/**
 * The reason that the service is a class and not an interface, is that we don't want all the methods to be exposed
 * externally, but we do want to have all the methods defined by all the implementations of the class.
 * (Interfaces don't support non-public access specifiers)
 */

public abstract class ArrangeService {

    /**
     *  This is the wrapper API, which calls all the other APIs. This is the only one which is publicly accessible
     */
    public abstract void arrange(ParsedUnit parsedUnit);

    /**
     *  prepares className, injectMock, imports.
     *  The last one is the only reason that this method needs to be called at the end.
     */
    protected abstract String prepareBoilerPlate(ParsedUnit parsedUnit, List<String> allImports);

    /**
     *  Basically, this one operates on all the autowired Objects
     */
    protected abstract String mockTheAutowiredObjects(ParsedUnit parsedUnit, List<String> allImports);

    protected abstract String prepareSetupMethod(ParsedUnit parsedUnit, List<String> allImports);

    protected abstract String prepareAllTestMethod(ParsedUnit parsedUnit, List<String> allImports);

    protected abstract void callWriter(String classAsString, String classPackage, String className);

}
