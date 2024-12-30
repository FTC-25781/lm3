package org.firstinspires.ftc.teamcode.subsystems.deposit;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;

public class DepositClawSubsystem implements Subsystem {

    public final Servo clawServo;

    private static final double CLAW_OPEN_POS = 0.4;
    private static final double CLAW_CLOSED_POS = 0.7;

    public DepositClawSubsystem(HardwareMap hardwareMap) {
        clawServo = hardwareMap.get(Servo.class, "dclsrv");
    }

    public void runToPreset() {
        closeDepositClaw();
    }

    public void openDepositClaw() {
        clawServo.setPosition(CLAW_OPEN_POS);
    }

    public void closeDepositClaw() {
        clawServo.setPosition(CLAW_CLOSED_POS);
    }

    @Override
    public void update() {
        // No-op: Implement any periodic updates if needed.
    }
}
