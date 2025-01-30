package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Lift Test", group = "Linear OpMode")
public class LiftTest extends LinearOpMode {

    private DcMotor liftMotor;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        liftMotor.setDirection(DcMotor.Direction.FORWARD);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // Wait for the game to start (driver presses START)
        waitForStart();



        // Run for a short period (e.g., 1 second) to ensure strafing starts
        sleep(1000);

        // Stop motors after the strafe

        // Main loop (for gamepad control, if needed)
        while (opModeIsActive()) {
            double power = gamepad1.left_stick_y;
            liftMotor.setPower(-power);

//            telemetry.addData("Motors", "power (%.2f)", power);
//            telemetry.update();
        }
    }
}