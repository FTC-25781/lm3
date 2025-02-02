package org.firstinspires.ftc.teamcode.TeleOp;

import static org.firstinspires.ftc.teamcode.pedroPathing.tuning.FollowerConstants.leftFrontMotorName;
import static org.firstinspires.ftc.teamcode.pedroPathing.tuning.FollowerConstants.leftBackMotorName;
import static org.firstinspires.ftc.teamcode.pedroPathing.tuning.FollowerConstants.rightFrontMotorName;
import static org.firstinspires.ftc.teamcode.pedroPathing.tuning.FollowerConstants.rightBackMotorName;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.pedroPathing.follower.Follower;

import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositClawSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositSlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositV4BSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeClawSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeSlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeV4BSubsystem;

import java.util.concurrent.TimeUnit;

/**
 * This is the TeleOpEnhancements OpMode. It is an example usage of the TeleOp enhancements that
 * Pedro Pathing is capable of.
 * @version 1.0, 1/10/2025
 */

@TeleOp(name = "Match TeleOp", group = "Test")
public class TeleOpEnhancements extends OpMode {
    private Follower follower;

    private DcMotorEx leftFront;
    private DcMotorEx leftRear;
    private DcMotorEx rightFront;
    private DcMotorEx rightRear;

    public DepositClawSubsystem depositClaw;
    public DepositSlideSubsystem depositSlide;
    public DepositV4BSubsystem depositV4B;
    public IntakeClawSubsystem intakeClaw;
    public IntakeSlideSubsystem intakeSlide;
    public IntakeV4BSubsystem intakeV4B;

    private boolean AUTO_RUN=false;

    /**
     * This initializes the drive motors as well as the Follower and motion Vectors.
     */
    @Override
    public void init() {
        follower = new Follower(hardwareMap);

        depositClaw  = new DepositClawSubsystem(hardwareMap);
        depositSlide = new DepositSlideSubsystem(hardwareMap, telemetry);
        depositV4B   = new DepositV4BSubsystem(hardwareMap, telemetry);

        intakeClaw  = new IntakeClawSubsystem(hardwareMap);
        intakeSlide = new IntakeSlideSubsystem(hardwareMap, telemetry);
        intakeV4B   = new IntakeV4BSubsystem(hardwareMap);

        leftFront  = hardwareMap.get(DcMotorEx.class, leftFrontMotorName);
        leftRear   = hardwareMap.get(DcMotorEx.class, leftBackMotorName);
        rightRear  = hardwareMap.get(DcMotorEx.class, rightBackMotorName);
        rightFront = hardwareMap.get(DcMotorEx.class, rightFrontMotorName);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        follower.startTeleopDrive();
    }

    /**
     * This runs the OpMode. This is only drive control with Pedro Pathing live centripetal force
     * correction.
     */
    @Override
    public void loop() {
        follower.setTeleOpMovementVectors(-gamepad1.left_stick_y,
                                          -gamepad1.left_stick_x,
                                          -gamepad1.right_stick_x);
        follower.update(gamepad1.right_bumper);

        updateIntakeControls();
        updateDepositControls();
        sendTelemetry();
        //if the right bumper is pressed toggle auto run
        if (gamepad1.a) AUTO_RUN=!AUTO_RUN;
        autoTransfer();
    }

    private void updateIntakeControls() {
        if (gamepad2.a) intakeClaw.openClaw();
        if (gamepad2.b) intakeClaw.closeClaw();

        if(!AUTO_RUN) {
            intakeSlide.manualExtension(gamepad2.right_stick_y);
        }
        if (gamepad2.x) intakeV4B.setWristPickPosition();
        if (gamepad2.y) intakeV4B.setWristDropPosition();
        if (gamepad2.right_stick_button) intakeV4B.setWristDefaultPosition();
        if (gamepad2.left_trigger > 0.5) intakeClaw.setOrientationIncrease();
        if (gamepad2.right_trigger > 0.5) intakeClaw.setOrientationDecrease();

        intakeSlide.update();
        intakeV4B.update();
        intakeClaw.update();
    }

    private void updateDepositControls() {
        if (gamepad2.right_bumper) depositClaw.openDepositClaw();
        if (gamepad2.left_bumper) depositClaw.closeDepositClaw();

        if(!AUTO_RUN) {
            depositSlide.manualExtension(gamepad2.left_stick_y);
        }

        if (gamepad2.dpad_down) depositV4B.setWristPickPosition();
        if (gamepad2.dpad_up) depositV4B.setWristDropPosition();
        if (gamepad2.left_stick_button) depositV4B.setWristSpecimenDropPosition();

        depositSlide.update();
        depositV4B.update();
        depositClaw.update();
    }


    @SuppressLint("DefaultLocale")
    private void sendTelemetry() {

        // Auto Run
        telemetry.addLine("====== Auto run information ======");
        telemetry.addData("Auto run enable: ", AUTO_RUN);

        // Deposit
        telemetry.addLine("====== Deposit information ======");
        telemetry.addLine(String.format("Claw state/Timer: %s / %d",
                          depositClaw.CURRENT_STATE.name(),
                          depositClaw.timer.time(TimeUnit.MILLISECONDS)));
        telemetry.addLine(String.format("ARM state/Timer:  %s / %d",
                          depositV4B.CURRENT_STATE.name(),
                          depositV4B.timer.time(TimeUnit.MILLISECONDS)));
        telemetry.addLine(String.format("Slide state/Power:  %s / (%1.2f, %1.2f)",
                          depositSlide.CURRENT_STATE.name(),
                          depositSlide.verticalSlideMotor.getPower(),
                          depositSlide.verticalSlideMotor2.getPower()));
        telemetry.addData("Vertical raw height(CM): ", depositSlide.laserSensor.getDistance(DistanceUnit.CM));

        // intake
        telemetry.addLine("====== Intake information ======");
        telemetry.addLine(String.format("Claw state/Timer: %s / %d",
                intakeClaw.CURRENT_STATE.name(),
                intakeClaw.timer.time(TimeUnit.MILLISECONDS)));
        telemetry.addLine(String.format("ARM state/Timer:  %s / %d",
                intakeV4B.CURRENT_STATE.name(),
                intakeV4B.timer.time(TimeUnit.MILLISECONDS)));
        telemetry.addLine(String.format("Slide state/Power:  %s / %1.2f",
                intakeSlide.CURRENT_STATE.name(),
                intakeSlide.slideMotor.getPower()));
        telemetry.addData("Horizontal range(CM)", intakeSlide.sensorDistance.getDistance(DistanceUnit.CM));
        telemetry.addData("Orientation Position", intakeClaw.orientationServo.getPosition());
        telemetry.update();
    }

    private void autoTransfer() { //automatically transfers the sample from the intake to the deposit

        if (!AUTO_RUN)
            return;

        // Intake independent of deposit
        //intake wrist to drop position
        if(intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.DROP_POSITION &&
           intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.DROP_POSITIONING) {
            intakeV4B.setWristDropPosition();
        }

        //retract the intake slide
        if(intakeSlide.CURRENT_STATE != IntakeSlideSubsystem.Intake_state.RETRACTING &&
           intakeSlide.CURRENT_STATE != IntakeSlideSubsystem.Intake_state.RETRACTED) {
            intakeSlide.retractMainSlide();
        }

        // Deposit independent of intake
        //deposit wrist to pick position
        if (depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.PICK_POSITION &&
            depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.PICK_POSITIONING) {
            depositV4B.setWristPickPosition();
        }
        //open the deposit claw
        if(depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.OPENED &&
           depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.OPENING) {
            depositClaw.openDepositClaw();
        }

        // Both Sync
        //retract the deposit slide to pick position
        if(intakeSlide.CURRENT_STATE == IntakeSlideSubsystem.Intake_state.RETRACTED &&
                depositSlide.CURRENT_STATE != DepositSlideSubsystem.Deposit_state.RETRACT_PICKING &&
                depositSlide.CURRENT_STATE != DepositSlideSubsystem.Deposit_state.RETRACT_PICKED) {
            depositSlide.retractPickDepositMainSlide();
        }
        //close the deposit claw
        if(depositSlide.CURRENT_STATE == DepositSlideSubsystem.Deposit_state.RETRACT_PICKED &&
                depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.CLOSED &&
                depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.CLOSING) {
            depositClaw.closeDepositClaw();
        }
        //open the intake claw
        if(depositClaw.CURRENT_STATE == DepositClawSubsystem.DepositClaw_state.CLOSED &&
                intakeClaw.CURRENT_STATE != IntakeClawSubsystem.IntakeClaw_state.OPENED &&
                intakeClaw.CURRENT_STATE != IntakeClawSubsystem.IntakeClaw_state.OPENING) {
            intakeClaw.openClaw();
        }
        //extend the deposit slide
        if(intakeClaw.CURRENT_STATE == IntakeClawSubsystem.IntakeClaw_state.OPENED &&
                depositSlide.CURRENT_STATE != DepositSlideSubsystem.Deposit_state.EXTENDED &&
                depositSlide.CURRENT_STATE != DepositSlideSubsystem.Deposit_state.EXTENDING) {
            depositSlide.extendDepositMainSlide();
        }
        //deposit wrist to drop position
        if(depositSlide.CURRENT_STATE == DepositSlideSubsystem.Deposit_state.EXTENDED &&
                depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.DROP_POSITION &&
                depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.DROP_POSITIONING) {
            depositV4B.setWristDropPosition();
            AUTO_RUN=false;
        }

        intakeSlide.update();
        intakeV4B.update();
        intakeClaw.update();
        depositSlide.update();
        depositV4B.update();
        depositClaw.update();
    }
}
