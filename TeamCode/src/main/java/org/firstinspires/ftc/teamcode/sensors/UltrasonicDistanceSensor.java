package org.firstinspires.ftc.teamcode.sensors;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class UltrasonicDistanceSensor implements DistanceSensor {

    private final AnalogInput analog;

    public static double MAX_RANGE = 120.0;

    public static double MIN_RANGE = 5;

    public UltrasonicDistanceSensor(AnalogInput analog) {
        this.analog = analog;
    }

    @Override
    public double getDistance(DistanceUnit unit) {
        double inches=0;
        double temp=0;

        for(int i=0; i<11; i++) {
            inches = (analog.getVoltage() * 312.5) / 2.54 * 1.6857;
            temp = (temp + inches)/2;
        }

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