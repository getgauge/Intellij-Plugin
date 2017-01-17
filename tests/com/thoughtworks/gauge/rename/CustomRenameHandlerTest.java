package com.thoughtworks.gauge.rename;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.thoughtworks.gauge.language.SpecFileType;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomRenameHandlerTest {

    private Project project;
    private Editor editor;
    private VirtualFile virtualFile;
    private DataContext dataContext;

    @Before
    public void setUp() throws Exception {
        dataContext = mock(DataContext.class);
        virtualFile = mock(VirtualFile.class);
        editor = mock(Editor.class);
        project = mock(Project.class);
    }

    @Test
    public void testShouldRenameSpecStepElement() throws Exception {
        PsiElement element = mock(SpecStepImpl.class);

        when(virtualFile.getFileType()).thenReturn(SpecFileType.INSTANCE);
        when(dataContext.getData(CommonDataKeys.PSI_ELEMENT.getName())).thenReturn(element);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE.getName())).thenReturn(virtualFile);
        when(dataContext.getData(CommonDataKeys.EDITOR.getName())).thenReturn(editor);
        when(dataContext.getData(CommonDataKeys.PROJECT.getName())).thenReturn(project);

        assertTrue("Should rename the spec step. Expected: true, Actual: false", new CustomRenameHandler().isAvailableOnDataContext(dataContext));
    }

    @Test
    public void testShouldRenameConceptStepElement() throws Exception {
        PsiElement element = mock(ConceptStepImpl.class);

        when(virtualFile.getFileType()).thenReturn(SpecFileType.INSTANCE);
        when(dataContext.getData(CommonDataKeys.PSI_ELEMENT.getName())).thenReturn(element);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE.getName())).thenReturn(virtualFile);
        when(dataContext.getData(CommonDataKeys.EDITOR.getName())).thenReturn(editor);
        when(dataContext.getData(CommonDataKeys.PROJECT.getName())).thenReturn(project);

        assertTrue("Should rename the concept step. Expected: true, Actual: false", new CustomRenameHandler().isAvailableOnDataContext(dataContext));
    }

    @Test
    public void testShouldRenameImplementedMethodElement() throws Exception {
        PsiElement element = mock(PsiMethod.class);

        when(virtualFile.getFileType()).thenReturn(SpecFileType.INSTANCE);
        when(dataContext.getData(CommonDataKeys.PSI_ELEMENT.getName())).thenReturn(element);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE.getName())).thenReturn(virtualFile);
        when(dataContext.getData(CommonDataKeys.EDITOR.getName())).thenReturn(editor);
        when(dataContext.getData(CommonDataKeys.PROJECT.getName())).thenReturn(project);

        assertTrue("Should rename the implemented method. Expected: true, Actual: false", new CustomRenameHandler().isAvailableOnDataContext(dataContext));
    }

    @Test
    public void testShouldNotRenameNonGaugeElement() throws Exception {
        PsiElement element = mock(PsiElement.class);

        when(virtualFile.getFileType()).thenReturn(SpecFileType.INSTANCE);
        when(dataContext.getData(CommonDataKeys.PSI_ELEMENT.getName())).thenReturn(element);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE.getName())).thenReturn(virtualFile);
        when(dataContext.getData(CommonDataKeys.EDITOR.getName())).thenReturn(editor);
        when(dataContext.getData(CommonDataKeys.PROJECT.getName())).thenReturn(project);

        assertFalse("Should rename a non gauge element. Expected: false, Actual: true", new CustomRenameHandler().isAvailableOnDataContext(dataContext));
    }

    @Test
    public void testShouldNotRenameInNonGaugeFile() throws Exception {
        PsiElement element = mock(SpecStepImpl.class);

        when(virtualFile.getFileType()).thenReturn(JavaFileType.INSTANCE);
        when(dataContext.getData(CommonDataKeys.PSI_ELEMENT.getName())).thenReturn(element);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE.getName())).thenReturn(virtualFile);
        when(dataContext.getData(CommonDataKeys.EDITOR.getName())).thenReturn(editor);
        when(dataContext.getData(CommonDataKeys.PROJECT.getName())).thenReturn(project);

        assertFalse("Should rename in non gauge file. Expected: false, Actual: true", new CustomRenameHandler().isAvailableOnDataContext(dataContext));
    }

    @Test
    public void testShouldRenameWhenElementIsNotPresentInDataContext() throws Exception {
        CaretModel caretModel = mock(CaretModel.class);
        PsiFile file = mock(PsiFile.class);

        when(dataContext.getData(CommonDataKeys.PSI_ELEMENT.getName())).thenReturn(null);
        when(dataContext.getData(CommonDataKeys.EDITOR.getName())).thenReturn(editor);
        when(editor.getCaretModel()).thenReturn(caretModel);
        when(caretModel.getOffset()).thenReturn(0);
        when(dataContext.getData(CommonDataKeys.PSI_FILE.getName())).thenReturn(file);
        when(file.findElementAt(0)).thenReturn(mock(SpecStepImpl.class));

        boolean isAvailable = new CustomRenameHandler().isAvailableOnDataContext(dataContext);
        assertTrue("Should rename when element is not present in DataContext. Expected: true, Actual: false", isAvailable);

        when(file.findElementAt(0)).thenReturn(mock(ConceptStepImpl.class));
        isAvailable = new CustomRenameHandler().isAvailableOnDataContext(dataContext);
        assertTrue("Should rename when element is not present in DataContext. Expected: true, Actual: false", isAvailable);
    }

    @Test
    public void testShouldRenameWhenNoEditorInDataContext() throws Exception {
        when(dataContext.getData(CommonDataKeys.PSI_ELEMENT.getName())).thenReturn(null);
        when(dataContext.getData(CommonDataKeys.EDITOR.getName())).thenReturn(null);

        boolean isAvailable = new CustomRenameHandler().isAvailableOnDataContext(dataContext);
        assertFalse("Should rename when editor is not present. Expected: false, Actual: true", isAvailable);
    }

    @Test
    public void testShouldRenameWhenPsiFileIsNotPresentInDataContext() throws Exception {
        CaretModel caretModel = mock(CaretModel.class);

        when(dataContext.getData(CommonDataKeys.PSI_ELEMENT.getName())).thenReturn(null);
        when(dataContext.getData(CommonDataKeys.EDITOR.getName())).thenReturn(editor);
        when(editor.getCaretModel()).thenReturn(caretModel);
        when(caretModel.getOffset()).thenReturn(0);
        when(dataContext.getData(CommonDataKeys.PSI_FILE.getName())).thenReturn(null);

        boolean isAvailable = new CustomRenameHandler().isAvailableOnDataContext(dataContext);
        assertFalse("Should rename when psi file is not present. Expected: false, Actual: true", isAvailable);
    }
}