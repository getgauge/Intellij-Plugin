package com.thoughtworks.gauge.highlight;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.thoughtworks.gauge.util.GaugeUtil;

/**
 * The highlighter condition that determines if a Gauge file should be displayed differently due to errors within (e.g.
 * syntax errors). In most themes, this will result in the Gauge file with errors should be red.
 */
public class ErrorHighLighter implements Condition<VirtualFile> {

    @Override
    public boolean value(VirtualFile virtualFile) {
        return GaugeUtil.isGaugeFile(virtualFile);
    }
}
