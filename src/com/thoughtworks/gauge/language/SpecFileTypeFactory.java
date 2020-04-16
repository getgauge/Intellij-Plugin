/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.language;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

import static com.thoughtworks.gauge.Constants.*;

public class SpecFileTypeFactory extends FileTypeFactory {

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(SpecFileType.INSTANCE, SPEC_EXTENSION);
        fileTypeConsumer.consume(SpecFileType.INSTANCE, MD_EXTENSION);
        fileTypeConsumer.consume(ConceptFileType.INSTANCE, CONCEPT_EXTENSION);
    }


}
