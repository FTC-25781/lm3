package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Intake Orientation Test", group = "Linear OpMode")
public class clawOrientationTest extends LinearOpMode {
    private Servo orientationServo;

    @Override
    public void runOpMode() {
        orientationServo = hardwareMap.get(Servo.class, "orsrv");
        waitForStart();

        while (opModeIsActive()) {
            orientationServo.setPosition(gamepad1.left_stick_y);

            telemetry.addData("Servo value", orientationServo.getPosition());
            telemetry.update();
        }
    }
}
