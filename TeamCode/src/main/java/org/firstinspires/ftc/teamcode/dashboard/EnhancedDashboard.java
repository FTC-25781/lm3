package org.firstinspires.ftc.teamcode.dashboard;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.firstinspires.ftc.teamcode.dashboard.messages.*;

public class EnhancedDashboard {
    private static EnhancedDashboard instance;
    private final FtcDashboard dashboard;
    private final ObjectMapper objectMapper;
    
    private EnhancedDashboard() {
        this.dashboard = FtcDashboard.getInstance();
        this.objectMapper = new ObjectMapper();
    }
    
    public static EnhancedDashboard getInstance() {
        if (instance == null) {
            instance = new EnhancedDashboard();
        }
        return instance;
    }
    
    public void sendSubsystemUpdate(SubsystemUpdateMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            sendWebSocketMessage(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    
    public void sendTelemetryUpdate(TelemetryUpdateMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            sendWebSocketMessage(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    
    private void sendWebSocketMessage(String jsonMessage) {
        // The FTC Dashboard doesn't directly expose WebSocket sending,
        // so we'll use a custom telemetry packet approach
        TelemetryPacket packet = new TelemetryPacket();
        packet.put("__enhanced_dashboard__", jsonMessage);
        dashboard.sendTelemetryPacket(packet);
    }
}