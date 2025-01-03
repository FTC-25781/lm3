package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;



@TeleOp(name = "Motor + Servo Test", group = "Teleop")
public class MotorServoTest extends LinearOpMode {
    private Servo servo1;
    private Servo servo2;

    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    @Override
    public void runOpMode() {


        // Initialize the servos
        servo1 = hardwareMap.get(Servo.class, "wsrv1");
        servo2 = hardwareMap.get(Servo.class, "wsrv2");

        servo1.setDirection(Servo.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftDrive  = hardwareMap.get(DcMotor.class, "vsmot");
        rightDrive = hardwareMap.get(DcMotor.class, "vsmot2");

        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        while (opModeIsActive()) {
            // Moving forward: servo1 clockwise, servo2 counterclockwise
            servo1.setPosition(gamepad1.right_stick_y); // Adjust for clockwise
            servo2.setPosition(gamepad1.right_stick_y); // Adjust for counterclockwise

            // Display the servo positions in the telemetry for debugging
            telemetry.addData("Servo1 Position", servo1.getPosition());
            telemetry.addData("Servo2 Position", servo2.getPosition());
            telemetry.update();

            double power = -gamepad1.left_stick_y;
            leftDrive.setPower(power);
            rightDrive.setPower(power);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Motors", "power (%.2f)", power);
            telemetry.update();
        }
    }
}