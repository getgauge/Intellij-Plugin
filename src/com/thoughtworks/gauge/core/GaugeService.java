// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

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
