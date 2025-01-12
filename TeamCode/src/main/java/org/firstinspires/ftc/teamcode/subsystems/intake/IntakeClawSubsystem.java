package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.TimeUnit;

public class IntakeClawSubsystem {

    public final Servo clawServo;
    public final Servo orientationServo;

    private static final double CLAW_OPEN_POS = 0.77;
    private static final double CLAW_CLOSED_POS = 1.0;
    private double currentOrientation = 0.0;

    public static enum IntakeClaw_state {
        INITIALISED,
        UNINITIALISED,
        OPENING,
        OPENED,
        CLOSING,
        CLOSED,
        STOPPED
    }

    private final ElapsedTime timer = new ElapsedTime();

    public IntakeClawSubsystem.IntakeClaw_state CURRENT_STATE = IntakeClawSubsystem.IntakeClaw_state.UNINITIALISED;

    public IntakeClawSubsystem(HardwareMap hardwareMap) {
        clawServo = hardwareMap.get(Servo.class, "clsrv");
        orientationServo = hardwareMap.get(Servo.class, "orsrv");

        if (clawServo == null || orientationServo == null) {
            throw new IllegalArgumentException("Failed to initialize one or more servos.");
        }

        CURRENT_STATE = IntakeClaw_state.INITIALISED;
    }

    // Moves orientation servo to preset position
    public void runToPreset() {
        orientationServo.setPosition(1.0);
    }

    // Increases orientation by 10 degrees, capping at 90 degrees
    public void setOrientationIncrease() {
        if (currentOrientation < 90.0) {
            currentOrientation += 10.0;
            double servoPosition = currentOrientation / 180.0;
            orientationServo.setPosition(servoPosition);
        }
    }

    public void setOrientationDecrease() {
        if (currentOrientation > 0) {
            currentOrientation -= 10.0;
            double servoPosition = currentOrientation / 180.0;
            orientationServo.setPosition(servoPosition);
        }
    }

    // Opens the claw to a pre-defined position
    public Runnable openClaw() {
        clawServo.setPosition(CLAW_OPEN_POS);
        CURRENT_STATE = IntakeClaw_state.OPENING;
        timer.reset();
        timer.startTime();
        return null;
    }

    // Closes the claw to a pre-defined position
    public Runnable closeClaw() {
        clawServo.setPosition(CLAW_CLOSED_POS);
        CURRENT_STATE = IntakeClaw_state.CLOSING;
        timer.reset();
        timer.startTime();
        return null;
    }

    private double orientationClamp(double value) {
        return Math.max(0.0, Math.min(0.55, value));
    }

    public void update() {
        switch (CURRENT_STATE) {
            case OPENING:
                if (timer.time(TimeUnit.MILLISECONDS) > 1000) {
                    CURRENT_STATE = IntakeClaw_state.OPENED;
                }
                break;
            case CLOSING:
                if (timer.time(TimeUnit.MILLISECONDS) > 1000) {
                    CURRENT_STATE = IntakeClaw_state.CLOSED;
                }
                break;
            case OPENED:
                break;
            case CLOSED:
                break;
            case INITIALISED:
                break;
            case UNINITIALISED:
                break;
            case STOPPED:
                break;
            default:
                break;
        }
    }
}
