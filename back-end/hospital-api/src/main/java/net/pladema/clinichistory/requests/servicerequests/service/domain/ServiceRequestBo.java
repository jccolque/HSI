package net.pladema.clinichistory.requests.servicerequests.service.domain;

import ar.lamansys.sgh.clinichistory.domain.ips.DiagnosticReportBo;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentType;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.SourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ar.lamansys.sgh.clinichistory.domain.document.IDocumentBo;
import ar.lamansys.sgh.clinichistory.domain.document.PatientInfoBo;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestBo implements IDocumentBo {

    private Long id;

    private Integer serviceRequestId;

    private PatientInfoBo patientInfo;

    private String categoryId;

    private Integer medicalCoverageId;

    private Integer encounterId;

    private Integer institutionId;

    private Integer doctorId;

    private Long noteId;

    private List<DiagnosticReportBo> diagnosticReports;

    private LocalDate requestDate = LocalDate.now();

    @Override
    public Integer getPatientId() {
        if (patientInfo != null)
            return patientInfo.getId();
        return null;
    }

    @Override
    public short getDocumentType() {
        return DocumentType.ORDER;
    }

    @Override
    public Short getDocumentSource() {
        return SourceType.ORDER;
    }

}
