package com.thoughtworks.gauge.annotator;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.language.ConceptFileType;
import com.thoughtworks.gauge.language.SpecFileType;

import java.io.File;
import java.util.*;

import static com.intellij.psi.search.GlobalSearchScope.moduleScope;

public class FileManager {
    public static List<PsiFile> getAllJavaFiles(Module module) {
        Collection<VirtualFile> javaVirtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, JavaFileType.INSTANCE, moduleScope(module));
        List<PsiFile> javaFiles = new ArrayList<PsiFile>();

        for (VirtualFile javaVFile : javaVirtualFiles) {
            PsiFile file = PsiManager.getInstance(module.getProject()).findFile(javaVFile);
            if (file != null) {
                javaFiles.add(file);
            }
        }
        Collections.sort(javaFiles, new Comparator<PsiFile>() {
            @Override
            public int compare(PsiFile o1, PsiFile o2) {
                return FileManager.getJavaFileName(o1).compareToIgnoreCase(FileManager.getJavaFileName(o2));
            }
        });
        return javaFiles;
    }

    public static List<PsiFile> getAllConceptFiles(Project project) {
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, ConceptFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        List<PsiFile> files = new ArrayList<PsiFile>();

        for (VirtualFile ConceptVFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(ConceptVFile);
            if (file != null) {
                files.add(file);
            }
        }
        Collections.sort(files, new Comparator<PsiFile>() {
            @Override
            public int compare(PsiFile o1, PsiFile o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return files;
    }

    public static String getJavaFileName(PsiFile value) {
        PsiJavaFile javaFile = (PsiJavaFile) value;
        if (!javaFile.getPackageName().equals("")) {
            return javaFile.getPackageName() + "." + javaFile.getName();
        }
        return javaFile.getName();
    }

    public static List<String> getDirNamesUnderSpecs(Project project) {
        File dir = new File(project.getBasePath() + File.separator + Constants.SPECS_DIR);
        File[] files = dir.listFiles();
        List<String> dirs = new ArrayList<String>();
        dirs.add(String.format("%s%s%s", File.separator, Constants.SPECS_DIR, File.separator));
        if (files != null)  getDirectories(files, dirs, project.getBasePath());
        return dirs;
    }

    private static void getDirectories(File[] files, List<String> dirs, String basePath) {
        for (File file : files)
            if (file.isDirectory()) {
                dirs.add(file.getPath().replace(basePath, "") + File.separator);
                getDirectories(file.listFiles(), dirs, basePath);
            }
    }

    public static List<VirtualFile> getAllSpecFiles(Project project) {
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SpecFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        return new ArrayList<VirtualFile>(virtualFiles);
    }

    public static List<VirtualFile> getConceptFiles(Project project) {
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, ConceptFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        return new ArrayList<VirtualFile>(virtualFiles);
    }
}
