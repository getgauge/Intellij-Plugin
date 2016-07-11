package com.thoughtworks.gauge.rename;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.language.SpecFileType;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomRenameHandlerTest {

    @Test
    public void testShouldRenameGaugeElement() throws Exception {
        DataContext dataContext = mock(DataContext.class);
        VirtualFile virtualFile = mock(VirtualFile.class);
        PsiElement element = mock(SpecStepImpl.class);
        Editor editor = mock(Editor.class);
        Project project = mock(Project.class);
        when(virtualFile.getFileType()).thenReturn(SpecFileType.INSTANCE);
        when(dataContext.getData(CommonDataKeys.PSI_ELEMENT.getName())).thenReturn(element);
        when(dataContext.getData(CommonDataKeys.VIRTUAL_FILE.getName())).thenReturn(virtualFile);
        when(dataContext.getData(CommonDataKeys.EDITOR.getName())).thenReturn(editor);
        when(dataContext.getData(CommonDataKeys.PROJECT.getName())).thenReturn(project);

        CompileAction compileAction = mock(CompileAction.class);
        assertTrue("Should rename the spec step. Expected: true, Actual: false",new CustomRenameHandler(compileAction).isAvailableOnDataContext(dataContext));
    }

}