package ar.lamansys.immunization.infrastructure.input.rest.dto;

import ar.lamansys.immunization.domain.vaccine.VaccineSchemeBo;
import ar.lamansys.immunization.domain.vaccine.conditionapplication.VaccineConditionApplicationBo;
import ar.lamansys.immunization.domain.vaccine.doses.VaccineDoseBo;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class VaccineConditionsDto {

    private final Short id;

    private final String description;

    private List<VaccineSchemeDto> schemes;

    public VaccineConditionsDto(VaccineConditionApplicationBo vaccineConditionApplicationBo,
                                VaccineSchemeBo vaccineSchemeBo,
                                VaccineDoseBo vaccineDoseBo) {
        this.id = vaccineConditionApplicationBo.getId();
        this.description = vaccineConditionApplicationBo.getDescription();
        this.schemes = new ArrayList<>(Collections.singletonList(new VaccineSchemeDto(vaccineSchemeBo, vaccineDoseBo)));
    }

    public void add(VaccineSchemeBo vaccineSchemeBo, VaccineDoseBo vaccineDoseBo) {
        this.schemes.stream().filter(scheme -> scheme.getId().equals(vaccineSchemeBo.getId()))
                .findFirst()
                .ifPresentOrElse(
                        scheme ->  scheme.add(vaccineDoseBo),
                        () -> schemes.add(new VaccineSchemeDto(vaccineSchemeBo, vaccineDoseBo)));
    }
}