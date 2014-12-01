package com.thoughtworks.gauge.language;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class SpecFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(SpecFileType.INSTANCE, "spec");
        fileTypeConsumer.consume(SpecFileType.INSTANCE, "md");
        fileTypeConsumer.consume(ConceptFileType.INSTANCE, "cpt");
    }


}