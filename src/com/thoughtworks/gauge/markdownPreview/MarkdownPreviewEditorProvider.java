package com.thoughtworks.gauge.markdownPreview;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.PossiblyDumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class MarkdownPreviewEditorProvider implements FileEditorProvider, PossiblyDumbAware{
    @Override
    public boolean accept(Project project, VirtualFile file) {
        Module module = ModuleUtil.findModuleForFile(file, project);
        return module != null && GaugeUtil.isGaugeFile(file) && GaugeUtil.isGaugeModule(module);
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new MarkdownPreviewEditor(project, FileDocumentManager.getInstance().getDocument(file));
    }

    @Override
    public void disposeEditor(@NotNull FileEditor fileEditor) {
        fileEditor.dispose();
    }

    @NotNull
    @Override
    public FileEditorState readState(@NotNull Element element, @NotNull Project project, @NotNull VirtualFile virtualFile) {
        return FileEditorState.INSTANCE;
    }

    @Override
    public void writeState(@NotNull FileEditorState fileEditorState, @NotNull Project project, @NotNull Element element) {

    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "MarkdownPreview";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}
