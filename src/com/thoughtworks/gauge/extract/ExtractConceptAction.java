package com.thoughtworks.gauge.extract;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.helper.ModuleHelper;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;

public class ExtractConceptAction extends AnAction {

    private ModuleHelper moduleHelper;

    public ExtractConceptAction() {
        this.moduleHelper = new ModuleHelper();
    }

    ExtractConceptAction(ModuleHelper moduleHelper) {
        this.moduleHelper = moduleHelper;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        DataContext dataContext = anActionEvent.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        PsiFile file = CommonDataKeys.PSI_FILE.getData(dataContext);
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        if (file == null || project == null || editor == null) {
            Messages.showErrorDialog("Cannot find project details, rejecting extract to concept request.", "Extract To Concept");
            return;
        }
        new ExtractConceptHandler().invoke(project, editor, file);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        Boolean enable = true;
        if (file == null || project == null || !moduleHelper.isGaugeModule(file, project) || !GaugeUtil.isGaugeFile(file))
            enable = false;
        e.getPresentation().setEnabled(enable);
    }
}
    