package com.thoughtworks.gauge.markdownPreview;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.thoughtworks.gauge.helper.ModuleHelper;
import com.thoughtworks.gauge.language.SpecFileType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MarkdownPreviewEditorProviderTest {

    private Module module;
    private ModuleHelper helper;
    private VirtualFile file;
    private Project project;

    @Before
    public void setUp() throws Exception {
        project = mock(Project.class);
        file = mock(VirtualFile.class);
        helper = mock(ModuleHelper.class);
        module = mock(Module.class);
    }

    @Test
    public void shouldAcceptOnlyGaugeFileAndModule() {
        when(helper.getModule(file, project)).thenReturn(module);
        when(helper.isGaugeModule(module)).thenReturn(true);
        when(file.getFileType()).thenReturn(SpecFileType.INSTANCE);

        MarkdownPreviewEditorProvider provider = new MarkdownPreviewEditorProvider(helper);

        assertTrue("Should accept gauge file and module. Expected: true, Actual: false", provider.accept(project, file));
    }

    @Test
    public void shouldNotAcceptNonGaugeFile() {
        when(helper.getModule(file, project)).thenReturn(module);
        when(helper.isGaugeModule(module)).thenReturn(true);
        when(file.getFileType()).thenReturn(JavaFileType.INSTANCE);

        MarkdownPreviewEditorProvider provider = new MarkdownPreviewEditorProvider(helper);

        assertFalse("Should accept non gauge file. Expected: false, Actual: true", provider.accept(project, file));
        verify(helper, never()).isGaugeModule(module);
    }

    @Test
    public void shouldNotAcceptNonGaugeModule() {
        when(helper.getModule(file, project)).thenReturn(module);
        when(helper.isGaugeModule(module)).thenReturn(false);

        MarkdownPreviewEditorProvider provider = new MarkdownPreviewEditorProvider(helper);

        assertFalse("Should accept non gauge module. Expected: false, Actual: true", provider.accept(project, file));
    }

    @Test
    public void shouldNotAcceptIfModuleIsNull() {
        when(helper.getModule(file, project)).thenReturn(null);

        MarkdownPreviewEditorProvider provider = new MarkdownPreviewEditorProvider(helper);

        assertFalse("Should accept null module. Expected: false, Actual: true", provider.accept(project, file));
        verify(helper, never()).isGaugeModule(any(Module.class));
    }
}