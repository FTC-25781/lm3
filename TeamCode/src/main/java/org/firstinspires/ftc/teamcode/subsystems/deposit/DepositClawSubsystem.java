package org.firstinspires.ftc.teamcode.subsystems.deposit;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.Subsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeSlideSubsystem;

import java.util.concurrent.TimeUnit;

public class DepositClawSubsystem implements Subsystem {
    public final Servo clawServo;

    private static final double CLAW_OPEN_POS = 0.6;
    private static final double CLAW_CLOSED_POS = 0.9;
    private static final long POSITIONING_TIME_MS = 500; // Constant for positioning time


    public static enum DepositClaw_state {
        INITIALISED,
        UNINITIALISED,
        OPENING,
        OPENED,
        CLOSING,
        CLOSED,
        STOPPED
    }

    ElapsedTime timer = new ElapsedTime();

    public DepositClawSubsystem.DepositClaw_state CURRENT_STATE = DepositClawSubsystem.DepositClaw_state.UNINITIALISED;

    public DepositClawSubsystem(HardwareMap hardwareMap) {
        clawServo = hardwareMap.get(Servo.class, "dclsrv");
        CURRENT_STATE = DepositClawSubsystem.DepositClaw_state.INITIALISED;
    }

    public void runToPreset() {
        closeDepositClaw();
    }

    public void openDepositClaw() {
        if (CURRENT_STATE == DepositClaw_state.OPENING ||
                CURRENT_STATE == DepositClaw_state.OPENED) {
            return;
        }
        clawServo.setPosition(CLAW_OPEN_POS);
        CURRENT_STATE = DepositClaw_state.OPENING;
        timer.reset();
    }

    public void closeDepositClaw() {
        if (CURRENT_STATE == DepositClaw_state.CLOSING ||
        CURRENT_STATE == DepositClaw_state.CLOSED) {
            return;
        }
        clawServo.setPosition(CLAW_CLOSED_POS);
        CURRENT_STATE = DepositClaw_state.CLOSING;
        timer.reset();
    }

    @Override
    public void update() {
        switch (CURRENT_STATE) {
            case OPENING:
                if (timer.time(TimeUnit.MILLISECONDS) > POSITIONING_TIME_MS) {
                    CURRENT_STATE = DepositClaw_state.OPENED;
                }
                break;
            case CLOSING:
                if (timer.time(TimeUnit.MILLISECONDS) > POSITIONING_TIME_MS) {
                    CURRENT_STATE = DepositClaw_state.CLOSED;
                }
                break;
            case OPENED:
            case STOPPED:
            case CLOSED:
            case INITIALISED:
            case UNINITIALISED:
            default:
                break;
        }
    }
}
