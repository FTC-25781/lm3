package org.firstinspires.ftc.teamcode.subsystems.deposit;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeSlideSubsystem;

public class DepositClawSubsystem implements Subsystem {

    public final Servo clawServo;

    private static final double CLAW_OPEN_POS = 0.4;
    private static final double CLAW_CLOSED_POS = 0.7;

    public static enum DepositClaw_state {
        INITIALISED,
        UNINITIALISED,
        OPENING,
        OPENED,
        CLOSING,
        CLOSED,
        STOPPED
    }

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
        return null;
    }

    public Runnable closeDepositClaw() {
        clawServo.setPosition(CLAW_CLOSED_POS);
        CURRENT_STATE = DepositClaw_state.CLOSING;
        return null;
    }

    @Override
    public void update() {
        switch (CURRENT_STATE) {
            case OPENING:
                break;
            case CLOSING:
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
