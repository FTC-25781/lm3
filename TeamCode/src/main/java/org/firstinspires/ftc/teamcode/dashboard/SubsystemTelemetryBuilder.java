package org.firstinspires.ftc.teamcode.dashboard;

import org.firstinspires.ftc.teamcode.dashboard.messages.*;
import java.util.ArrayList;
import java.util.List;

public class SubsystemTelemetryBuilder {
    private final String subsystem;
    private final List<TelemetryUpdateMessage.TelemetryValue> values;
    
    public SubsystemTelemetryBuilder(String subsystem) {
        this.subsystem = subsystem;
        this.values = new ArrayList<>();
    }
    
    public SubsystemTelemetryBuilder addValue(String key, Object value) {
        values.add(new TelemetryUpdateMessage.TelemetryValue(key, value));
        return this;
    }
    
    public SubsystemTelemetryBuilder addValue(String key, Object value, String unit) {
        values.add(new TelemetryUpdateMessage.TelemetryValue(key, value, unit));
        return this;
    }
    
    public TelemetryUpdateMessage build() {
        return new TelemetryUpdateMessage(subsystem, values);
    }
}