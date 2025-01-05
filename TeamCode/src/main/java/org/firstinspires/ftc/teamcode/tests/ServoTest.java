package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Servo Test", group = "Teleop")
public class ServoTest extends LinearOpMode {
    private Servo iservo1; // 0.37
    private Servo iservo2;

    private Servo dservo1;
    private Servo dservo2;

    private Servo intakeClawServo;
    private Servo depositClawServo;

    private static final double SERVO_INCREMENT = 0.01;

    @Override
    public void runOpMode() {
        // Initialize the servos
//        iservo1 = hardwareMap.get(Servo.class, "wsrv1"); //port 2
//        iservo2 = hardwareMap.get(Servo.class, "wsrv2"); //port 3

        dservo1 = hardwareMap.get(Servo.class, "dwsrv1"); //ex port 1
        dservo2 = hardwareMap.get(Servo.class, "dwsrv2"); //ex port 0

        intakeClawServo = hardwareMap.get(Servo.class, "clsrv"); //port 1
//        depositClawServo = hardwareMap.get(Servo.class, "dclsrv"); //ex port 2

        iservo1.setDirection(Servo.Direction.REVERSE);
        dservo1.setDirection(Servo.Direction.REVERSE);

        // Set initial positions
        iservo1.setPosition(0.5);
        iservo2.setPosition(0.5);
        dservo1.setPosition(0.5);
        dservo2.setPosition(0.5);
        intakeClawServo.setPosition(0.5);
//        depositClawServo.setPosition(0.5);

        // Wait for the game to start
        waitForStart();

        while (opModeIsActive()) {
            // Gamepad controls for `iservo1` and `iservo2` using gamepad1
//            if (gamepad1.dpad_up) {
//                iservo1.setPosition(iservo1.getPosition() + SERVO_INCREMENT);
//                iservo2.setPosition(iservo2.getPosition() + SERVO_INCREMENT);
//            } else if (gamepad1.dpad_down) {
//                iservo1.setPosition(iservo1.getPosition() - SERVO_INCREMENT);
//                iservo2.setPosition(iservo2.getPosition() - SERVO_INCREMENT);
//            }

            dservo1.setPosition(gamepad1.right_stick_y);
            dservo2.setPosition(gamepad1.right_stick_y);

//            // Gamepad controls for `dservo1` and `dservo2` using gamepad2
//            if (gamepad2.left_bumper) {
//                dservo1.setPosition(dservo1.getPosition() + SERVO_INCREMENT);
//                dservo2.setPosition(dservo2.getPosition() + SERVO_INCREMENT);
//            } else if (gamepad2.right_bumper) {
//                dservo1.setPosition(dservo1.getPosition() - SERVO_INCREMENT);
//                dservo2.setPosition(dservo2.getPosition() - SERVO_INCREMENT);
//            }
//
//            // Gamepad controls for `intakeClawServo`
//            if (gamepad2.y) {
//                intakeClawServo.setPosition(intakeClawServo.getPosition() + SERVO_INCREMENT);
//            } else if (gamepad2.x) {
//                intakeClawServo.setPosition(intakeClawServo.getPosition() - SERVO_INCREMENT);
//            }
//
//            // Gamepad controls for `depositClawServo`
//            if (gamepad2.dpad_up) {
//                depositClawServo.setPosition(depositClawServo.getPosition() + SERVO_INCREMENT);
//            } else if (gamepad2.dpad_down) {
//                depositClawServo.setPosition(depositClawServo.getPosition() - SERVO_INCREMENT);
//            }

            // Display servo positions on telemetry
            telemetry.addData("Servo1 Position", iservo1.getPosition());
            telemetry.addData("Servo2 Position", iservo2.getPosition());
            telemetry.addData("Deposit Servo1 Position", dservo1.getPosition());
            telemetry.addData("Deposit Servo2 Position", dservo2.getPosition());
            telemetry.addData("Intake Claw Servo Position", intakeClawServo.getPosition());
//            telemetry.addData("Deposit Claw Servo Position", depositClawServo.getPosition());
            telemetry.update();
        }
    }
}
