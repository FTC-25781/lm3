package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "PID SarXX Test", group = "Test")
public class PIDMotorTest extends LinearOpMode {

    private DcMotorEx pidMotor;

    // PID values
    private final double Kp = 0.1;
    private final double Ki = 0.0;
    private final double Kd = 0.1;

    // Variables
    private double targetPosition = 200;
    private double lastError = 0;
    private double lastTime = 0;
    private double integral = 0;

    @Override
    public void runOpMode() {
        pidMotor = hardwareMap.get(DcMotorEx.class, "sarxxmot");

        // Makes sure that motor goes in right direction so I don't break the slides again :)
        pidMotor.setDirection(DcMotorEx.Direction.FORWARD);

        // Motor commands
        pidMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        pidMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        pidMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        waitForStart();
        lastTime = System.currentTimeMillis();

        while (opModeIsActive()) {
            double power = calculatePID(targetPosition, pidMotor.getCurrentPosition());

            // Clculates and limits power
            power = Math.max(-1, Math.min(1, power));

            pidMotor.setPower(power);

            // Telemetry
            telemetry.addData("Target Position", targetPosition);
            telemetry.addData("Current Position", pidMotor.getCurrentPosition());
            telemetry.addData("Power", power);
            telemetry.update();
        }

        // Stops the motor at end of the loop
        pidMotor.setPower(0.0);
    }

    // Math to calculate and return final PID values
    private double calculatePID(double target, double current) {
        double currentTime = getRuntime();
        double deltaTime = currentTime - lastTime;
        lastTime = currentTime;

        double error = target - current;

        double proportional = Kp * error;

        integral += error * deltaTime;
        if (Math.abs(integral) > 1000) {
            integral = Math.signum(integral) * 100;
        }

        double derivative = Kd * (error - lastError) / (deltaTime + 1e-6);
        lastError = error;

        return proportional + Ki * integral + derivative;
    }
}