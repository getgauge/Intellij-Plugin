package com.thoughtworks.gauge.markdownPreview;

import com.intellij.ide.browsers.OpenInBrowserRequest;
import com.intellij.ide.browsers.WebBrowserUrlProvider;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Url;
import com.intellij.util.UrlImpl;
import com.thoughtworks.gauge.language.ConceptFileType;
import com.thoughtworks.gauge.language.SpecFileType;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import static com.intellij.openapi.vcs.VcsNotifier.STANDARD_NOTIFICATION;
import static com.thoughtworks.gauge.util.GaugeUtil.getGaugeSettings;

public class GaugeWebBrowserPreview extends WebBrowserUrlProvider {
    private static File tempDirectory;


    private static File createOrGetTempDirectory(String projectName) throws IOException {
        if (tempDirectory == null) {
            tempDirectory = FileUtil.createTempDirectory(projectName, null, true);
        }
        return tempDirectory;
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
            GaugeSettingsModel settings = getGaugeSettings();
            Spectacle spectacle = new Spectacle(request.getProject(), settings);
            if (spectacle.isInstalled()) {
                return previewUrl(request, virtualFile, settings);
            } else {
                spectacle.notifyToInstall();
            }
        } catch (Exception e) {
            Messages.showWarningDialog(String.format("Unable to create html file for %s", virtualFile.getName()), "Error");
        }
        return null;
    }

    @Nullable
    private Url previewUrl(OpenInBrowserRequest request, VirtualFile virtualFile, GaugeSettingsModel settings) throws IOException, InterruptedException {
        ProcessBuilder docsProcessBuilder = new ProcessBuilder(settings.getGaugePath(), "docs", "spectacle", "specs/" + virtualFile.getName());
        String projectName = request.getProject().getName();
        docsProcessBuilder.environment().put("spectacle_out_dir", createOrGetTempDirectory(projectName).getPath() + "/docs");
        GaugeUtil.setGaugeEnvironmentsTo(docsProcessBuilder, settings);
        docsProcessBuilder.directory(new File(request.getProject().getBasePath()));
        Process docsProcess = docsProcessBuilder.start();
        int docsExitCode = docsProcess.waitFor();
        if (docsExitCode != 0) {
            String docsOutput = String.format("<pre>%s</pre>", GaugeUtil.getOutput(docsProcess.getInputStream(), " ").replace("<", "&lt;").replace(">", "&gt;"));
            Notifications.Bus.notify(new Notification("Specification Preview", "Error: Specification Preview", docsOutput, NotificationType.ERROR));
            return null;
        }
        return new UrlImpl(FileUtil.join(createOrGetTempDirectory(projectName).getPath(), "docs/html/specs/" + virtualFile.getNameWithoutExtension() + ".html"));
    }
}
