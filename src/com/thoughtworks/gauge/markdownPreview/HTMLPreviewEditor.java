package com.thoughtworks.gauge.markdownPreview;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


public class HTMLPreviewEditor extends HTMLEditorKit {

    private final Document document;

    public HTMLPreviewEditor(@NotNull Document document) {
        this.document = document;
    }

    @Override
    public Object clone() {
        return new HTMLPreviewEditor(document);
    }

    @Override
    public ViewFactory getViewFactory() {
        return new MarkdownViewFactory(document);
    }

    private static class MarkdownViewFactory extends HTMLFactory {

        private final Document document;

        private MarkdownViewFactory(Document document) {
            this.document = document;
        }

        @Override
        public View create(Element elem) {
            if (HTML.Tag.IMG.equals(elem.getAttributes().getAttribute(StyleConstants.NameAttribute)))
                return new MarkdownImageView(document, elem);
            return super.create(elem);
        }
    }

    private static class MarkdownImageView extends ImageView {

        private final Document document;

        private MarkdownImageView(@NotNull Document document, @NotNull Element elem) {
            super(elem);
            this.document = document;
        }

        @Override
        public URL getImageURL() {
            final String src = (String) getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
            final VirtualFile localImage = resolveRelativePath(document, src);
            try {
                if (localImage != null && localImage.exists())
                    return new File(localImage.getPath()).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            return super.getImageURL();
        }

        public VirtualFile resolveRelativePath(@NotNull Document document, @NotNull String target) {
            return FileDocumentManager.getInstance().getFile(document).getParent().findFileByRelativePath(target);
        }
    }
}