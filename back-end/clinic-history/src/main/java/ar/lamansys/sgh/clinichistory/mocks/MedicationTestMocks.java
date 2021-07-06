package ar.lamansys.sgh.clinichistory.mocks;


import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.entity.DocumentMedicamentionStatement;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.ips.entity.Dosage;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.ips.entity.MedicationStatement;

import java.time.LocalDate;

public class MedicationTestMocks {

    public static MedicationStatement createMedicationStatement(Integer patientId, Integer code, String cie10Codes, String statusId,
                                                                Long noteId, Integer healthConditionId, Integer dosageId) {
        return new MedicationStatement(patientId, code, cie10Codes, statusId, noteId, healthConditionId, dosageId);
    }

    public static Dosage createDosage(Double duration, String durationUnit, Integer frequency, String periodUnit, boolean chronic,
                                      LocalDate startDate, LocalDate endDate, LocalDate suspendedStartDate, LocalDate suspendendEndDate){
        return new Dosage(duration,durationUnit, frequency, periodUnit, chronic, startDate, endDate, suspendedStartDate, suspendendEndDate);
    }
    public static DocumentMedicamentionStatement createDocumentMedicationStatement(Long documentId, Integer medicationId){
        DocumentMedicamentionStatement result = new DocumentMedicamentionStatement(documentId, medicationId);
        return result;
    }
}