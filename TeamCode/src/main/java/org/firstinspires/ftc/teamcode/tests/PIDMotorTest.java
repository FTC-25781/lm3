package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "PID SarXX Test", group = "Test")
public class PIDMotorTest extends LinearOpMode {

    private DcMotorEx pidMotor;

    // PID values
    private final double Kp = 0.09;
    private final double Ki = 0;
    private final double Kd = 0;

    // Variables
    private double targetPosition = 18003;
    private double Error = 0;
    private double Time = 0;
    private double integral = 0;

    // FTC Dashboard instance
    private FtcDashboard dashboard;

    @Override
    public void runOpMode() {
        pidMotor = hardwareMap.get(DcMotorEx.class, "sarxxmot");

        // Initialize the FTC Dashboard
        dashboard = FtcDashboard.getInstance();
        Telemetry dashboardTelemetry = dashboard.getTelemetry();

        // Makes sure that motor goes in right direction so I don't break the slides again :)
        pidMotor.setDirection(DcMotorEx.Direction.FORWARD);

        // Motor commands
        pidMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        pidMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        pidMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        waitForStart();
        Time = System.currentTimeMillis();

        // Calculates and limits power
        while (opModeIsActive()) {
            double rawpower = calculatePID(targetPosition, pidMotor.getCurrentPosition());


            // Calculates and limits power
            double power = Math.max(-1, Math.min(1, rawpower));

            pidMotor.setPower(power);

            // Telemetry for Driver Station
            telemetry.addData("Target Position", targetPosition);
            telemetry.addData("Current Position", pidMotor.getCurrentPosition());
            telemetry.addData("Power", power);
            telemetry.addData("Kp", Kp);
            telemetry.addData("Ki", Ki);
            telemetry.addData("Kd", Kd);
            telemetry.addData("Error", Error);
            telemetry.addData("Time", Time);
            telemetry.addData("Integral", integral);
            telemetry.addData("Before capping the power", rawpower);
            telemetry.update();

            // Telemetry for Dashboard
            // TelemetryPacket packet = new TelemetryPacket();
            dashboardTelemetry.addData("Target Position", targetPosition);
            dashboardTelemetry.addData("Current Position", pidMotor.getCurrentPosition());
            dashboardTelemetry.addData("Before capping the power", rawpower);
            dashboardTelemetry.addData("Power", power);
            dashboardTelemetry.addData("Kp", Kp);
            dashboardTelemetry.addData("Ki", Ki);
            dashboardTelemetry.addData("Kd", Kd);
            dashboardTelemetry.addData("Error", Error);
            dashboardTelemetry.addData("Time", Time);
            dashboardTelemetry.addData("Integral", integral);

            // Sends telemetry to Dashboard
            dashboardTelemetry.update();
        }

        // Stops the motor at the end of the loop
        pidMotor.setPower(0.0);
    }

    // Math to calculate and return final PID values
    private double calculatePID(double target, double current) {
        double currentTime = getRuntime();
        double deltaTime = currentTime - Time;
        Time = currentTime;

        double error = target - current;

        double proportional = Kp * error/74;

        integral += error * deltaTime;
        if (Math.abs(integral) > 999) {
            integral = Math.signum(integral);
        }

        double derivative = Kd * (error - Error) / (deltaTime + 1e-6);
        Error = error;

        return proportional + Ki * integral + derivative;
    }
}
