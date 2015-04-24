package com.thoughtworks.gauge.stub;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.PsiFileImplUtil;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Consumer;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.IOUtil;
import com.intellij.util.io.KeyDescriptor;
import com.thoughtworks.gauge.language.ConceptFileType;
import com.thoughtworks.gauge.language.SpecFileType;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileStub extends FileBasedIndexExtension<String, List<Integer>> {
    @NonNls
    public static final ID<String,List<Integer>> NAME = ID.create("FormClassIndex");

    @NotNull
    @Override
    public ID<String, List<Integer>> getName() {
        return NAME;
    }

    @NotNull
    @Override
    public DataIndexer<String, List<Integer>, FileContent> getIndexer() {
        return new DataIndexer<String, List<Integer>, FileContent>() {
            @NotNull
            @Override
            public Map<String, List<Integer>> map(@NotNull FileContent fileContent) {
                List<Integer> offsets = new ArrayList<Integer>();
                List<PsiElement> steps = new ArrayList<PsiElement>();
                PsiFile psiFile = null;
                try {
                    psiFile = ((FileContentImpl) fileContent).createFileFromText(FileUtils.readFileToString(new File(fileContent.getFile().getPath())));
                } catch (IOException e) {
                    return Collections.emptyMap();
                }
                if (fileContent.getFileType() instanceof SpecFileType)
                    steps = new ArrayList<PsiElement>(PsiTreeUtil.collectElementsOfType(psiFile, SpecStepImpl.class));
                else if (fileContent.getFileType() instanceof ConceptFileType)
                    steps = new ArrayList<PsiElement>(PsiTreeUtil.collectElementsOfType(psiFile, ConceptStepImpl.class));
                for (PsiElement step : steps)
                    offsets.add(step.getTextOffset());
                return Collections.singletonMap(fileContent.getFile().getPath(), offsets);
            }
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return new KeyDescriptor<String>() {

            @Override
            public int getHashCode(String s) {
                return s.hashCode();
            }

            @Override
            public boolean isEqual(String s, String t1) {
                return s.equals(t1);
            }

            @Override
            public void save(@NotNull DataOutput dataOutput, String s) throws IOException {
                IOUtil.writeUTF(dataOutput, s);
            }

            @Override
            public String read(@NotNull DataInput dataInput) throws IOException {
                return IOUtil.readUTF(dataInput);
            }
        };
    }

    @NotNull
    @Override
    public DataExternalizer<List<Integer>> getValueExternalizer() {
        return new DataExternalizer<List<Integer>>() {

            @Override
            public void save(@NotNull DataOutput dataOutput, List<Integer> integers) throws IOException {
                String offsets = "";
                for (Integer integer : integers) offsets += integer.toString() + ",";
                IOUtil.writeUTF(dataOutput, offsets);
            }

            @Override
            public List<Integer> read(@NotNull DataInput dataInput) throws IOException {
                ArrayList<Integer> offsets = new ArrayList<Integer>();
                String s = IOUtil.readUTF(dataInput);
                for (String offset : s.split(","))
                    if (!offset.equals(""))
                        offsets.add(Integer.parseInt(offset));
                return offsets;
            }
        };
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new FileBasedIndex.FileTypeSpecificInputFilter() {
            @Override
            public void registerFileTypesUsedForIndexing(@NotNull Consumer<FileType> consumer) {
                consumer.consume(SpecFileType.INSTANCE);
                consumer.consume(ConceptFileType.INSTANCE);
            }

            @Override
            public boolean acceptInput(@NotNull VirtualFile virtualFile) {
                return virtualFile.getExtension() != null && (virtualFile.getExtension().equals("spec") || virtualFile.getExtension().equals("cpt"));
            }
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 0;
    }
}
