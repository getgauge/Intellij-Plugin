/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.core;

import com.thoughtworks.gauge.connection.GaugeConnection;

public class GaugeService {
    private final Process gaugeProcess;
    private final GaugeConnection gaugeConnection;

    public GaugeService(Process gaugeProcess, GaugeConnection gaugeConnection) {
        this.gaugeProcess = gaugeProcess;
        this.gaugeConnection = gaugeConnection;
    }

    public GaugeConnection getGaugeConnection() {
        return gaugeConnection;
    }

    public Process getGaugeProcess() {
        return gaugeProcess;
    }
}
