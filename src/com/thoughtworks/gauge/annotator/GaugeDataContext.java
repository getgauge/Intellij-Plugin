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

package com.thoughtworks.gauge.annotator;

import com.intellij.ide.IdeView;
import com.intellij.ide.util.DirectoryChooserUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GaugeDataContext implements DataContext {
    private final DataContext dataContext;

    public GaugeDataContext(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    @Nullable
    @Override
    public Object getData(@NonNls String dataId) {
        if (dataId.equals("IDEView")) {
            return new MyIdeView(LangDataKeys.PROJECT.getData(dataContext));
        } else {
            return dataContext.getData(dataId);
        }

    }

    private static class MyIdeView implements IdeView {
        private final Project project;

        private MyIdeView(Project project) {
            this.project = project;
        }

        @Override
        public void selectElement(PsiElement element) {

        }

        @Override
        public PsiDirectory[] getDirectories() {
            List<PsiDirectory> psiDirectories = new ArrayList<>();
            PsiManager psiManager = PsiManager.getInstance(project);
            for (VirtualFile root : ProjectRootManager.getInstance(psiManager.getProject()).getContentSourceRoots()) {
                PsiDirectory directory = psiManager.findDirectory(root);
                if (directory != null) {
                    psiDirectories.add(directory);
                }
            }
            return psiDirectories.toArray(new PsiDirectory[]{});
        }

        @Nullable
        @Override
        public PsiDirectory getOrChooseDirectory() {
            return DirectoryChooserUtil.getOrChooseDirectory(this);
        }
    }
}
