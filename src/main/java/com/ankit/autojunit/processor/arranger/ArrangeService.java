package com.ankit.autojunit.processor.arranger;

import com.ankit.autojunit.processor.model.ParsedUnit;

import java.util.List;

public abstract class ArrangeService {

    public abstract void arrange(ParsedUnit parsedUnit);

    protected abstract void prepareBoilerPlate();

    protected abstract List<String> arrangeImports(List<String> allImports);

    protected abstract void prepareSetupMethod();

    protected abstract void prepareTestMethod();

    protected abstract void prepareClassAsString();

}
