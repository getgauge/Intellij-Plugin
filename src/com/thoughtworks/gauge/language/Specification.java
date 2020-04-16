/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.language;

import com.intellij.lang.Language;

public class Specification extends Language {
    public static final Specification INSTANCE = new Specification();

    private Specification() {
        super("Specification");
    }
}
