package com.thoughtworks.gauge.annotator;

import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInsight.template.TemplateBuilder;
import com.intellij.codeInsight.template.TemplateBuilderFactory;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.thoughtworks.gauge.StepParam;
import com.thoughtworks.gauge.StepParamType;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.language.psi.SpecStep;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreateStepImplFix extends BaseIntentionAction {
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
        return true;
    }

    @Override
    public void invoke(@NotNull final Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(JavaFileType.INSTANCE);
                descriptor.setRoots(project.getBaseDir());
                descriptor.setHideIgnored(true);

                final VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
                if (file != null) {
                    createStepImpl(project, file);
                }
            }
        });
    }

    private void createStepImpl(final Project project, final VirtualFile file) {
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
                final StringBuilder text = new StringBuilder(String.format("@Step(\"%s\")\n", stepValue.getStepAnnotation()));
                text.append(String.format("public void %s(%s){\n\n", stepValue.getMethodName(), getParamList(stepValue.getParams())));
                text.append("}\n");
                final PsiMethod stepMethod = JavaPsiFacade.getElementFactory(project).createMethodFromText(text.toString(), psiClass);
                PsiMethod addedElement = (PsiMethod) psiClass.add(stepMethod);
                JavaCodeStyleManager.getInstance(project).shortenClassReferences(addedElement);
                CodeStyleManager.getInstance(project).reformat(psiClass);
                addedElement = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(addedElement);
                return addedElement;
            }

            private String getParamList(List<StepParam> params) {
                StringBuilder paramlistBuilder = new StringBuilder();
                for (int i = 0; i < params.size(); i++) {
                    StepParam param = params.get(i);
                    if (param.getType() == StepParamType.string) {
                        paramlistBuilder.append("String arg").append(i);
                    } else {
                        paramlistBuilder.append("Table arg").append(i);
                    }
                    if (i != params.size() - 1) {
                        paramlistBuilder.append(", ");
                    }

                }
                return paramlistBuilder.toString();
            }

            private void templateParams(PsiMethod addedElement, TemplateBuilder builder) {
                PsiParameterList paramsList = addedElement.getParameterList();
                PsiParameter[] parameters = paramsList.getParameters();
                for (PsiParameter parameter : parameters) {
                    PsiElement nameIdentifier = parameter.getNameIdentifier();
                    if (nameIdentifier != null) {
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

}
