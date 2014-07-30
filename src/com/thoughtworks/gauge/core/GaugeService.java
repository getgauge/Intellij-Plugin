package com.thoughtworks.gauge.core;

import com.thoughtworks.gauge.GaugeConnection;

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
