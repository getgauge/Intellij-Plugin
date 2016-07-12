package com.thoughtworks.gauge.execution;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.impl.dataRules.VirtualFileRule;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.language.SpecFile;
import com.thoughtworks.gauge.language.SpecFileType;
import com.thoughtworks.gauge.language.psi.SpecScenario;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GaugeExecutionProducerTest {
    @Test
    public void shouldSetupConfigurationFromContext() {
        GaugeRunConfiguration configuration = mock(GaugeRunConfiguration.class);
        ConfigurationContext context = mock(ConfigurationContext.class);
        DataContext dataContext = mock(DataContext.class);
        Module module = mock(Module.class);
        PsiElement element = mock(SpecFile.class);
        SpecFile file = mock(SpecFile.class);
        VirtualFile virtualFile = mock(VirtualFile.class);

        when(file.getVirtualFile()).thenReturn(virtualFile);
        when(context.getDataContext()).thenReturn(dataContext);
        when(context.getModule()).thenReturn(module);
        when(context.getPsiLocation()).thenReturn(element);
        when(element.getContainingFile()).thenReturn(file);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY.getName())).thenReturn(new VirtualFile[]{mock(VirtualFile.class)});

        boolean success = new GaugeExecutionProducer().setupConfigurationFromContext(configuration, context, null);

        assertTrue("should setup configuration from context. Expected: true, Actual: false", success);
        verify(configuration, times(1)).setModule(module);
    }

    @Test
    public void shouldNotSetupConfigurationIfDataContextIsNotPresentInConfigurationContext() {
        GaugeRunConfiguration configuration = mock(GaugeRunConfiguration.class);
        ConfigurationContext context = mock(ConfigurationContext.class);
        DataContext dataContext = mock(DataContext.class);

        when(context.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY.getName())).thenReturn(null);

        boolean success = new GaugeExecutionProducer().setupConfigurationFromContext(configuration, context, null);

        assertFalse("should setup configuration if data context is not present in configuration context. Expected: false, Actual: true", success);
    }

    @Test
    public void shouldNotSetupConfigurationIfElementIsNotPresentInConfigurationContext() {
        GaugeRunConfiguration configuration = mock(GaugeRunConfiguration.class);
        ConfigurationContext context = mock(ConfigurationContext.class);
        DataContext dataContext = mock(DataContext.class);

        when(context.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY.getName())).thenReturn(new VirtualFile[]{mock(VirtualFile.class)});
        when(context.getPsiLocation()).thenReturn(null);
        when(context.getModule()).thenReturn(mock(Module.class));

        boolean success = new GaugeExecutionProducer().setupConfigurationFromContext(configuration, context, null);

        assertFalse("should setup configuration if module is not present in configuration context. Expected: false, Actual: true", success);
    }

    @Test
    public void shouldNotSetupConfigurationIfModuleIsNotPresentInConfigurationContext() {
        GaugeRunConfiguration configuration = mock(GaugeRunConfiguration.class);
        ConfigurationContext context = mock(ConfigurationContext.class);
        DataContext dataContext = mock(DataContext.class);

        when(context.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY.getName())).thenReturn(new VirtualFile[]{mock(VirtualFile.class)});
        when(context.getModule()).thenReturn(null);
        boolean success = new GaugeExecutionProducer().setupConfigurationFromContext(configuration, context, null);

        assertFalse("should setup configuration if module is not present in configuration context. Expected: false, Actual: true", success);
    }

    @Test
    public void shouldNotSetupConfigurationIfElementDoesntBelongToGaugeSpecFile() {
        GaugeRunConfiguration configuration = mock(GaugeRunConfiguration.class);
        ConfigurationContext context = mock(ConfigurationContext.class);
        DataContext dataContext = mock(DataContext.class);
        PsiElement element = mock(PsiElement.class);

        when(context.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY.getName())).thenReturn(new VirtualFile[]{mock(VirtualFile.class)});
        when(context.getPsiLocation()).thenReturn(element);
        when(element.getContainingFile()).thenReturn(mock(PsiFile.class));
        when(context.getModule()).thenReturn(mock(Module.class));

        boolean success = new GaugeExecutionProducer().setupConfigurationFromContext(configuration, context, null);

        assertFalse("should setup configuration if element doesn't belong to gauge spec file. Expected: false, Actual: true", success);
    }

    @Test
    public void shouldNotSetupConfigurationIfElementIsNotInSpecScope() {
        GaugeRunConfiguration configuration = mock(GaugeRunConfiguration.class);
        ConfigurationContext context = mock(ConfigurationContext.class);
        DataContext dataContext = mock(DataContext.class);
        PsiElement element = mock(SpecScenario.class);

        when(context.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY.getName())).thenReturn(new VirtualFile[]{mock(VirtualFile.class)});
        when(context.getPsiLocation()).thenReturn(element);
        when(element.getContainingFile()).thenReturn(mock(SpecFile.class));
        when(context.getModule()).thenReturn(mock(Module.class));

        boolean success = new GaugeExecutionProducer().setupConfigurationFromContext(configuration, context, null);

        assertFalse("should setup configuration if element is not in spec scope. Expected: false, Actual: true", success);
    }

    @Test
    public void shouldNotSetupConfigurationIfVirtualFileIsNotPresent() {
        GaugeRunConfiguration configuration = mock(GaugeRunConfiguration.class);
        ConfigurationContext context = mock(ConfigurationContext.class);
        DataContext dataContext = mock(DataContext.class);
        PsiElement element = mock(SpecFile.class);
        PsiFile file = mock(SpecFile.class);

        when(file.getVirtualFile()).thenReturn(null);
        when(context.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY.getName())).thenReturn(new VirtualFile[]{mock(VirtualFile.class)});
        when(context.getPsiLocation()).thenReturn(element);
        when(element.getContainingFile()).thenReturn(file);
        when(context.getModule()).thenReturn(mock(Module.class));

        boolean success = new GaugeExecutionProducer().setupConfigurationFromContext(configuration, context, null);

        assertFalse("should setup configuration if element doesn't contain virtual file. Expected: false, Actual: true", success);
    }
}