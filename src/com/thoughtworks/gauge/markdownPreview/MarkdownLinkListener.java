package com.thoughtworks.gauge.markdownPreview;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.ex.http.HttpFileSystem;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.BrowserHyperlinkListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


public class MarkdownLinkListener implements HyperlinkListener {
    private final BrowserHyperlinkListener browserLinkListener = new BrowserHyperlinkListener();

    private final JEditorPane editor;
    private final Project project;
    private final Document document;
    public MarkdownLinkListener(@NotNull JEditorPane editor, @NotNull Project project, @NotNull Document document) {
        this.editor = editor;
        this.project = project;
        this.document = document;
    }
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
            URL target = e.getURL();
            if (target == null) {
                if (e.getDescription().startsWith("#")) {
                    editor.scrollToReference(e.getDescription().substring(1));
                    return;
                }
                try {
                    target = new File(e.getDescription()).toURI().toURL();
                } catch (MalformedURLException e1) {
                    return;
                }
            }
            if (VirtualFileManager.getInstance().getFileSystem(target.getProtocol()) instanceof HttpFileSystem) {
                browserLinkListener.hyperlinkUpdate(e);
            } else {
                VirtualFile virtualTarget = VirtualFileManager.getInstance().getFileSystem(target.getProtocol()).findFileByPath(target.getFile());;
                if (virtualTarget == null || !virtualTarget.exists())
                    virtualTarget = FileDocumentManager.getInstance().getFile(document).getParent().findFileByRelativePath(e.getDescription());
                try {
                    if (virtualTarget == null)
                        virtualTarget = resolveClassReference(project, e.getDescription());
                } catch (NoClassDefFoundError silent) {
                    return;
                }
                if (virtualTarget != null)
                    FileEditorManager.getInstance(project).openFile(virtualTarget, true);
            }
        }
    }
    public static VirtualFile resolveClassReference(@NotNull Project project, @NotNull String target) {
        final PsiClass classpathResource = JavaPsiFacade.getInstance(project).findClass(target, GlobalSearchScope.projectScope(project));
        if (classpathResource != null)
            return classpathResource.getContainingFile().getVirtualFile();
        return null;
    }
}

