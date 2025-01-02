package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;



@TeleOp(name = "Servo Test", group = "Teleop")
public class ServoTest extends LinearOpMode {
    private Servo iservo1;
    private Servo iservo2;

    private Servo dservo1;
    private Servo dservo2;

    private Servo intakeClawServo;
    private Servo depositClawServo;
    private Servo orientation;

    private static final double DEPOSIT_DROP = 0.0;
    private static final double DEPOSIT_PICKUP = 0.73;
    private static final double DEPOSIT_SPEC = 1.0;
    private static final double INTAKE_DROP = 0.38;
    private static final double INTAKE_DEFAULT = 0.15;
    private static final double INTAKE_PICKUP = 0.05;
    private static final double INTAKE_CLOSE = 1.0;
    private static final double INTAKE_OPEN = 0.77;
    private static final double DEPOSIT_OPEN = 0.4;
    private static final double DEPOSIT_CLOSE = 0.8;

    @Override
    public void runOpMode() {


        // Initialize the servos
        iservo1 = hardwareMap.get(Servo.class, "wsrv1"); //port 2
        iservo2 = hardwareMap.get(Servo.class, "wsrv2"); //port 3

        dservo1 = hardwareMap.get(Servo.class, "dwsrv1"); //ex port 1
        dservo2 = hardwareMap.get(Servo.class, "dwsrv2"); //ex port 0

        intakeClawServo  = hardwareMap.get(Servo.class, "clsrv"); //port 1
        depositClawServo  = hardwareMap.get(Servo.class, "dclsrv"); //ex port 2
//
        orientation =  hardwareMap.get(Servo.class, "orsrv"); //port 0

        iservo1.setDirection(Servo.Direction.REVERSE);
        dservo1.setDirection(Servo.Direction.REVERSE);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        iservo1.setPosition(0.5); // Adjust for clockwise
        iservo2.setPosition(0.5); // Adjust for counterclockwise
        dservo1.setPosition(0.5);
        dservo2.setPosition(0.5);
        depositClawServo.setPosition(DEPOSIT_CLOSE);
        intakeClawServo.setPosition(INTAKE_CLOSE);

        while (opModeIsActive()) {

            if (gamepad2.x) {
                iservo1.setPosition(INTAKE_PICKUP);
                iservo2.setPosition(INTAKE_PICKUP);
            }

            if (gamepad2.y) {
                iservo1.setPosition(INTAKE_DEFAULT);
                iservo2.setPosition(INTAKE_DEFAULT);
            }
//
//            intakeClawServo.setPosition(gamepad2.left_stick_y);
            orientation.setPosition(gamepad2.left_stick_y);

            if (gamepad2.dpad_left) {
                depositClawServo.setPosition(DEPOSIT_CLOSE);
            }

            if (gamepad2.dpad_right) {
                depositClawServo.setPosition(DEPOSIT_OPEN);
            }

//

//
            // Drop position
            if (gamepad2.dpad_down) {
                dservo1.setPosition(DEPOSIT_PICKUP);
                dservo2.setPosition(DEPOSIT_PICKUP);
            }

            // pick position
            if (gamepad2.dpad_up) {
                dservo1.setPosition(DEPOSIT_SPEC);
                dservo2.setPosition(DEPOSIT_SPEC);
            }

            // Sample postion
            if (gamepad2.left_stick_button) {
                dservo1.setPosition(DEPOSIT_DROP);
                dservo2.setPosition(DEPOSIT_DROP);
            }


            // Display the servo positions in the telemetry for debugging
            telemetry.addData("Servo1 Position", iservo1.getPosition());
            telemetry.addData("Servo2 Position", iservo2.getPosition());
            telemetry.addData("Left Stick Y", gamepad2.left_stick_y);

            telemetry.addData("Deposit Servo1 Position", dservo1.getPosition());
            telemetry.addData("Deposit Servo2 Position", dservo2.getPosition());
            telemetry.addData("Right Stick Y", gamepad2.right_stick_y);

            telemetry.update();
        }
    }
}