package net.pladema.internation.service.domain.ips;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class AnthropometricDataBo implements Serializable {

    private ClinicalObservation bloodType;

    private ClinicalObservation height;

    private ClinicalObservation weight;

    public boolean wasDeleted() {
        return bloodType.isDeleted() || height.isDeleted() || weight.isDeleted();
    }
}
