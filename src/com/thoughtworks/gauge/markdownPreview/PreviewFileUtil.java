package com.thoughtworks.gauge.markdownPreview;

import com.intellij.ide.browsers.OpenInBrowserRequest;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;

class PreviewFileUtil {
    private static File tempDirectory;

    private static File createOrGetTempDirectory(String projectName) throws IOException {
        if (tempDirectory == null) {
            tempDirectory = FileUtil.createTempDirectory(projectName, null, true);
        }
        return tempDirectory;
    }

    static File createHtmlFile(OpenInBrowserRequest request, VirtualFile virtualFile, String projectName) throws IOException {
        File fileToPreview = new File(virtualFile.getPath());
        String relativeDirPath = FileUtil.getRelativePath(new File(request.getProject().getBaseDir().getPath()), new File(fileToPreview.getParent()));
        String htmlFilePath = FileUtil.join(relativeDirPath, virtualFile.getNameWithoutExtension()) + ".html";
        File htmlFile = new File(FileUtil.join(createOrGetTempDirectory(projectName).getPath(), htmlFilePath));
        FileUtil.createIfDoesntExist(htmlFile);
        return htmlFile;
    }

    static File getCssFile(String projectName) throws IOException {
        File previewFile = new File(FileUtil.join(createOrGetTempDirectory(projectName).getPath(), "preview.css"));
        if (!previewFile.exists() && FileUtil.createIfDoesntExist(previewFile)) {
            String cssContent = IOUtils.toString(PreviewFileUtil.class.getResourceAsStream("/preview.css"), "UTF-8");
            FileUtil.writeToFile(previewFile, cssContent);
        }
        return previewFile;
    }
}
