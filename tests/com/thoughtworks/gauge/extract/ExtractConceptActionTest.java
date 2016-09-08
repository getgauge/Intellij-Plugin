package com.thoughtworks.gauge.extract;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.thoughtworks.gauge.helper.ModuleHelper;
import com.thoughtworks.gauge.language.SpecFileType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExtractConceptActionTest {
    @Mock
    private DataContext dataContext;
    private Project project;
    private AnActionEvent event;
    private Presentation presentation;
    private ModuleHelper helper;
    private VirtualFile vFile;

    @Before
    public void setUp() throws Exception {
        dataContext = mock(DataContext.class);
        vFile = mock(VirtualFile.class);
        project = mock(Project.class);
        event = mock(AnActionEvent.class);
        presentation = new Presentation();
        helper = mock(ModuleHelper.class);
    }

    @Test
    public void shouldShowExtractToConceptAction() {
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE.getName())).thenReturn(vFile);
        when(dataContext.getData(CommonDataKeys.PROJECT.getName())).thenReturn(project);
        when(vFile.getFileType()).thenReturn(SpecFileType.INSTANCE);
        when(event.getPresentation()).thenReturn(presentation);
        when(event.getDataContext()).thenReturn(dataContext);
        when(helper.isGaugeModule(vFile, project)).thenReturn(true);

        new ExtractConceptAction(helper).update(event);

        assertTrue(presentation.isEnabled());
    }

    @Test
    public void shouldHideExtractToConceptActionWhenNotGaugeModule() {
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE.getName())).thenReturn(vFile);
        when(dataContext.getData(CommonDataKeys.PROJECT.getName())).thenReturn(project);
        when(event.getDataContext()).thenReturn(dataContext);
        when(event.getPresentation()).thenReturn(presentation);
        when(helper.isGaugeModule(vFile, project)).thenReturn(false);

        new ExtractConceptAction(helper).update(event);

        assertFalse(presentation.isEnabled());
    }

    @Test
    public void shouldHideExtractToConceptActionWhenProjectIsNotPresent() {
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE.getName())).thenReturn(vFile);
        when(event.getDataContext()).thenReturn(dataContext);
        when(event.getPresentation()).thenReturn(presentation);

        new ExtractConceptAction(helper).update(event);

        assertFalse(presentation.isEnabled());
    }

    @Test
    public void shouldHideExtractToConceptActionWhenFileIsNotPresent() {
        when(event.getPresentation()).thenReturn(presentation);
        when(event.getDataContext()).thenReturn(dataContext);
        when(helper.isGaugeModule(vFile, project)).thenReturn(true);

        new ExtractConceptAction(helper).update(event);

        assertFalse(presentation.isEnabled());
    }

    @Test
    public void shouldHideExtractToConceptActionWhenNotGaugeFile() {
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE.getName())).thenReturn(vFile);
        when(dataContext.getData(CommonDataKeys.PROJECT.getName())).thenReturn(project);
        when(vFile.getFileType()).thenReturn(JavaFileType.INSTANCE);
        when(event.getPresentation()).thenReturn(presentation);
        when(event.getDataContext()).thenReturn(dataContext);
        when(helper.isGaugeModule(vFile, project)).thenReturn(true);

        new ExtractConceptAction(helper).update(event);

        assertFalse(presentation.isEnabled());
    }
}