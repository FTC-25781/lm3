package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Motor Direction Test", group = "Linear OpMode")
public class MotorTest extends LinearOpMode {

    private DcMotor rightBack;
    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftFront = hardwareMap.get(DcMotor.class, "left_front");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        leftBack = hardwareMap.get(DcMotor.class, "left_back");
        rightBack = hardwareMap.get(DcMotor.class, "right_back");

        // Wait for the game to start (driver presses START)
        waitForStart();

        // Hard-code strafing to the right at full power
        leftFront.setPower(1);
        rightFront.setPower(-1);
        leftBack.setPower(-1);
        rightBack.setPower(1);

        // Run for a short period (e.g., 1 second) to ensure strafing starts
        sleep(1000);

        // Stop motors after the strafe

        // Main loop (for gamepad control, if needed)
        while (opModeIsActive()) {
            leftFront.setPower(0);
            rightFront.setPower(0);
            leftBack.setPower(0);
            rightBack.setPower(0);

//            telemetry.addData("Motors", "power (%.2f)", power);
//            telemetry.update();
        }
    }
}