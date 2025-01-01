package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Motor Test", group = "Teleop")
public class MotorTest extends LinearOpMode {
    private DcMotor motor;

    @Override
    public void runOpMode() {
        // Initialize the servos
        motor = hardwareMap.get(DcMotor.class, "hsmot");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        while (opModeIsActive()) {
            double power = gamepad1.left_stick_y;
            motor.setPower(power);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Motors", motor.getCurrentPosition());
            telemetry.update();
        }
    }
}