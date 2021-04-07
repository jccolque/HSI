package net.pladema.clinichistory.documents.core.ips;

import net.pladema.clinichistory.documents.service.DocumentService;
import net.pladema.clinichistory.documents.repository.ips.AllergyIntoleranceRepository;
import net.pladema.clinichistory.documents.repository.ips.entity.AllergyIntolerance;
import net.pladema.clinichistory.documents.repository.ips.masterdata.AllergyIntoleranceClinicalStatusRepository;
import net.pladema.clinichistory.documents.repository.ips.masterdata.AllergyIntoleranceVerificationStatusRepository;
import net.pladema.clinichistory.documents.repository.ips.masterdata.entity.AllergyIntoleranceClinicalStatus;
import net.pladema.clinichistory.documents.repository.ips.masterdata.entity.AllergyIntoleranceVerificationStatus;
import net.pladema.clinichistory.documents.service.domain.PatientInfoBo;
import net.pladema.clinichistory.documents.service.ips.AllergyService;
import net.pladema.clinichistory.documents.service.ips.SnomedService;
import net.pladema.clinichistory.documents.service.ips.domain.AllergyConditionBo;
import net.pladema.snowstorm.services.CalculateCie10CodesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllergyServiceImpl implements AllergyService {

    private static final Logger LOG = LoggerFactory.getLogger(AllergyServiceImpl.class);

    public static final String OUTPUT = "Output -> {}";

    private final AllergyIntoleranceRepository allergyIntoleranceRepository;

    private final AllergyIntoleranceClinicalStatusRepository allergyClinicalStatusRepository;

    private final AllergyIntoleranceVerificationStatusRepository allergyVerificationStatusRepository;

    private final DocumentService documentService;

    private final SnomedService snomedService;

    private final CalculateCie10CodesService calculateCie10CodesService;

    public AllergyServiceImpl(AllergyIntoleranceRepository allergyIntoleranceRepository,
                              AllergyIntoleranceClinicalStatusRepository allergyClinicalStatusRepository,
                              AllergyIntoleranceVerificationStatusRepository allergyVerificationStatusRepository,
                              DocumentService documentService,
                              SnomedService snomedService,
                              CalculateCie10CodesService calculateCie10CodesService){
        this.allergyIntoleranceRepository = allergyIntoleranceRepository;
        this.allergyClinicalStatusRepository = allergyClinicalStatusRepository;
        this.allergyVerificationStatusRepository = allergyVerificationStatusRepository;
        this.documentService = documentService;
        this.snomedService = snomedService;
        this.calculateCie10CodesService = calculateCie10CodesService;
    }

    @Override
    public List<AllergyConditionBo> loadAllergies(PatientInfoBo patientInfo, Long documentId, List<AllergyConditionBo> allergies) {
        LOG.debug("Input parameters -> patientInfo {}, documentId {}, allergies {}", patientInfo, documentId, allergies);
        allergies.forEach(allergy -> {
            Integer snomedId = snomedService.getSnomedId(allergy.getSnomed())
                    .orElseGet(() -> snomedService.createSnomedTerm(allergy.getSnomed()));
            String cie10Codes = calculateCie10CodesService.execute(allergy.getSnomed().getSctid(), patientInfo);
            AllergyIntolerance allergyIntolerance = saveAllergyIntolerance(patientInfo, allergy, snomedId, cie10Codes);

            allergy.setId(allergyIntolerance.getId());
            allergy.setVerificationId(allergyIntolerance.getVerificationStatusId());
            allergy.setVerification(getVerification(allergy.getVerificationId()));
            allergy.setStatusId(allergyIntolerance.getStatusId());
            allergy.setStatus(getStatus(allergy.getStatusId()));
            allergy.setCategoryId(allergyIntolerance.getCategoryId());
            allergy.setDate(allergyIntolerance.getStartDate());

            documentService.createDocumentAllergyIntolerance(documentId, allergyIntolerance.getId());
        });
        List<AllergyConditionBo> result = allergies;
        LOG.debug(OUTPUT, result);
        return result;
    }

    private AllergyIntolerance saveAllergyIntolerance(PatientInfoBo patientInfo, AllergyConditionBo allergy, Integer snomedId, String cie10Codes) {
        LOG.debug("Input parameters -> patientInfo {}, allergy {}, snomedId {}", patientInfo, allergy, snomedId);
        AllergyIntolerance allergyIntolerance = new AllergyIntolerance(patientInfo.getId(),
                snomedId,
                cie10Codes,
                allergy.getStatusId(),
                allergy.getVerificationId(),
                allergy.getCategoryId(),
                allergy.getDate());
        allergyIntolerance = allergyIntoleranceRepository.save(allergyIntolerance);
        LOG.debug("allergyIntolerance saved -> {} ", allergyIntolerance.getId());
        LOG.debug(OUTPUT, allergyIntolerance);
        return allergyIntolerance;
    }

    private String getVerification(String id) {
        return allergyVerificationStatusRepository.findById(id).map(AllergyIntoleranceVerificationStatus::getDescription).orElse(null);
    }

    private String getStatus(String id) {
        return allergyClinicalStatusRepository.findById(id).map(AllergyIntoleranceClinicalStatus::getDescription).orElse(null);
    }

}
