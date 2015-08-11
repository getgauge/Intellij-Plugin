package com.thoughtworks.gauge.highlight;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.thoughtworks.gauge.util.GaugeUtil;

public class ErrorHighLighter implements Condition<VirtualFile> {

    @Override
    public boolean value(VirtualFile virtualFile) {
        return GaugeUtil.isGaugeFile(virtualFile);
    }
}
