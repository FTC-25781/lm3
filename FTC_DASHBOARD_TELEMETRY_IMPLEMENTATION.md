# FTC Dashboard Enhanced Telemetry Implementation Plan

## Overview

This document provides a comprehensive plan for implementing enhanced telemetry packet transmission from the FTC robot code to the Redux-based enhanced FTC Dashboard. The implementation will enable real-time subsystem monitoring, advanced visualization, and comprehensive telemetry capabilities.

## Architecture Overview

```
┌─────────────────────────┐       WebSocket       ┌─────────────────────────┐
│   FTC Robot Code        │ ──────────────────►   │  Enhanced Dashboard     │
│   (This Repository)     │    JSON Messages      │  (ftc-dashboard repo)   │
│                         │                       │                         │
│  - Subsystems           │                       │  - Redux Store          │
│  - Telemetry Manager    │                       │  - React Components     │
│  - WebSocket Client     │                       │  - WebSocket Server     │
└─────────────────────────┘                       └─────────────────────────┘
```

## Message Protocol

The enhanced dashboard expects two primary message types:

### 1. Subsystem Update Message
```json
{
  "type": "SUBSYSTEM_UPDATE",
  "subsystem": "drivetrain|intake|deposit|camera|general",
  "data": {
    // Subsystem-specific data
  },
  "timestamp": 1234567890
}
```

### 2. Enhanced Telemetry Message
```json
{
  "type": "TELEMETRY_UPDATE",
  "section": "drivetrain|intake|deposit|claw|general",
  "values": [
    {
      "key": "variableName",
      "value": 123.45,
      "unit": "mm"
    }
  ],
  "timestamp": 1234567890
}
```

## Implementation Steps

### Step 1: Create Enhanced Telemetry Infrastructure

#### 1.1 Create Enhanced Dashboard Manager
Create `/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/dashboard/EnhancedDashboard.java`:

```java
package org.firstinspires.ftc.teamcode.dashboard;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class EnhancedDashboard {
    private static EnhancedDashboard instance;
    private final FtcDashboard dashboard;
    
    private EnhancedDashboard() {
        this.dashboard = FtcDashboard.getInstance();
    }
    
    public static EnhancedDashboard getInstance() {
        if (instance == null) {
            instance = new EnhancedDashboard();
        }
        return instance;
    }
    
    public void sendSubsystemUpdate(String subsystem, Map<String, Object> data) {
        try {
            JSONObject message = new JSONObject();
            message.put("type", "SUBSYSTEM_UPDATE");
            message.put("subsystem", subsystem);
            message.put("timestamp", System.currentTimeMillis());
            message.put("data", new JSONObject(data));
            
            sendWebSocketMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    public void sendTelemetryUpdate(String section, Map<String, TelemetryValue> values) {
        try {
            JSONObject message = new JSONObject();
            message.put("type", "TELEMETRY_UPDATE");
            message.put("section", section);
            message.put("timestamp", System.currentTimeMillis());
            
            JSONArray valuesArray = new JSONArray();
            for (Map.Entry<String, TelemetryValue> entry : values.entrySet()) {
                JSONObject valueObj = new JSONObject();
                valueObj.put("key", entry.getKey());
                valueObj.put("value", entry.getValue().value);
                if (entry.getValue().unit != null) {
                    valueObj.put("unit", entry.getValue().unit);
                }
                valuesArray.put(valueObj);
            }
            message.put("values", valuesArray);
            
            sendWebSocketMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    private void sendWebSocketMessage(JSONObject message) {
        // The FTC Dashboard doesn't directly expose WebSocket sending,
        // so we'll use a custom telemetry packet approach
        TelemetryPacket packet = new TelemetryPacket();
        packet.put("__enhanced_dashboard__", message.toString());
        dashboard.sendTelemetryPacket(packet);
    }
    
    public static class TelemetryValue {
        public final Object value;
        public final String unit;
        
        public TelemetryValue(Object value, String unit) {
            this.value = value;
            this.unit = unit;
        }
        
        public TelemetryValue(Object value) {
            this(value, null);
        }
    }
}
```

#### 1.2 Create Subsystem Telemetry Interfaces
Create `/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/dashboard/SubsystemTelemetry.java`:

```java
package org.firstinspires.ftc.teamcode.dashboard;

import java.util.Map;

public interface SubsystemTelemetry {
    Map<String, Object> getSubsystemData();
    Map<String, EnhancedDashboard.TelemetryValue> getTelemetryValues();
    String getSubsystemName();
}
```

### Step 2: Enhance Existing Subsystems

#### 2.1 Update Drivetrain Telemetry
Modify the `Follower` class to implement enhanced telemetry:

```java
// Add to Follower class
private EnhancedDashboard enhancedDashboard = EnhancedDashboard.getInstance();

public void sendEnhancedTelemetry() {
    Map<String, Object> drivetrainData = new HashMap<>();
    
    // Position data
    Map<String, Double> position = new HashMap<>();
    position.put("x", poseUpdater.getPose().getX());
    position.put("y", poseUpdater.getPose().getY());
    position.put("z", poseUpdater.getPose().getHeading());
    drivetrainData.put("position", position);
    
    // Velocity data
    Map<String, Double> velocity = new HashMap<>();
    velocity.put("x", poseUpdater.getVelocity().getXComponent());
    velocity.put("y", poseUpdater.getVelocity().getYComponent());
    velocity.put("z", poseUpdater.getHeadingVelocity());
    drivetrainData.put("velocity", velocity);
    
    // Encoder values
    Map<String, Integer> encoders = new HashMap<>();
    encoders.put("leftFront", leftFront.getCurrentPosition());
    encoders.put("leftBack", leftBack.getCurrentPosition());
    encoders.put("rightFront", rightFront.getCurrentPosition());
    encoders.put("rightBack", rightBack.getCurrentPosition());
    drivetrainData.put("encoders", encoders);
    
    // Motor currents
    Map<String, Double> currents = new HashMap<>();
    currents.put("leftFront", leftFront.getCurrent(CurrentUnit.AMPS));
    currents.put("leftBack", leftBack.getCurrent(CurrentUnit.AMPS));
    currents.put("rightFront", rightFront.getCurrent(CurrentUnit.AMPS));
    currents.put("rightBack", rightBack.getCurrent(CurrentUnit.AMPS));
    drivetrainData.put("currents", currents);
    
    // Send subsystem update
    enhancedDashboard.sendSubsystemUpdate("drivetrain", drivetrainData);
    
    // Send telemetry values
    Map<String, EnhancedDashboard.TelemetryValue> telemetryValues = new HashMap<>();
    telemetryValues.put("x_position", new EnhancedDashboard.TelemetryValue(
        poseUpdater.getPose().getX(), "inches"));
    telemetryValues.put("y_position", new EnhancedDashboard.TelemetryValue(
        poseUpdater.getPose().getY(), "inches"));
    telemetryValues.put("heading", new EnhancedDashboard.TelemetryValue(
        Math.toDegrees(poseUpdater.getPose().getHeading()), "degrees"));
    telemetryValues.put("velocity", new EnhancedDashboard.TelemetryValue(
        poseUpdater.getTotalVelocity(), "in/s"));
    
    enhancedDashboard.sendTelemetryUpdate("drivetrain", telemetryValues);
}
```

#### 2.2 Update Intake Subsystem
Add to `IntakeSlideSubsystem`:

```java
private EnhancedDashboard enhancedDashboard = EnhancedDashboard.getInstance();

public void sendEnhancedTelemetry() {
    Map<String, Object> intakeData = new HashMap<>();
    
    // Slide position
    intakeData.put("slidePosition", sensorDistance.getDistance(DistanceUnit.CM));
    intakeData.put("targetPosition", targetPosition);
    intakeData.put("state", CURRENT_STATE.name());
    intakeData.put("motorPower", slideMotor.getPower());
    intakeData.put("limitSwitch", !intakeLimitSwitch.getState());
    
    enhancedDashboard.sendSubsystemUpdate("intake", intakeData);
    
    // Telemetry values
    Map<String, EnhancedDashboard.TelemetryValue> telemetryValues = new HashMap<>();
    telemetryValues.put("slide_position", new EnhancedDashboard.TelemetryValue(
        sensorDistance.getDistance(DistanceUnit.CM), "cm"));
    telemetryValues.put("motor_current", new EnhancedDashboard.TelemetryValue(
        slideMotor.getCurrent(CurrentUnit.AMPS), "A"));
    telemetryValues.put("state", new EnhancedDashboard.TelemetryValue(
        CURRENT_STATE.name()));
    
    enhancedDashboard.sendTelemetryUpdate("intake", telemetryValues);
}
```

Add similar implementations for:
- `IntakeV4BSubsystem` (servo positions)
- `IntakeClawSubsystem` (claw state, sample detection)

#### 2.3 Update Deposit Subsystem
Add similar telemetry methods to:
- `DepositSlideSubsystem`
- `DepositV4BSubsystem`
- `DepositClawSubsystem`

### Step 3: Create Central Telemetry Manager

Create `/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/dashboard/TelemetryManager.java`:

```java
package org.firstinspires.ftc.teamcode.dashboard;

import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TelemetryManager {
    private static TelemetryManager instance;
    private final List<SubsystemTelemetry> subsystems = new ArrayList<>();
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
    
    public void registerSubsystem(SubsystemTelemetry subsystem) {
        subsystems.add(subsystem);
    }
    
    public void update() {
        double currentTime = timer.seconds();
        if (currentTime - lastUpdateTime >= 1.0 / UPDATE_RATE_HZ) {
            lastUpdateTime = currentTime;
            
            // Send general dashboard update
            sendGeneralUpdate();
            
            // Update all registered subsystems
            for (SubsystemTelemetry subsystem : subsystems) {
                dashboard.sendSubsystemUpdate(
                    subsystem.getSubsystemName(), 
                    subsystem.getSubsystemData()
                );
                dashboard.sendTelemetryUpdate(
                    subsystem.getSubsystemName(),
                    subsystem.getTelemetryValues()
                );
            }
        }
    }
    
    private void sendGeneralUpdate() {
        Map<String, Object> generalData = new HashMap<>();
        generalData.put("opModeActive", true);
        generalData.put("runtime", timer.seconds());
        generalData.put("updateRate", UPDATE_RATE_HZ);
        
        dashboard.sendSubsystemUpdate("general", generalData);
    }
}
```

### Step 4: Integrate into OpModes

#### 4.1 Update TeleOpEnhancements
Modify `TeleOpEnhancements.java`:

```java
// Add to class members
private TelemetryManager telemetryManager;

// Add to init()
telemetryManager = TelemetryManager.getInstance();

// Register subsystems if they implement SubsystemTelemetry
// Or manually send telemetry in loop()

// Add to loop()
@Override
public void loop() {
    // Existing code...
    
    // Send enhanced telemetry
    follower.sendEnhancedTelemetry();
    intakeSlide.sendEnhancedTelemetry();
    intakeV4B.sendEnhancedTelemetry();
    intakeClaw.sendEnhancedTelemetry();
    depositSlide.sendEnhancedTelemetry();
    depositV4B.sendEnhancedTelemetry();
    depositClaw.sendEnhancedTelemetry();
    
    // Or use centralized manager
    telemetryManager.update();
}
```

### Step 5: Dashboard WebSocket Handler Modification

Since FTC Dashboard doesn't directly expose WebSocket message sending, we need to modify the dashboard's WebSocket handler to parse our custom messages.

Create a custom fork of FTC Dashboard or add a middleware layer that:
1. Intercepts telemetry packets with `__enhanced_dashboard__` key
2. Parses the JSON message
3. Forwards it through the WebSocket to the React dashboard

Alternatively, implement a custom WebSocket server in the robot code that the enhanced dashboard can connect to directly.

### Step 6: Camera Integration (Optional)

For camera telemetry:

```java
public class CameraTelemetry {
    public void sendCameraUpdate(List<DetectedObject> objects) {
        Map<String, Object> cameraData = new HashMap<>();
        
        List<Map<String, Object>> detectedObjects = new ArrayList<>();
        for (DetectedObject obj : objects) {
            Map<String, Object> objData = new HashMap<>();
            objData.put("x", obj.x);
            objData.put("y", obj.y);
            objData.put("width", obj.width);
            objData.put("height", obj.height);
            objData.put("confidence", obj.confidence);
            objData.put("label", obj.label);
            objData.put("color", obj.color);
            detectedObjects.add(objData);
        }
        
        cameraData.put("detectedObjects", detectedObjects);
        cameraData.put("frameRate", getCurrentFPS());
        cameraData.put("processingTime", getProcessingTime());
        
        EnhancedDashboard.getInstance().sendSubsystemUpdate("camera", cameraData);
    }
}
```

## Testing Strategy

### 1. Unit Tests
- Test JSON message generation
- Test telemetry value formatting
- Test update rate limiting

### 2. Integration Tests
- Verify WebSocket connection
- Test message delivery to dashboard
- Validate Redux store updates

### 3. System Tests
- Full robot-to-dashboard communication
- Performance testing at competition load
- Network reliability testing

## Performance Considerations

1. **Update Rate**: Limit updates to 10Hz to prevent network congestion
2. **Message Size**: Keep messages under 64KB
3. **Buffering**: Implement message queuing for reliability
4. **Compression**: Consider gzip for large messages

## Implementation Timeline

### Phase 1: Core Infrastructure (Week 1)
- [ ] Create EnhancedDashboard class
- [ ] Implement message protocol
- [ ] Set up basic WebSocket communication

### Phase 2: Subsystem Integration (Week 2)
- [ ] Add telemetry to drivetrain
- [ ] Add telemetry to intake subsystem
- [ ] Add telemetry to deposit subsystem

### Phase 3: Testing & Optimization (Week 3)
- [ ] Performance testing
- [ ] Bug fixes
- [ ] Documentation

### Phase 4: Advanced Features (Week 4)
- [ ] Camera integration
- [ ] Recording/replay support
- [ ] Custom alerts

## Troubleshooting Guide

### Common Issues

1. **Messages not reaching dashboard**
   - Check WebSocket connection
   - Verify message format
   - Check network configuration

2. **Performance issues**
   - Reduce update frequency
   - Optimize message size
   - Check for memory leaks

3. **Data synchronization**
   - Ensure timestamp accuracy
   - Handle clock drift
   - Implement message ordering

## Alternative Implementation Approaches

### Option 1: Direct WebSocket Server
Instead of using FTC Dashboard's WebSocket, implement a separate WebSocket server:

```java
public class EnhancedWebSocketServer {
    private WebSocketServer server;
    
    public EnhancedWebSocketServer(int port) {
        server = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onMessage(WebSocket conn, String message) {
                // Handle incoming messages
            }
        };
    }
    
    public void broadcast(String message) {
        server.broadcast(message);
    }
}
```

### Option 2: REST API Approach
Implement a REST API for telemetry updates:

```java
public class TelemetryAPI {
    private final OkHttpClient client = new OkHttpClient();
    private final String baseUrl = "http://192.168.43.1:3000/api";
    
    public void postTelemetry(String endpoint, String json) {
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
            .url(baseUrl + endpoint)
            .post(body)
            .build();
        
        client.newCall(request).enqueue(new Callback() {
            // Handle response
        });
    }
}
```

## Conclusion

This implementation plan provides a comprehensive approach to integrating enhanced telemetry from the FTC robot code to the Redux-based dashboard. The modular design allows for incremental implementation and easy debugging. Follow the phases sequentially for best results.

## Additional Resources

- [FTC Dashboard Documentation](https://acmerobotics.github.io/ftc-dashboard)
- [WebSocket Protocol RFC](https://datatracker.ietf.org/doc/html/rfc6455)
- [Redux Documentation](https://redux.js.org/)
- [React Documentation](https://reactjs.org/)