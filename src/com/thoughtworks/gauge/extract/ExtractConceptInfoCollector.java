package com.thoughtworks.gauge.extract;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.annotator.FileManager;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ExtractConceptInfoCollector {
    private final Editor editor;
    private final Map<String, String> tableMap;
    private final List<SpecStepImpl> specSteps;
    private boolean isCancelled = false;

    public ExtractConceptInfoCollector(Editor editor, Map<String, String> tableMap, List<SpecStepImpl> specSteps) {
        this.editor = editor;
        this.tableMap = tableMap;
        this.specSteps = specSteps;
    }

    public ExtractConceptInfo getAllInfo() {
        this.isCancelled = false;
        String steps = getFormattedSteps();
        final ExtractConceptDialog form = new ExtractConceptDialog();
        showDialog(steps, form);
        if (form.getInfo().conceptName.equals("") || this.isCancelled)
            return new ExtractConceptInfo("", "", false);
        String conceptName = form.getInfo().conceptName;
        String fileName = editor.getProject().getBasePath() + form.getInfo().fileName;
        boolean shouldContinue = conceptName != null;
        return new ExtractConceptInfo(conceptName, fileName, shouldContinue);
    }

    private void showDialog(String steps, ExtractConceptDialog form) {
        form.setData(steps, getConceptNames());
        final DialogBuilder builder = new DialogBuilder(editor.getProject());
        builder.setCenterPanel(form.getRootPane());
        builder.setTitle("Extract Concept");
        builder.removeAllActions();
        builder.addCancelAction();
        builder.addOkAction();
        final ExtractConceptInfoCollector self = this;
        builder.setCancelOperation(new Runnable() {
            @Override
            public void run() {
                builder.getWindow().setVisible(false);
                self.isCancelled = true;
            }
        });
        builder.show();
    }

    private List<String> getConceptNames() {
        List<PsiFile> files = FileManager.getAllConceptFiles(editor.getProject());
        List<String> names = new ArrayList<String>();
        for (PsiFile file : files) {
            names.add(file.getVirtualFile().getPath().replace(editor.getProject().getBasePath(), ""));
        }
        return names;
    }

    private String getFormattedSteps() {
        StringBuilder builder = new StringBuilder();
        for (SpecStepImpl step : specSteps) {
            if (step.getInlineTable() != null) {
                builder.append(step.getText().trim().replace(step.getInlineTable().getText().trim(), "").trim())
                        .append(" ").append("<" + tableMap.get(step.getInlineTable().getText().trim()) + ">").append("\n");
                continue;
            }
            builder.append(step.getText().trim()).append("\n");
        }
        return builder.toString();
    }
}

class ExtractConceptInfo {
    public final String conceptName;
    public final String fileName;
    public final Boolean shouldContinue;

    public ExtractConceptInfo(String conceptName, String fileName, Boolean shouldContinue) {
        this.conceptName = conceptName;
        this.fileName = fileName;
        this.shouldContinue = shouldContinue;
    }
}