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

    public Runnable openDepositClaw() {
        clawServo.setPosition(CLAW_OPEN_POS);
        CURRENT_STATE = DepositClaw_state.OPENING;
        timer.reset();
        return null;
    }

    public Runnable closeDepositClaw() {
        clawServo.setPosition(CLAW_CLOSED_POS);
        CURRENT_STATE = DepositClaw_state.CLOSING;
        timer.reset();
        return null;
    }

    @Override
    public void update() {
        switch (CURRENT_STATE) {
            case OPENING:
                if (timer.time(TimeUnit.MILLISECONDS) > 1000) {
                    CURRENT_STATE = DepositClaw_state.OPENED;
                }
                break;
            case CLOSING:
                if (timer.time(TimeUnit.MILLISECONDS) > 1000) {
                    CURRENT_STATE = DepositClaw_state.CLOSED;
                }
                break;
            case OPENED:
                break;
            case STOPPED:
                break;
            case CLOSED:
                break;
            case INITIALISED:
                break;
            case UNINITIALISED:
                break;
            default:
                break;
        }
    }
}
