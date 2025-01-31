package org.firstinspires.ftc.teamcode.sensors;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;

public class UltrasonicDistanceSensor implements DistanceSensor {

    private final AnalogInput analog;
    private LynxModule hub;
    public double voltage = 0;

    public static double MAX_RANGE = 120.0;

    public static double MIN_RANGE = 5;

    public UltrasonicDistanceSensor(AnalogInput analog, LynxModule hub) {

        this.analog = analog;
        this.hub = hub;
    }

    @Override
    public double getDistance(DistanceUnit unit) {
        double inches=0;
        double temp=0;

        double batteryVoltage = hub.getInputVoltage(VoltageUnit.VOLTS);
        voltage = analog.getVoltage();
        for(int i=0; i<11; i++) {
            inches = (analog.getVoltage() * 312.5) / 2.54 * 1.6857;
            inches = inches*(12.5/batteryVoltage);
            temp = (temp + inches)/2;
        }

        analog.getMaxVoltage();
        inches = temp;

        switch (unit) {
            case INCH:
                return inches;
            default:
                return inches;
        }
    }



    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return null;
    }

    @Override
    public String getConnectionInfo() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {

    }

    @Override
    public void close() {

    }
}