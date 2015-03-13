package com.thoughtworks.gauge.extract;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.ide.actions.CreateFileAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringActionHandler;
import com.thoughtworks.gauge.GaugeConnection;
import com.thoughtworks.gauge.annotator.FileManager;
import com.thoughtworks.gauge.annotator.GaugeDataContext;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import gauge.messages.Api;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class ExtractConceptHandler implements RefactoringActionHandler {
    @Override
    public void invoke(@NotNull final Project project, final Editor editor, final PsiFile psiFile, DataContext dataContext) {
        final String selectedText = editor.getCaretModel().getAllCarets().get(0).getSelectedText();
        final ExtractConceptResponse response = getResponse(psiFile, selectedText);
        if (!response.isValid) {
            HintManager.getInstance().showErrorHint(editor, response.error);
            return;
        }
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                List<PsiFile> conceptFiles = FileManager.getAllConceptFiles(project);
                conceptFiles.add(0, null);
                ListPopup stepImplChooser = JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<PsiFile>("Choose File", conceptFiles) {
                    @Override
                    public boolean isSpeedSearchEnabled() {
                        return true;
                    }

                    @Override
                    public PopupStep onChosen(final PsiFile selectedValue, boolean finalChoice) {
                        return doFinalStep(new Runnable() {
                            public void run() {
                                if (selectedValue == null) {
                                    ActionManager instance = ActionManager.getInstance();
                                    DataContext dataContext = DataManager.getInstance().getDataContext();
                                    GaugeDataContext gaugeDataContext = new GaugeDataContext(dataContext);
                                    AnActionEvent anActionEvent = new AnActionEvent(null, gaugeDataContext, ActionPlaces.UNKNOWN, new Presentation("Create File"), instance, 0);
                                    CreateFileAction createFileAction = new GaugeCreateFileAction("# " + response.conceptHeading + "\n" + response.steps, psiFile);
                                    createFileAction.actionPerformed(anActionEvent);
                                }
                                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                                            @Override
                                            public void run() {
                                                if (selectedValue != null) {
                                                    Document document = FileDocumentManager.getInstance().getDocument(selectedValue.getVirtualFile());
                                                    document.setText(selectedValue.getText().trim() + "\n\n" + "# " + response.conceptHeading + "\n" + response.steps);
                                                }
                                                EditorModificationUtil.deleteSelectedText(editor);
                                                EditorModificationUtil.insertStringAtCaret(editor, response.conceptText, true, false);
                                            }
                                        }, "Create Concept", "Create Concept");

                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public Icon getIconFor(PsiFile aValue) {
                        return aValue == null ? AllIcons.Actions.CreateFromUsage : aValue.getIcon(0);
                    }

                    @NotNull
                    @Override
                    public String getTextFor(PsiFile value) {
                        return value == null ? "Create new file" : value.getVirtualFile().getPath().replace(project.getBasePath(), "");
                    }
                });
                stepImplChooser.showCenteredInCurrentWindow(project);
            }
        });
    }

    private ExtractConceptResponse getResponse(PsiElement psiElement, String text) {
        final Module module = ModuleUtil.findModuleForPsiElement(psiElement);
        GaugeService gaugeService = Gauge.getGaugeService(module);
        GaugeConnection gaugeConnection = gaugeService.getGaugeConnection();
        try {
            Api.ExtractConceptInfoResponse response = gaugeConnection.sendGetExtractConceptInfoRequest(text);
            if (!response.getIsValid())
                return new ExtractConceptResponse("Not Valid Selection");
            Pair<String, Boolean> pairs = Messages.showInputDialogWithCheckBox("Extract Concept", "Refactor", "refactor other usages",
                    false, true, null, response.getConceptHeading(), new ExtractConceptInputValidator(response.getConceptHeading()));
            if (pairs.getFirst() == null) return new ExtractConceptResponse("Process Cancelled");
            if (!pairs.getFirst().equals(response.getConceptHeading())) {
                Api.FormatConceptHeadingResponse response1 = gaugeConnection.sendGetFormatConceptHeadingRequest(pairs.getFirst(), response.getConceptHeading(), response.getConceptText());
                return new ExtractConceptResponse(pairs.getFirst(), response.getIsValid(), response.getSteps(), response1.getNewConceptText());
            } else
                return new ExtractConceptResponse(pairs.getFirst(), response.getIsValid(), response.getSteps(), response.getConceptText());
        } catch (Exception ignored) {
        }
        return new ExtractConceptResponse("Not Valid Selection");
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {
    }

    private class ExtractConceptResponse {
        public final String conceptHeading;
        public final boolean isValid;
        public final String steps;
        public final String conceptText;
        public String error;

        public ExtractConceptResponse(String conceptHeading, boolean isValid, String steps, String conceptText) {
            this.conceptHeading = conceptHeading;
            this.isValid = isValid;
            this.steps = steps;
            this.conceptText = conceptText;
        }

        public ExtractConceptResponse(String error) {
            this.conceptHeading = "";
            this.isValid = false;
            this.steps = "";
            this.conceptText = "";
            this.error = error;
        }
    }
}
