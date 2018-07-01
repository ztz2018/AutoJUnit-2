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
     *  prepares className, injectMock
     */
    protected abstract String prepareBoilerPlate(ParsedUnit parsedUnit);

    /**
     *  Basically, this one operates on all the autowired Objects
     */
    protected abstract void mockTheAutowiredObjects();

    protected abstract List<String> arrangeImports(List<String> allImports);

    protected abstract void prepareSetupMethod();

    protected abstract void prepareTestMethod();

    protected abstract void prepareClassAsString();

    protected abstract void callWriter(String classAsString, String classPackage, String className);

}
