package com.thoughtworks.gauge.markdownPreview;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pegdown.PegDownProcessor;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.beans.PropertyChangeListener;

public class MarkdownPreviewEditor extends UserDataHolderBase implements FileEditor {
    private Document document;
    private boolean previewIsObsolete = true;
    protected final JEditorPane jEditorPane = new JEditorPane();
    private final JScrollPane scrollPane = new JBScrollPane(jEditorPane);
    private PegDownProcessor processor;

    private PegDownProcessor initProcessor() {
        MarkdownExtensions markdownExtensions = new MarkdownExtensions();
        return new PegDownProcessor(markdownExtensions.getExtensionsValue(), markdownExtensions.getParsingTimeout());
    }

    public MarkdownPreviewEditor(@NotNull Project project, @NotNull Document document) {
        this.document = document;
        this.processor = initProcessor();

        this.document.addDocumentListener(new DocumentAdapter() {
            @Override
            public void documentChanged(DocumentEvent e) {
                previewIsObsolete = true;
            }
        });

        final HTMLEditorKit kit = new HTMLPreviewEditor(document);

        StyleSheet styleSheet = new StyleSheet();
        styleSheet.importStyleSheet(MarkdownPreviewEditor.class.getResource("/preview.css"));
        kit.setStyleSheet(styleSheet);

        jEditorPane.setEditorKit(kit);
        jEditorPane.setEditable(false);
        jEditorPane.setText(document.getText());
        jEditorPane.getCaret().setMagicCaretPosition(new Point(0, 0));
        ((DefaultCaret) jEditorPane.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        jEditorPane.addHyperlinkListener(new MarkdownLinkListener(jEditorPane, project, document));
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return scrollPane;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return scrollPane;
    }

    @NotNull
    @Override
    public String getName() {
        return "HTML Preview";
    }

    @NotNull
    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel fileEditorStateLevel) {
        return FileEditorState.INSTANCE;
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return document.getText() != null;
    }

    @Override
    public void selectNotify() {
        if (previewIsObsolete) {
            try {
                jEditorPane.setText(String.format("<div id=\"markdown-preview\">%s</div>",
                        processor.markdownToHtml(Formatter.format(document.getText()))));
                previewIsObsolete = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deselectNotify() {
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Nullable
    @Override
    public StructureViewBuilder getStructureViewBuilder() {
        return null;
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

}
