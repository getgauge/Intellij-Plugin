package com.thoughtworks.gauge;

import com.thoughtworks.gauge.core.GaugeConnection;

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
}
