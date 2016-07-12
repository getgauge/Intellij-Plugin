package com.thoughtworks.gauge.execution;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.language.SpecFile;
import com.thoughtworks.gauge.language.psi.SpecScenario;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class GaugeExecutionProducerTest {

    private VirtualFile virtualFile;
    private SpecFile file;
    private PsiElement element;
    private Module module;
    private DataContext dataContext;
    private ConfigurationContext context;
    private GaugeRunConfiguration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = mock(GaugeRunConfiguration.class);
        context = mock(ConfigurationContext.class);
        dataContext = mock(DataContext.class);
        module = mock(Module.class);
        element = mock(SpecFile.class);
        file = mock(SpecFile.class);
        virtualFile = mock(VirtualFile.class);
    }

    @Test
    public void shouldSetupConfigurationFromContext() {
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
        when(context.getDataContext()).thenReturn(dataContext);

        boolean success = new GaugeExecutionProducer().setupConfigurationFromContext(configuration, context, null);

        assertFalse("should setup configuration if data context is not present in configuration context. Expected: false, Actual: true", success);
        verify(context, never()).getModule();
    }

    @Test
    public void shouldNotSetupConfigurationIfElementIsNotPresentInConfigurationContext() {
        when(context.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY.getName())).thenReturn(new VirtualFile[]{mock(VirtualFile.class)});
        when(context.getModule()).thenReturn(mock(Module.class));

        boolean success = new GaugeExecutionProducer().setupConfigurationFromContext(configuration, context, null);

        assertFalse("should setup configuration if module is not present in configuration context. Expected: false, Actual: true", success);
    }

    @Test
    public void shouldNotSetupConfigurationIfModuleIsNotPresentInConfigurationContext() {
        when(context.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY.getName())).thenReturn(new VirtualFile[]{mock(VirtualFile.class)});

        boolean success = new GaugeExecutionProducer().setupConfigurationFromContext(configuration, context, null);

        assertFalse("should setup configuration if module is not present in configuration context. Expected: false, Actual: true", success);
    }

    @Test
    public void shouldNotSetupConfigurationIfElementDoesntBelongToGaugeSpecFile() {
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