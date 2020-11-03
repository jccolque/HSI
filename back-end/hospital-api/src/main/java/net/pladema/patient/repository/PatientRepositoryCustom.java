package net.pladema.patient.repository;

import net.pladema.patient.controller.dto.PatientSearchFilter;
import net.pladema.patient.repository.domain.PatientMedicalCoverageVo;
import net.pladema.patient.service.domain.PatientSearch;

import java.util.List;

public interface PatientRepositoryCustom {

    List<PatientSearch> getAllByOptionalFilter(PatientSearchFilter searchFilter);

    PatientMedicalCoverageVo getPatientCoverage(Integer patientMedicalCoverageId);

    List<PatientMedicalCoverageVo> getPatientCoverages(Integer patientId);

    List<PatientMedicalCoverageVo> getPatientHealthInsurances(Integer patientId);

    List<PatientMedicalCoverageVo> getPatientPrivateHealthInsurances(Integer patientId);

}
