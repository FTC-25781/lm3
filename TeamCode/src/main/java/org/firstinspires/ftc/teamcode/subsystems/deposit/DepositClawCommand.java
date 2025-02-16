package org.firstinspires.ftc.teamcode.subsystems.deposit;

import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositClawSubsystem;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class DepositClawCommand extends OpMode {

    public static DepositClawSubsystem depositClaw;

    public enum ActionType {
        OPEN_CLAW,
        CLOSE_CLAW
    }

    @Override
    public void init() {
        depositClaw = new DepositClawSubsystem(hardwareMap);
    }

    @Override
    public void loop() {

    }

    public static void executeAction(ActionType action) {

        switch (action) {
            case OPEN_CLAW:
                if (depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.OPENING &&
                        depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.OPENED) {
                    depositClaw.openDepositClaw();
                }
                break;

            case CLOSE_CLAW:
                if (depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.CLOSING &&
                        depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.CLOSED) {
                    depositClaw.closeDepositClaw();
                }
                break;

            default:
                break;
        }
    }
}
