package net.pladema.clinichistory.documents.core.ips;

import net.pladema.clinichistory.documents.repository.ips.DiagnosticReportRepository;
import net.pladema.clinichistory.documents.repository.ips.entity.DiagnosticReport;
import net.pladema.clinichistory.documents.service.DocumentService;
import net.pladema.clinichistory.documents.service.NoteService;
import net.pladema.clinichistory.documents.service.domain.PatientInfoBo;
import net.pladema.clinichistory.documents.service.ips.DiagnosticReportService;
import net.pladema.clinichistory.documents.service.ips.SnomedService;
import net.pladema.clinichistory.requests.servicerequests.service.domain.DiagnosticReportBo;
import net.pladema.snowstorm.services.CalculateCie10CodesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiagnosticReportServiceImpl implements DiagnosticReportService {

    private static final Logger LOG = LoggerFactory.getLogger(DiagnosticReportServiceImpl.class);

    public static final String OUTPUT = "Output -> {}";

    private final DiagnosticReportRepository diagnosticReportRepository;
    private final DocumentService documentService;
    private final NoteService noteService;
    private final SnomedService snomedService;
    private final CalculateCie10CodesService calculateCie10CodesService;

    public DiagnosticReportServiceImpl(DiagnosticReportRepository diagnosticReportRepository,
                                       DocumentService documentService,
                                       NoteService noteService,
                                       SnomedService snomedService,
                                       CalculateCie10CodesService calculateCie10CodesService) {
        this.diagnosticReportRepository = diagnosticReportRepository;
        this.documentService = documentService;
        this.noteService = noteService;
        this.snomedService = snomedService;
        this.calculateCie10CodesService = calculateCie10CodesService;
    }

    public List<Integer> loadDiagnosticReport(Long documentId, PatientInfoBo patientInfo, List<DiagnosticReportBo> diagnosticReportBos) {
        LOG.debug("Input parameters -> documentId {}, patientInfo {}, studyBo {}", documentId, patientInfo, diagnosticReportBos);
        List<Integer> result = new ArrayList<>();
        diagnosticReportBos.forEach(diagnosticReportBo -> {
            DiagnosticReport diagnosticReport = getNewDiagnosticReport(patientInfo, diagnosticReportBo);
            result.add(diagnosticReportRepository.save(diagnosticReport).getId());
            diagnosticReportRepository.save(diagnosticReport);
            documentService.createDocumentDiagnosticReport(documentId, diagnosticReport.getId());
        });
        LOG.trace(OUTPUT, result);
        return result;
    }

    private DiagnosticReport getNewDiagnosticReport(PatientInfoBo patientInfo, DiagnosticReportBo diagnosticReportBo) {
        DiagnosticReport result = new DiagnosticReport();

        Integer snomedId = snomedService.getSnomedId(diagnosticReportBo.getSnomed())
                .orElseGet(() -> snomedService.createSnomedTerm(diagnosticReportBo.getSnomed()));
        String cie10Codes = calculateCie10CodesService.execute(diagnosticReportBo.getSnomed().getSctid(), patientInfo);

        result.setPatientId(patientInfo.getId());

        result.setSnomedId(snomedId);
        result.setCie10Codes(cie10Codes);
        result.setHealthConditionId(diagnosticReportBo.getHealthConditionId());

        result.setNoteId(generateNoteId(diagnosticReportBo.getNoteId(), diagnosticReportBo.getObservations()));

        result.setLink(diagnosticReportBo.getLink());

        if (diagnosticReportBo.getStatusId() != null) {
            result.setStatusId(diagnosticReportBo.getStatusId());
        }
        LOG.debug(OUTPUT, result);
        return result;
    }

    public Long generateNoteId(Long noteId, String observations){
        if (noteId != null) {
            return noteId;
        } else {
            return observations != null ?  noteService.createNote(observations) : null;
        }
    }

}
