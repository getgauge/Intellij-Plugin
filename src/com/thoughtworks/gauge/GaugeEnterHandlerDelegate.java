/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.util.GaugeUtil;

public class GaugeEnterHandlerDelegate implements EnterHandlerDelegate {

    @Override
    public Result preprocessEnter(PsiFile psiFile, Editor editor, Ref<Integer> ref, Ref<Integer> ref1, DataContext dataContext, EditorActionHandler editorActionHandler) {
        return Result.Continue;
    }

    @Override
    public Result postProcessEnter(PsiFile psiFile, Editor editor, DataContext dataContext) {
        Document document = editor.getDocument();
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if (GaugeUtil.isGaugeFile(file)) {
            FileDocumentManager.getInstance().saveDocumentAsIs(document);
        }
        return null;
    }
}
