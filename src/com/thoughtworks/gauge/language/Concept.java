/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.language;

import com.intellij.lang.Language;

public class Concept extends Language {
    public static final Concept INSTANCE = new Concept();

    private Concept() {
        super("Concept");
    }
}
