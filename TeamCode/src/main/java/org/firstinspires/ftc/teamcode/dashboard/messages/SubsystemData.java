package org.firstinspires.ftc.teamcode.dashboard.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "subsystemType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DrivetrainData.class, name = "drivetrain"),
    @JsonSubTypes.Type(value = IntakeData.class, name = "intake"),
    @JsonSubTypes.Type(value = DepositData.class, name = "deposit"),
    @JsonSubTypes.Type(value = CameraData.class, name = "camera"),
    @JsonSubTypes.Type(value = GeneralData.class, name = "general")
})
public abstract class SubsystemData {
}