package ar.lamansys.odontology.domain.consultation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class ConsultationInfoBo {

    private Integer id;

    private Integer patientId;

    private Integer institutionId;

    private Integer clinicalSpecialtyId;

    private LocalDate performedDate;

    private Integer doctorId;

    private boolean billable;

    List<ConsultationReasonBo> reasons;

    public ConsultationInfoBo(Integer id, ConsultationBo consultation, Integer doctorId, LocalDate performedDate, boolean billable) {
        this.id = id;
        this.patientId = consultation.getPatientId();
        this.institutionId = consultation.getInstitutionId();
        this.clinicalSpecialtyId = consultation.getClinicalSpecialtyId();
        this.reasons = consultation.getReasons();
        this.doctorId = doctorId;
        this.performedDate = performedDate;
        this.billable = billable;
    }

}