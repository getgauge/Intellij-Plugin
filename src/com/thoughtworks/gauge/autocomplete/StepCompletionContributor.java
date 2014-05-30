package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.thoughtworks.gauge.core.GaugeConnection;
import com.thoughtworks.gauge.language.Specification;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;
import com.thoughtworks.gauge.util.SocketUtils;

import java.io.File;
import java.io.IOException;

public class StepCompletionContributor extends CompletionContributor {


    public StepCompletionContributor() throws IOException {
        int freePort = SocketUtils.findFreePort();

        ProcessBuilder gauge = new ProcessBuilder("gauge", "--daemonize");
        gauge.environment().put("GAUGE_API_PORT", String.valueOf(freePort));
        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = DataKeys.PROJECT.getData(dataContext);
        gauge.directory(new File(project.getBasePath()));
        Process process = gauge.start();

        GaugeConnection gaugeConnection = new GaugeConnection(freePort);

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.STEP).withLanguage(Specification.INSTANCE), new StepCompletionProvider(gaugeConnection));
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.DYNAMIC_ARG).withLanguage(Specification.INSTANCE), new DynamicArgCompletionProvider());
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.ARG).withLanguage(Specification.INSTANCE), new StaticArgCompletionProvider());
    }

}