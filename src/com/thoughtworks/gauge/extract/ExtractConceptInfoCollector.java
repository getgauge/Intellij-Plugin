package com.thoughtworks.gauge.extract;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.annotator.FileManager;
import com.thoughtworks.gauge.language.psi.ConceptTable;
import com.thoughtworks.gauge.language.psi.SpecPsiImplUtil;
import com.thoughtworks.gauge.language.psi.SpecTable;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ExtractConceptInfoCollector {
    public static final String CREATE_NEW_FILE = "Create New File(Enter info in the below field)";
    private final Editor editor;
    private final Map<String, String> tableMap;
    private final List<PsiElement> steps;
    private Project project;

    public ExtractConceptInfoCollector(Editor editor, Map<String, String> tableMap, List<PsiElement> steps, Project project) {
        this.editor = editor;
        this.tableMap = tableMap;
        this.steps = steps;
        this.project = project;
    }

    public ExtractConceptInfo getAllInfo() {
        String steps = getFormattedSteps();
        List<String> args = getArgs(steps);
        final ExtractConceptDialog form = new ExtractConceptDialog(this.editor.getProject(), args, FileManager.getDirNamesUnderSpecs(project));
        showDialog(steps, form);
        if (form.getInfo().cancelled)
            return form.getInfo();
        return new ExtractConceptInfo(form.getInfo().conceptName, project.getBasePath() + form.getInfo().fileName, form.getInfo().cancelled);
    }

    private List<String> getArgs(String steps) {
        List<String> args = new ArrayList<>();
        for (String step : steps.split("\n")) {
            String unescapeStep = StringUtil.unescapeStringCharacters(step);
            for (String p : SpecPsiImplUtil.getStepValueFor(this.steps.get(0), step, false).getParameters())
                args.add(getNameWithParamChar(unescapeStep, p));
        }
        return args;
    }

    private String getNameWithParamChar(String step, String p) {
        String arg = StringUtil.escapeStringCharacters(p);
        return step.charAt(step.indexOf(p) - 1) + arg + step.charAt(step.indexOf(p) + p.length());
    }

    private void showDialog(String steps, ExtractConceptDialog form) {
        final DialogBuilder builder = new DialogBuilder(editor.getProject());
        form.setData(steps, getConceptFileNames(), builder);
        builder.setCenterPanel(form.getRootPane());
        builder.setTitle("Extract Concept");
        builder.removeAllActions();
        builder.show();
    }

    private List<String> getConceptFileNames() {
        List<PsiFile> files = FileManager.getAllConceptFiles(editor.getProject());
        List<String> names = new ArrayList<>();
        names.add(CREATE_NEW_FILE);
        files.forEach((file) -> names.add(file.getVirtualFile().getPath().replace(project.getBasePath(), "")));
        return names;
    }

    private String getFormattedSteps() {
        StringBuilder builder = new StringBuilder();
        for (PsiElement step : steps)
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