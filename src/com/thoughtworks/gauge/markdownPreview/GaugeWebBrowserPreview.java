package com.thoughtworks.gauge.markdownPreview;

import com.intellij.ide.browsers.OpenInBrowserRequest;
import com.intellij.ide.browsers.WebBrowserUrlProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Url;
import com.intellij.util.UrlImpl;
import com.thoughtworks.gauge.language.ConceptFileType;
import com.thoughtworks.gauge.language.SpecFileType;
import org.jetbrains.annotations.Nullable;
import org.pegdown.PegDownProcessor;

import java.io.File;
import java.nio.file.Paths;

public class GaugeWebBrowserPreview extends WebBrowserUrlProvider {

    private PegDownProcessor initProcessor() {
        MarkdownExtensions markdownExtensions = new MarkdownExtensions();
        return new PegDownProcessor(markdownExtensions.getExtensionsValue(), markdownExtensions.getParsingTimeout());
    }

    @Override
    public boolean canHandleElement(OpenInBrowserRequest request) {
        FileType fileType = request.getFile().getFileType();
        return fileType instanceof SpecFileType
                || fileType instanceof ConceptFileType;
    }

    @Nullable
    @Override
    protected Url getUrl(OpenInBrowserRequest request, VirtualFile virtualFile) throws BrowserException {
        try {
            String markdownToHtml = String.format("<div id=\"markdown-preview\">%s</div>",
                    initProcessor().markdownToHtml(Formatter.format(request.getFile().getText())));
            File preview = PreviewFileUtil.getCssFile(request.getProject().getName());
            File htmlFile = PreviewFileUtil.createHtmlFile(request, virtualFile, request.getProject().getName());
            String styleSheetLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + Paths.get(preview.toURI()) + "\">";
            FileUtil.writeToFile(htmlFile, String.format("%s\n %s", styleSheetLink, markdownToHtml));
            return new UrlImpl(htmlFile.getPath());
        } catch (Exception e) {
            Messages.showWarningDialog(String.format("Unable to create html file for %s", virtualFile.getName()), "Error");
        }
        return null;
    }
}
