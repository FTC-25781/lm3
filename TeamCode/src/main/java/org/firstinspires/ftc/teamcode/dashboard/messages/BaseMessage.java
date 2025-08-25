package org.firstinspires.ftc.teamcode.dashboard.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SubsystemUpdateMessage.class, name = "SUBSYSTEM_UPDATE"),
    @JsonSubTypes.Type(value = TelemetryUpdateMessage.class, name = "TELEMETRY_UPDATE")
})
public abstract class BaseMessage {
    private long timestamp;
    
    public BaseMessage() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public long getTimestamp() { 
        return timestamp; 
    }
    
    public void setTimestamp(long timestamp) { 
        this.timestamp = timestamp; 
    }
}