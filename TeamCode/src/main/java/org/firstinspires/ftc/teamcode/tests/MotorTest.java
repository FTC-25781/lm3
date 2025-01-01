package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Motor + Servo Test", group = "Teleop")
public class MotorTest extends LinearOpMode {
    private DcMotor motor;

    @Override
    public void runOpMode() {
        // Initialize the servos
        motor = hardwareMap.get(DcMotor.class, "hsmot");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

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