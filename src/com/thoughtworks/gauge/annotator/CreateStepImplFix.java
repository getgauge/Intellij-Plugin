// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

package com.thoughtworks.gauge.annotator;

import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInsight.template.TemplateBuilder;
import com.intellij.codeInsight.template.TemplateBuilderFactory;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class CreateStepImplFix extends BaseIntentionAction {
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.annotator.CreateStepImplFix");
    private static final PsiFile NEW_FILE_HOLDER = null;
    public static final String IMPLEMENTATION = "implementation";
    private final SpecStep step;

    public CreateStepImplFix(SpecStep step) {
        this.step = step;
    }

    @NotNull
    @Override
    public String getText() {
        return "Create step implementation";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Step Implementation";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        Module module = GaugeUtil.moduleForPsiElement(step);
        return module != null && GaugeUtil.isGaugeFile(file.getVirtualFile()) && GaugeUtil.isGaugeModule(module);
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                List<PsiFile> javaFiles = Gauge.getSubModules(GaugeUtil.moduleForPsiElement(file)).stream()
                        .map(FileManager::getAllJavaFiles)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
                javaFiles.add(0, NEW_FILE_HOLDER);
                ListPopup stepImplChooser = JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<PsiFile>("Choose implementation class", javaFiles) {
                    @Override
                    public boolean isSpeedSearchEnabled() {
                        return true;
                    }

                    @Override
                    public PopupStep onChosen(final PsiFile selectedValue, boolean finalChoice) {
                        return doFinalStep(() -> {
                            if (selectedValue == NEW_FILE_HOLDER) {
                                createFileAndAddImpl(editor);
                            } else {
                                addImpl(project, selectedValue.getVirtualFile());
                            }
                        });
                    }

                    @Override
                    public Icon getIconFor(PsiFile aValue) {
                        return aValue == null ? AllIcons.Actions.IntentionBulb : aValue.getIcon(0);
                    }

                    @NotNull
                    @Override
                    public String getTextFor(PsiFile value) {
                        return value == null ? "Create new file" : getJavaFileName(value);
                    }
                });
                stepImplChooser.showCenteredInCurrentWindow(step.getProject());
            }

            private String getJavaFileName(PsiFile value) {
                PsiJavaFile javaFile = (PsiJavaFile) value;
                if (!javaFile.getPackageName().equals("")) {
                    return javaFile.getPackageName() + "." + javaFile.getName();
                }
                return javaFile.getName();
            }
        });
    }

    private void createFileAndAddImpl(Editor editor) {
        ActionManager instance = ActionManager.getInstance();
        DataContext dataContext = DataManager.getInstance().getDataContext(editor.getComponent());
        GaugeDataContext gaugeDataContext = new GaugeDataContext(dataContext);
        AnActionEvent anActionEvent = new AnActionEvent(null, gaugeDataContext, ActionPlaces.UNKNOWN, new Presentation("Create Class"), instance, 0);

        GaugeCreateClassAction createClassAction = new GaugeCreateClassAction();
        createClassAction.actionPerformed(anActionEvent);
        VirtualFile createdFile = createClassAction.getCreatedFile();
        if (createdFile != null) {
            addImpl(LangDataKeys.PROJECT.getData(dataContext), createdFile);
        }
    }

    private void addImpl(final Project project, final VirtualFile file) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                new WriteCommandAction.Simple(project) {
                    @Override
                    protected void run() throws Throwable {
                        PsiFile psifile = PsiManager.getInstance(project).findFile(file);
                        if (!FileModificationService.getInstance().prepareFileForWrite(psifile)) {
                            return;
                        }
                        PsiMethod addedStepImpl = addStepImplMethod(psifile);
                        final TemplateBuilder builder = TemplateBuilderFactory.getInstance().createTemplateBuilder(addedStepImpl);
                        templateMethodName(addedStepImpl, builder);
                        templateParams(addedStepImpl, builder);
                        templateBody(addedStepImpl, builder);
                        userTemplateModify(builder);
                    }
                }.execute();
            }

            private PsiMethod addStepImplMethod(PsiFile psifile) {

                final PsiClass psiClass = PsiTreeUtil.getChildOfType(psifile, PsiClass.class);
                PsiDocumentManager.getInstance(project).commitAllDocuments();

                StepValue stepValue = step.getStepValue();
                final StringBuilder text = new StringBuilder(String.format("@"+ Step.class.getName() +"(\"%s\")\n", stepValue.getStepAnnotationText()));
                text.append(String.format("public void %s(%s){\n\n", getMethodName(psiClass), getParamList(stepValue.getParameters())));
                text.append("}\n");
                final PsiMethod stepMethod = JavaPsiFacade.getElementFactory(project).createMethodFromText(text.toString(), psiClass);
                PsiMethod addedElement = (PsiMethod) psiClass.add(stepMethod);
                JavaCodeStyleManager.getInstance(project).shortenClassReferences(addedElement);
                CodeStyleManager.getInstance(project).reformat(psiClass);
                addedElement = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(addedElement);
                return addedElement;
            }

            private String getParamList(List<String> params) {
                StringBuilder paramlistBuilder = new StringBuilder();
                for (int i = 0; i < params.size(); i++) {
                    paramlistBuilder.append("Object arg").append(i);
                    if (i != params.size() - 1) {
                        paramlistBuilder.append(", ");
                    }
                }
                return paramlistBuilder.toString();
            }

            private void templateMethodName(PsiMethod addedStepImpl, TemplateBuilder builder) {
                PsiIdentifier methodName = addedStepImpl.getNameIdentifier();
                builder.replaceElement(methodName, methodName.getText());
            }

            private void templateParams(PsiMethod addedElement, TemplateBuilder builder) {
                PsiParameterList paramsList = addedElement.getParameterList();
                PsiParameter[] parameters = paramsList.getParameters();
                for (PsiParameter parameter : parameters) {
                    PsiElement nameIdentifier = parameter.getNameIdentifier();
                    PsiTypeElement typeElement = parameter.getTypeElement();
                    if (nameIdentifier != null) {
                        builder.replaceElement(typeElement, typeElement.getText());
                        builder.replaceElement(nameIdentifier, nameIdentifier.getText());
                    }
                }
            }

            private void templateBody(PsiMethod addedElement, TemplateBuilder builder) {
                final PsiCodeBlock body = addedElement.getBody();
                if (body != null) {
                    builder.replaceElement(body, new TextRange(2, 2), "");
                }
            }

            private void userTemplateModify(TemplateBuilder builder) {
                Editor editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, file, 0), true);
                final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
                if (editor != null) {
                    documentManager.doPostponedOperationsAndUnblockDocument(editor.getDocument());
                    builder.run(editor, false);
                }
            }
        });

    }

    @NotNull
    private String getMethodName(PsiClass psiClass) {
        try {
            for (Integer i = 1, length = psiClass.getAllMethods().length; i < length; i++) {
                String methodName = IMPLEMENTATION + i.toString();
                if (psiClass.findMethodsByName(methodName, true).length == 0)
                    return methodName;
            }
        } catch (Exception ignored) {
            LOG.debug(ignored);
        }
        return IMPLEMENTATION;
    }

}
