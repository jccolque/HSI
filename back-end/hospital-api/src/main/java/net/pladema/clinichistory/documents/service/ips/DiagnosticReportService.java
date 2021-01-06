package net.pladema.clinichistory.documents.service.ips;

import net.pladema.clinichistory.documents.repository.ips.entity.DiagnosticReport;
import net.pladema.clinichistory.documents.service.domain.PatientInfoBo;
import net.pladema.clinichistory.requests.servicerequests.service.domain.DiagnosticReportBo;

import java.util.List;

public interface DiagnosticReportService {
    List<DiagnosticReport> loadDiagnosticReport(Long documentId, PatientInfoBo patientInfo, List<DiagnosticReportBo> studyBos);
}
