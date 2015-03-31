package com.thoughtworks.gauge.extract;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.annotator.FileManager;
import com.thoughtworks.gauge.language.psi.ConceptTable;
import com.thoughtworks.gauge.language.psi.SpecTable;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExtractConceptInfoCollector {
    public static final String CREATE_NEW_FILE = "Create New File(Enter info in the below field)";
    private final Editor editor;
    private final Map<String, String> tableMap;
    private final List<PsiElement> specSteps;
    private boolean isCancelled = false;

    public ExtractConceptInfoCollector(Editor editor, Map<String, String> tableMap, List<PsiElement> specSteps) {
        this.editor = editor;
        this.tableMap = tableMap;
        this.specSteps = specSteps;
    }

    public ExtractConceptInfo getAllInfo() {
        this.isCancelled = false;
        String steps = getFormattedSteps();
        List<String> args = getArgs(steps);
        final ExtractConceptDialog form = new ExtractConceptDialog(this.editor.getProject(), args, FileManager.getDirNamesUnderSpecs(editor.getProject()));
        showDialog(steps, form);
        if (form.getInfo().conceptName.equals("") || this.isCancelled)
            return new ExtractConceptInfo("", "", false);
        String conceptName = form.getInfo().conceptName;
        String fileName = editor.getProject().getBasePath() + form.getInfo().fileName;
        boolean shouldContinue = conceptName != null;
        return new ExtractConceptInfo(conceptName, fileName, shouldContinue);
    }

    private List<String> getArgs(String steps) {
        List<String> args = new ArrayList<String>();
        matchPatterns(steps, args, "<(.*?)>", ">", "<");
        matchPatterns(steps, args, "\"(.*?)\"", "\"", "\"");
        return args;
    }

    private void matchPatterns(String steps, List<String> args, String regex, String suffix, String prefix) {
        Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(steps);
        while (matcher.find()) {
            args.add(prefix + matcher.group(1) + suffix);
        }
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
        names.add(CREATE_NEW_FILE);
        for (PsiFile file : files)
            names.add(file.getVirtualFile().getPath().replace(editor.getProject().getBasePath(), ""));
        return names;
    }

    private String getFormattedSteps() {
        StringBuilder builder = new StringBuilder();
        for (PsiElement step : specSteps)
            builder = step.getClass().equals(SpecStepImpl.class) ? formatStep(builder, (SpecStepImpl) step) : formatStep(builder, (ConceptStepImpl) step);
        return builder.toString();
    }

    private StringBuilder formatStep(StringBuilder builder, SpecStepImpl step) {
        SpecTable table = step.getInlineTable();
        if (table != null) {
            builder.append(step.getText().trim().replace(table.getText().trim(), "").trim())
                    .append(" <").append(tableMap.get(table.getText().trim())).append(">").append("\n");
            return builder;
        }
        return builder.append(step.getText().trim()).append("\n");
    }

    private StringBuilder formatStep(StringBuilder builder, ConceptStepImpl step) {
        ConceptTable table = step.getTable();
        if (table != null) {
            builder.append(step.getText().trim().replace(table.getText().trim(), "").trim())
                    .append(" <").append(tableMap.get(table.getText().trim())).append(">").append("\n");
            return builder;
        }
        return builder.append(step.getText().trim()).append("\n");
    }
}