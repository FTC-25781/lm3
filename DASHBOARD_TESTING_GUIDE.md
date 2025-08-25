# FTC Enhanced Dashboard Testing Guide

## Prerequisites

1. **Enhanced Dashboard Web Interface**: Ensure your enhanced dashboard at `/Users/rameshmac2019/git/ftc-dashboard` is running and can receive telemetry packets with the `__enhanced_dashboard__` key.

2. **Robot Connection**: Connect to your robot via:
   - USB cable for direct connection
   - WiFi Direct for wireless connection
   - Control Hub's built-in WiFi

## Testing Steps

### 1. Start the Dashboard

```bash
cd /Users/rameshmac2019/git/ftc-dashboard
# Start your enhanced dashboard server (follow the dashboard's README)
```

Open the dashboard in your browser (usually http://192.168.43.1:8080/dash or http://192.168.49.1:8080/dash)

### 2. Deploy and Run the OpMode

1. Build and deploy the TeamCode to your robot
2. Select "Match TeleOp" from the OpMode list
3. Initialize and start the OpMode

### 3. Verify Telemetry Data

The enhanced telemetry system sends data through the `__enhanced_dashboard__` key. You should see:

#### A. General System Data (10Hz update rate)
- Runtime
- Battery voltage
- Update rate
- Loop time

#### B. Drivetrain Data
- Position (x, y)
- Velocity (x, y)
- Encoder values (LF, LB, RF, RB)
- Motor currents (LF, LB, RF, RB)
- Heading

#### C. Intake Subsystem Data
- Slide position
- Target position
- Current state
- Motor power
- Motor current
- Limit switch status
- Sample detected status
- Sensor distance

#### D. Deposit Subsystem Data
- Slide positions (motor 1 & 2)
- Target positions
- Current state
- Motor powers
- Motor currents
- Laser distance

### 4. Test Specific Features

#### Test 1: Drivetrain Telemetry
1. Move the robot using gamepad1 joysticks
2. Verify position updates in dashboard
3. Check motor current readings change with movement
4. Confirm heading updates when rotating

#### Test 2: Intake Telemetry
1. Extend/retract intake using gamepad2 right stick
2. Verify:
   - Slide position updates
   - Motor power reflects joystick input
   - Limit switch triggers at full retraction
   - Distance sensor readings update

#### Test 3: Deposit Telemetry
1. Extend/retract deposit using gamepad2 left stick
2. Verify:
   - Both motor positions update
   - Laser distance sensor provides height readings
   - State changes are reflected

#### Test 4: Auto Transfer Mode
1. Press gamepad1.a to enable AUTO_RUN
2. Watch the coordinated subsystem movements
3. Verify state transitions are captured in telemetry

### 5. Debugging Tips

#### Check Console Output
Look for any Jackson serialization errors in the Android Studio logcat:
```
adb logcat | grep -E "(EnhancedDashboard|Jackson|JSON)"
```

#### Verify Message Format
The dashboard should receive JSON messages like:
```json
{
  "messageType": "subsystemUpdate",
  "timestamp": 1234567890,
  "subsystemType": "intake",
  "data": {
    "slidePosition": 500,
    "targetPosition": 1000,
    "state": "EXTENDING",
    ...
  }
}
```

#### Common Issues

1. **No data showing**: 
   - Check the dashboard is looking for `__enhanced_dashboard__` key
   - Verify robot is connected and OpMode is running
   - Check network connectivity

2. **Missing subsystem data**:
   - Ensure `sendEnhancedTelemetry()` is called in the loop
   - Check subsystem initialization

3. **Data not updating**:
   - Verify 10Hz rate limiting isn't too aggressive
   - Check `update()` methods are called for subsystems

### 6. Performance Monitoring

Monitor the telemetry update rate in the dashboard. It should maintain approximately 10Hz without significant drops.

## Dashboard Features to Test

1. **Real-time Graphs**: Plot motor currents, positions over time
2. **State Visualization**: Show subsystem states with color coding
3. **Alerts**: Set thresholds for motor currents, distances
4. **Data Recording**: Save telemetry sessions for analysis

## Next Steps

After verifying basic functionality:
1. Add telemetry to remaining subsystems (claws, V4B arms)
2. Create custom dashboard widgets for your specific needs
3. Implement data logging for match analysis
4. Add performance metrics and diagnostics