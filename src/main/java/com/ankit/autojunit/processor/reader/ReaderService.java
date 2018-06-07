package com.ankit.autojunit.processor.reader;

import com.ankit.autojunit.processor.model.ParsedUnit;

public interface ReaderService {

    ParsedUnit parseFileWithName(String fileLocation, String fileName);

    String processFilePath(String rootDir, String classPackage, String className);
}
