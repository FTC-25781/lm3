/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.sensors;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * This OpMode illustrates how to use the Modern Robotics Range Sensor.
 *
 * The OpMode assumes that the range sensor is configured with a name of "sensor_range".
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 *
 * @see <a href="http://modernroboticsinc.com/range-sensor">MR Range Sensor</a>
 */
@TeleOp(name = "Sensor: MR range sensor", group = "Sensor")
// comment out or remove this line to enable this OpMode
public class SensorMRRangeSensor extends LinearOpMode {

    private UltrasonicDistanceSensor rangeSensor;
    private DcMotorEx Mot1;
    private DcMotorEx  Mot2;
    private LynxModule hub;
    // private UltrasonicSensor ultrasonicSensor;
    private VoltageSensor batteryVoltageSensor;
    private AnalogInput currentSensor;

    // Reference values
    private static final double NOMINAL_BATTERY_VOLTAGE = 12.0; // Fully charged battery
    private static final double MAX_MOTOR_CURRENT = 5.0; // Max safe motor current in Amps

    @Override public void runOpMode() {

        double currentDistance;



        telemetry.addData("Status", "Initialized");
        telemetry.update();

        Mot1 = hardwareMap.get(DcMotorEx.class, "vsmot");
        Mot2 = hardwareMap.get(DcMotorEx.class, "vsmot2");
        hub = (LynxModule) hardwareMap.get(LynxModule.class, "Control Hub");
        // ultrasonicSensor = hardwareMap.get(UltrasonicSensor.class, "ultrasonicSensor");
        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();

        // currentSensor = hardwareMap.get(AnalogInput.class, "currentSensor"); // Analog current sensor

        // ultrasonicSensor.getUltrasonicLevel();

        Mot1.setDirection(DcMotor.Direction.REVERSE);
        Mot2.setDirection(DcMotor.Direction.FORWARD);

        Mot1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Mot2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // get a reference to our compass
        rangeSensor = new UltrasonicDistanceSensor(hardwareMap.get(AnalogInput.class, "vdist1"), hub);
        MovingAverageWithOutlier movingAverage = new MovingAverageWithOutlier(15, 7);

        // wait for the start button to be pressed
        waitForStart();

        while (opModeIsActive()) {
            double power = gamepad1.left_stick_y;

            // Set motor power
            Mot1.setPower(adjMotorPower(power, hub.getInputVoltage(VoltageUnit.VOLTS), Mot1.getCurrent(CurrentUnit.AMPS)));
            Mot2.setPower(adjMotorPower(power, hub.getInputVoltage(VoltageUnit.VOLTS), Mot2.getCurrent(CurrentUnit.AMPS)));

            currentDistance = rangeSensor.getDistance(DistanceUnit.INCH);
            // Window size 5, outlier threshold 10
            telemetry.addLine("====== Range Sensor information ======");
            telemetry.addData("Average distance (Inch)", "%.2f inch", movingAverage.add(currentDistance));
            telemetry.addData("Current distance (Inch)", "%.2f inch", currentDistance);
            telemetry.addData("Sensor O/P Voltage", rangeSensor.voltage);
            telemetry.addLine("====== Motor information ======");
            telemetry.addData("Motor requested Power ", "%.2f ", power);
            telemetry.addData("Motor-1 Current", Mot1.getCurrent(CurrentUnit.AMPS));
            telemetry.addData("Motor-2 Current", Mot2.getCurrent(CurrentUnit.AMPS));
            telemetry.addLine("====== Hub information ======");
            telemetry.addData("Battery Voltage", hub.getInputVoltage(VoltageUnit.VOLTS));
            telemetry.addData("Battery Current",  hub.getCurrent(CurrentUnit.AMPS));
            telemetry.update();
        }
    }

    private double adjMotorPower(double basePower, double batteryVoltage, double motorCurrent){
        // Compute power adjustment based on battery voltage
        double voltageCompensation = NOMINAL_BATTERY_VOLTAGE / batteryVoltage;

        // Limit power if current draw is too high (overload protection)
        double currentCompensation = 1.0;
        if (motorCurrent > MAX_MOTOR_CURRENT) {
            currentCompensation = MAX_MOTOR_CURRENT / motorCurrent;
        }

        // Calculate final motor power
        double adjustedPower = basePower * voltageCompensation * currentCompensation;
        return Math.max(-1, Math.min(1.0, adjustedPower)); // Ensure -1.0 <= power <= 1.0
    }
}
