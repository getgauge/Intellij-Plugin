package com.thoughtworks.gauge.execution;

import com.intellij.execution.Executor;
import com.intellij.execution.PsiLocation;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.thoughtworks.gauge.execution.runner.GaugeOutputToGeneralTestEventsConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class GaugeConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {
    public GaugeConsoleProperties(GaugeRunConfiguration config, String gauge, Executor executor) {
        super(config, gauge, executor);
        setIdBasedTestTree(true);
        setValueOf(HIDE_PASSED_TESTS, false);
        setValueOf(SCROLL_TO_SOURCE, true);
    }

    @Override
    public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
        return new GaugeOutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties);
    }

    @Override
    public boolean isIdBasedTestTree() {
        return true;
    }

    @Nullable
    @Override
    public SMTestLocator getTestLocator() {
        return (protocol, path, project, globalSearchScope) -> {
            try {
                String[] fileInfo = path.split(":");
                VirtualFile file = LocalFileSystem.getInstance().findFileByPath(fileInfo[0]);
                if (file == null) return new ArrayList<>();
                PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                if (psiFile == null) return new ArrayList<>();
                Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
                if (document == null) return new ArrayList<>();
                int line = Integer.parseInt(fileInfo[1]);
                PsiElement element = psiFile.findElementAt(document.getLineStartOffset(line));
                if (element == null) return new ArrayList<>();
                return Collections.singletonList(new PsiLocation<>(element));
            } catch (Exception e) {
                return new ArrayList<>();
            }
        };
    }
}
