package com.thoughtworks.gauge;

import com.intellij.codeInsight.editorActions.EnterHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;

public class GaugeEnterHandler extends EnterHandler {
    public GaugeEnterHandler(EditorActionHandler originalHandler) {
        super(originalHandler);
    }

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
        super.executeWriteAction(editor, caret, dataContext);
        FileDocumentManager.getInstance().saveDocumentAsIs(editor.getDocument());
    }
}
