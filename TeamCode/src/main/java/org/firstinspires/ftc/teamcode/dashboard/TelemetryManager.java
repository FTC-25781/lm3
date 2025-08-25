package org.firstinspires.ftc.teamcode.dashboard;

import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.dashboard.messages.*;

public class TelemetryManager {
    private static TelemetryManager instance;
    private final ElapsedTime timer = new ElapsedTime();
    private final double UPDATE_RATE_HZ = 10.0; // 10Hz update rate
    private double lastUpdateTime = 0;
    private final EnhancedDashboard dashboard = EnhancedDashboard.getInstance();
    
    private TelemetryManager() {}
    
    public static TelemetryManager getInstance() {
        if (instance == null) {
            instance = new TelemetryManager();
        }
        return instance;
    }
    
    public void update() {
        double currentTime = timer.seconds();
        if (currentTime - lastUpdateTime >= 1.0 / UPDATE_RATE_HZ) {
            lastUpdateTime = currentTime;
            
            // Send general dashboard update
            sendGeneralUpdate();
        }
    }
    
    private void sendGeneralUpdate() {
        GeneralData generalData = new GeneralData();
        generalData.setOpModeActive(true);
        generalData.setRuntime(timer.seconds());
        generalData.setUpdateRate(UPDATE_RATE_HZ);
        // Battery voltage would need to be obtained from hardware
        generalData.setBatteryVoltage(12.0); 
        
        dashboard.sendSubsystemUpdate(
            new SubsystemUpdateMessage("general", generalData)
        );
    }
}