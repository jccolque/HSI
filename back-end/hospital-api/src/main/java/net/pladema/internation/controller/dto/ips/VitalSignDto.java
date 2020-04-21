package net.pladema.internation.controller.dto.ips;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class VitalSignDto implements Serializable {

    private String systolicBloodPresure;

    private String diastolicBloodPresure;

    private String meanPresure;

    private String temperature;

    private String heartRate;

    private String respiratoryRate;

    private String bloodOxygenSaturation;

}
