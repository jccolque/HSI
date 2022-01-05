package net.pladema.patient.service.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.pladema.patient.controller.dto.CoverageDto;
import net.pladema.patient.controller.dto.PrivateHealthInsuranceDto;
import net.pladema.patient.repository.entity.PrivateHealthInsurance;

@Getter
@Setter
@NoArgsConstructor
public class PrivateHealthInsuranceBo extends MedicalCoverageBo {

    private String plan;

    public PrivateHealthInsuranceBo(Integer id, String name, String cuit, String plan){
        setId(id);
        setName(name);
        setCuit(cuit);
        this.plan= plan;
    }

    @Override
    public CoverageDto newInstance() {
        return new PrivateHealthInsuranceDto(getId(), getName(), getCuit(), getPlan());
    }

    @Override
    public PrivateHealthInsurance mapToEntity() {
        return new PrivateHealthInsurance(getId(), getName(), getCuit(), getPlan());
    }
}
