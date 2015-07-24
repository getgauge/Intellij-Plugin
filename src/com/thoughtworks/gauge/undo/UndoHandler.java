package com.thoughtworks.gauge.undo;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.openapi.vfs.LocalFileSystem.getInstance;

public class UndoHandler {
    private final List<String> fileNames;
    private final Project project;
    private final String name;

    public UndoHandler(List<String> fileNames, Project project, String name) {
        this.fileNames = fileNames;
        this.project = project;
        this.name = name;
    }

    public void handle() {
        refreshFiles();
        runWriteAction();
    }

    private void refreshFiles() {
        final Map<Document, String> documentTextMap = new HashMap<Document, String>();
        for (String fileName : fileNames) {
            VirtualFile fileByIoFile = LocalFileSystem.getInstance().findFileByIoFile(new File(fileName));
            if (fileByIoFile != null) {
                Document document = FileDocumentManager.getInstance().getDocument(fileByIoFile);
                if (document != null) documentTextMap.put(document, document.getText());
            }
        }
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(false);
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                for (Document document : documentTextMap.keySet())
                    document.setText(documentTextMap.get(document));
            }
        });
    }


    private void runWriteAction() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                    @Override
                    public void run() {
                        performUndoableAction(fileNames);
                    }
                }, name, name);
            }
        });
    }

    private void performUndoableAction(List<String> filesChangedList) {
        for (String fileName : filesChangedList)
            try {
                VirtualFile virtualFile = getInstance().findFileByIoFile(new File(fileName));
                if (virtualFile != null) {
                    Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
                    getInstance().refreshAndFindFileByIoFile(new File(fileName));
                    if (document != null) document.setText(FileUtils.readFileToString(new File(fileName)));
                }
            } catch (Exception ignored) {
            }
    }

}
