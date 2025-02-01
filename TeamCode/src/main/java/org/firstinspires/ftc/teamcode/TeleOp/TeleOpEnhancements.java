package org.firstinspires.ftc.teamcode.teleop;

import static org.firstinspires.ftc.teamcode.pedroPathing.tuning.FollowerConstants.leftFrontMotorName;
import static org.firstinspires.ftc.teamcode.pedroPathing.tuning.FollowerConstants.leftBackMotorName;
import static org.firstinspires.ftc.teamcode.pedroPathing.tuning.FollowerConstants.rightFrontMotorName;
import static org.firstinspires.ftc.teamcode.pedroPathing.tuning.FollowerConstants.rightBackMotorName;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.pedroPathing.follower.Follower;

import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositClawSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositSlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositV4BSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeClawSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeSlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeV4BSubsystem;

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

    /**
     * This initializes the drive motors as well as the Follower and motion Vectors.
     */
    @Override
    public void init() {
        follower = new Follower(hardwareMap);

        depositClaw = new DepositClawSubsystem(hardwareMap);
        depositSlide = new DepositSlideSubsystem(hardwareMap, telemetry);
        depositV4B = new DepositV4BSubsystem(hardwareMap, telemetry);
        intakeClaw = new IntakeClawSubsystem(hardwareMap);
        intakeSlide = new IntakeSlideSubsystem(hardwareMap, telemetry);
        intakeV4B = new IntakeV4BSubsystem(hardwareMap);

        leftFront = hardwareMap.get(DcMotorEx.class, leftFrontMotorName);
        leftRear = hardwareMap.get(DcMotorEx.class, leftBackMotorName);
        rightRear = hardwareMap.get(DcMotorEx.class, rightBackMotorName);
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
        follower.setTeleOpMovementVectors(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x);
        follower.update();

        updateIntakeControls();
        updateDepositControls();
        sendTelemetry();
        autoTransfer();
    }

    private void updateIntakeControls() {
        if (gamepad2.a) intakeClaw.openClaw();
        if (gamepad2.b) intakeClaw.closeClaw();

        intakeSlide.manualExtension(gamepad2.right_stick_y);

        if (gamepad2.x) intakeV4B.setWristPickPosition();
        if (gamepad2.y) intakeV4B.setWristDropPosition();
        if (gamepad2.right_stick_button) intakeV4B.setWristDefaultPosition();
        if (gamepad2.left_trigger > 0.5) intakeClaw.setOrientationIncrease();
        if (gamepad2.right_trigger > 0.5) intakeClaw.setOrientationDecrease();
    }

    private void updateDepositControls() {
        if (gamepad2.right_bumper) depositClaw.openDepositClaw();
        if (gamepad2.left_bumper) depositClaw.closeDepositClaw();

        depositSlide.manualExtension(gamepad2.left_stick_y);

        if (gamepad2.dpad_down) depositV4B.setWristPickPosition();
        if (gamepad2.dpad_up) depositV4B.setWristDropPosition();
        if (gamepad2.left_stick_button) depositV4B.setWristSpecimenDropPosition();
    }
    private void autoTransfer() { //automatically transfers the sample from the intake to the deposit
        //if the right bumper is pressed the transfer will start
        if (gamepad1.right_bumper) {
            //open the deposit claw
            if(depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.OPENED &&
                    depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.OPENING) {
                depositClaw.openDepositClaw();
            }
            //deposit wrist to pick position
            if (depositClaw.CURRENT_STATE == DepositClawSubsystem.DepositClaw_state.OPENED &&
                    depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.PICK_POSITION &&
                    depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.PICK_POSITIONING) {
                depositV4B.setWristPickPosition();
            }
            //intake wrist to drop position
            if(depositV4B.CURRENT_STATE == DepositV4BSubsystem.Depositv4b_state.PICK_POSITION &&
                    intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.DROP_POSITION &&
                    intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.DROP_POSITIONING) {
                intakeV4B.setWristDropPosition();
            }
            //retract the intake slide
            if(intakeV4B.CURRENT_STATE == IntakeV4BSubsystem.Intakev4b_state.DROP_POSITION &&
                    intakeSlide.CURRENT_STATE != IntakeSlideSubsystem.Intake_state.RETRACTING &&
                    intakeSlide.CURRENT_STATE != IntakeSlideSubsystem.Intake_state.RETRACTED) {
                intakeSlide.retractMainSlide();
            }
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
            }
        }
    }


    private void sendTelemetry() {
        telemetry.addData("Orientation Position", intakeClaw.orientationServo.getPosition());
        telemetry.addData("Wrist Servo 1 Position", intakeV4B.wristServo1.getPosition());
        telemetry.addData("Wrist Servo 2 Position", intakeV4B.wristServo2.getPosition());
        telemetry.addData("Slide Power", intakeSlide.slideMotor.getPower());
        telemetry.addData("Slide Position", intakeSlide.slideMotor.getCurrentPosition());
        telemetry.update();
    }
}
