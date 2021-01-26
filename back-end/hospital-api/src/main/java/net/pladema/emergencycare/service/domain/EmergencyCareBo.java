package net.pladema.emergencycare.service.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.pladema.clinichistory.outpatient.createoutpatient.service.domain.ReasonBo;
import net.pladema.emergencycare.repository.domain.EmergencyCareVo;
import net.pladema.emergencycare.repository.entity.EmergencyCareEpisode;
import net.pladema.emergencycare.triage.service.domain.TriageBo;
import net.pladema.medicalconsultation.doctorsoffice.service.domain.DoctorsOfficeBo;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyCareBo {

    private Integer id;

    private String firstname;

    private String lastname;

    private Integer patientId;

    private Integer institutionId;

    private Short triageCategoryId;

    private String triageName;

    private String triageColorCode;

    private Short emergencyCareType;

    private Short emergencyCareState;

    private Short emergencyCareEntrance;

    private DoctorsOfficeBo doctorsOffice;

    private LocalDateTime createdOn;

    private Integer patientMedicalCoverageId;

    private List<ReasonBo> reasons;

    private TriageBo triage;

    private String ambulanceCompanyId;

    private PoliceInterventionBo policeIntervention;

    public EmergencyCareBo(EmergencyCareVo emergencyCareVo){
        this.id = emergencyCareVo.getId();
        this.firstname = emergencyCareVo.getFirstname();
        this.lastname = emergencyCareVo.getLastname();
        this.patientId = emergencyCareVo.getPatientId();
        this.triageCategoryId = emergencyCareVo.getTriageCategoryId();
        this.triageName = emergencyCareVo.getTriageName();
        this.triageColorCode = emergencyCareVo.getTriageColorCode();
        this.institutionId = emergencyCareVo.getInstitutionId();
        this.emergencyCareType = emergencyCareVo.getEmergencyCareTypeId();
        this.emergencyCareState = emergencyCareVo.getEmergencyCareStateId();
        this.emergencyCareEntrance = emergencyCareVo.getEmergencyCareEntranceTypeId();
        this.doctorsOffice = emergencyCareVo.getDoctorsOffice() != null ? new DoctorsOfficeBo(emergencyCareVo.getDoctorsOffice()) : null;
        this.ambulanceCompanyId = emergencyCareVo.getAmbulanceCompanyId();
        if (emergencyCareVo.getPoliceIntervention()!= null)
            this.policeIntervention = new PoliceInterventionBo(emergencyCareVo.getPoliceIntervention());
        this.createdOn = emergencyCareVo.getCreatedOn();
    }

    public EmergencyCareBo(EmergencyCareEpisode emergencyCareEpisode) {
        this.id = emergencyCareEpisode.getId();
        this.patientId = emergencyCareEpisode.getPatientId();
        this.institutionId = emergencyCareEpisode.getInstitutionId();
        this.patientMedicalCoverageId = emergencyCareEpisode.getPatientMedicalCoverageId();
        this.emergencyCareEntrance = emergencyCareEpisode.getEmergencyCareEntranceTypeId();
        this.emergencyCareType = emergencyCareEpisode.getEmergencyCareTypeId();
        this.emergencyCareState = emergencyCareEpisode.getEmergencyCareStateId();
        this.ambulanceCompanyId = emergencyCareEpisode.getAmbulanceCompanyId();
    }

    public void setTriageVitalSignIds(List<Integer> vitalSignIds) {
        this.getTriage().setVitalSignIds(vitalSignIds);
    }

    public void setProfessionalId(Integer professionalId) {
        this.getTriage().setProfessionalId(professionalId);
    }

}
