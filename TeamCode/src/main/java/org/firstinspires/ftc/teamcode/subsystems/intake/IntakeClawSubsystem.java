package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositV4BSubsystem;

import java.util.concurrent.TimeUnit;

public class IntakeClawSubsystem {

    public final Servo clawServo;
    public final Servo orientationServo;

    private static final double CLAW_OPEN_POS = 0.77;
    private static final double CLAW_CLOSED_POS = 1.0;
    private double currentOrientation = 0.0;
    private static final long POSITIONING_TIME_MS = 500; // Constant for positioning time

    public static enum IntakeClaw_state {
        INITIALISED,
        UNINITIALISED,
        OPENING,
        OPENED,
        CLOSING,
        CLOSED,
        STOPPED
    }

    public final ElapsedTime timer = new ElapsedTime();

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
    public void openClaw() {
        if (CURRENT_STATE == IntakeClaw_state.OPENING ||
        CURRENT_STATE == IntakeClaw_state.OPENED) {
            return;
        }
        clawServo.setPosition(CLAW_OPEN_POS);
        timer.reset();
        CURRENT_STATE = IntakeClaw_state.OPENING;
    }

    // Closes the claw to a pre-defined position
    public void closeClaw()   {
        if (CURRENT_STATE == IntakeClaw_state.CLOSING ||
        CURRENT_STATE == IntakeClaw_state.CLOSED) {
            return;
        }
        clawServo.setPosition(CLAW_CLOSED_POS);
        timer.reset();
        CURRENT_STATE = IntakeClaw_state.CLOSING;
    }

    private double orientationClamp(double value) {
        return Math.max(0.0, Math.min(0.55, value));
    }

    public void update() {
        switch (CURRENT_STATE) {
            case OPENING:
                if (timer.time(TimeUnit.MILLISECONDS) > POSITIONING_TIME_MS) {
                    CURRENT_STATE = IntakeClaw_state.OPENED;
                }
                break;
            case CLOSING:
                if (timer.time(TimeUnit.MILLISECONDS) > POSITIONING_TIME_MS) {
                    CURRENT_STATE = IntakeClaw_state.CLOSED;
                }
                break;
            case OPENED:
            case CLOSED:
            case INITIALISED:
            case UNINITIALISED:
            case STOPPED:
            default:
                break;
        }
    }
}
