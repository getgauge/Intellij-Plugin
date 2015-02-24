// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

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
        return null;
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
