package net.pladema.emergencycare.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.pladema.clinichistory.hospitalization.controller.generalstate.dto.SnomedDto;
import net.pladema.medicalconsultation.doctorsoffice.controller.dto.DoctorsOfficeDto;
import ar.lamansys.sgx.shared.dates.controller.dto.DateTimeDto;
import net.pladema.sgx.masterdata.dto.MasterDataDto;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEmergencyCareDto extends EmergencyCareDto {

    Integer id;

    MasterDataDto emergencyCareState;

    DateTimeDto creationDate;

    DoctorsOfficeDto doctorsOffice;

    public ResponseEmergencyCareDto(Integer id, List<SnomedDto> reasons, MasterDataDto emergencyCareType,
                                    MasterDataDto entranceType, Boolean hasPoliceIntervention, PoliceInterventionDetailsDto policeIntervention,
                                    String ambulanceCompanyId, EmergencyCarePatientDto patient,
                                    MasterDataDto emergencyCareState, DateTimeDto createdOn){
        super(reasons, emergencyCareType, entranceType, hasPoliceIntervention, policeIntervention, ambulanceCompanyId, patient);
        this.id = id;
        this.emergencyCareState = emergencyCareState;
        this.creationDate = createdOn;
    }

}
