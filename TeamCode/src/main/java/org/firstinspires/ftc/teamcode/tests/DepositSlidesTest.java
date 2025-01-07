package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Deposit Slides Test", group = "Linear OpMode")
public class DepositSlidesTest extends LinearOpMode {
    private DcMotor Mot1;
    private DcMotor Mot2;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        Mot1 = hardwareMap.get(DcMotor.class, "vsmot");
        Mot2 = hardwareMap.get(DcMotor.class, "vsmot2");

        Mot1.setDirection(DcMotor.Direction.REVERSE);
        Mot2.setDirection(DcMotor.Direction.FORWARD);

        Mot1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Mot2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Wait for the game to start (driver presses START)
        waitForStart();

        // Stop motors after the strafe

        // Main loop (for gamepad control, if needed)
        while (opModeIsActive()) {
            double power = gamepad1.left_stick_y;
            Mot1.setPower(power);
            Mot2.setPower(power);

            telemetry.addData("Motor 1", "power (%.2f)", Mot1.getPower());
            telemetry.addData("Motor 2", "power (%.2f)", Mot2.getPower());
            telemetry.update();
        }
    }
}
