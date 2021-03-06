package net.pladema.clinichistory.hospitalization.service.anamnesis.impl;

import ar.lamansys.sgh.clinichistory.application.createDocument.DocumentFactory;
import ar.lamansys.sgh.clinichistory.domain.ips.ClinicalTermsValidatorUtils;
import ar.lamansys.sgh.clinichistory.domain.ips.DiagnosisBo;
import ar.lamansys.sgh.clinichistory.domain.ips.SnomedBo;
import ar.lamansys.sgx.shared.dates.configuration.DateTimeProvider;
import ar.lamansys.sgx.shared.featureflags.AppFeature;
import net.pladema.clinichistory.hospitalization.repository.domain.InternmentEpisode;
import net.pladema.clinichistory.hospitalization.service.InternmentEpisodeService;
import net.pladema.clinichistory.hospitalization.service.anamnesis.CreateAnamnesisService;
import net.pladema.clinichistory.hospitalization.service.anamnesis.domain.AnamnesisBo;
import net.pladema.clinichistory.hospitalization.service.documents.validation.AnthropometricDataValidator;
import net.pladema.clinichistory.hospitalization.service.documents.validation.EffectiveRiskFactorTimeValidator;
import ar.lamansys.sgx.shared.featureflags.application.FeatureFlagsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class CreateAnamnesisServiceImpl implements CreateAnamnesisService {

    private static final Logger LOG = LoggerFactory.getLogger(CreateAnamnesisServiceImpl.class);

    public static final String OUTPUT = "Output -> {}";

    private final DocumentFactory documentFactory;

    private final InternmentEpisodeService internmentEpisodeService;

    private final FeatureFlagsService featureFlagsService;

    private final DateTimeProvider dateTimeProvider;

    public CreateAnamnesisServiceImpl(DocumentFactory documentFactory,
                                      InternmentEpisodeService internmentEpisodeService,
                                      FeatureFlagsService featureFlagsService,
                                      DateTimeProvider dateTimeProvider) {
        this.documentFactory = documentFactory;
        this.internmentEpisodeService = internmentEpisodeService;
        this.featureFlagsService = featureFlagsService;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    @Transactional
    public AnamnesisBo execute(AnamnesisBo anamnesis) {
        LOG.debug("Input parameters -> anamnesis {}", anamnesis);

        assertContextValid(anamnesis);
        var internmentEpisode = internmentEpisodeService
                .getInternmentEpisode(anamnesis.getEncounterId(), anamnesis.getInstitutionId());

        LocalDateTime now = dateTimeProvider.nowDateTime();
        anamnesis.setPerformedDate(now);

        anamnesis.setPatientId(internmentEpisode.getPatientId());

        assertAnamnesisValid(anamnesis);
        assertDoesNotHaveAnamnesis(internmentEpisode);
        assertEffectiveRiskFactorTimeValid(anamnesis, internmentEpisode.getEntryDate());
        assertAnthropometricData(anamnesis);

        
        anamnesis.setId(documentFactory.run(anamnesis, true));

        internmentEpisodeService.updateAnamnesisDocumentId(anamnesis.getEncounterId(), anamnesis.getId());
        LOG.debug(OUTPUT, anamnesis);

        return anamnesis;
    }

    private void assertContextValid(AnamnesisBo anamnesis) {
        if (anamnesis.getInstitutionId() == null)
            throw new ConstraintViolationException("El id de la instituci??n es obligatorio", Collections.emptySet());
        if (anamnesis.getEncounterId() == null)
            throw new ConstraintViolationException("El id del encuentro asociado es obligatorio", Collections.emptySet());
    }

    private void assertAnamnesisValid(AnamnesisBo anamnesis) {
        anamnesis.validateSelf();
        assertMainDiagnosisValid(anamnesis);
        if (ClinicalTermsValidatorUtils.repeatedClinicalTerms(anamnesis.getDiagnosis()))
            throw new ConstraintViolationException("Diagn??sticos secundarios repetidos", Collections.emptySet());
        if (ClinicalTermsValidatorUtils.repeatedClinicalTerms(anamnesis.getPersonalHistories()))
            throw new ConstraintViolationException("Antecedentes personales repetidos", Collections.emptySet());
        if (ClinicalTermsValidatorUtils.repeatedClinicalTerms(anamnesis.getFamilyHistories()))
            throw new ConstraintViolationException("Antecedentes familiares repetidos", Collections.emptySet());
        if (ClinicalTermsValidatorUtils.repeatedClinicalTerms(anamnesis.getProcedures()))
            throw new ConstraintViolationException("Procedimientos repetidos", Collections.emptySet());
    }

    private void assertEffectiveRiskFactorTimeValid(AnamnesisBo anamnesis, LocalDateTime entryDate) {
        var validator = new EffectiveRiskFactorTimeValidator();
        validator.isValid(anamnesis, entryDate);
    }

    private void assertAnthropometricData(AnamnesisBo anamnesis) {
        var validator = new AnthropometricDataValidator();
        validator.isValid(anamnesis);
    }

    private void assertMainDiagnosisValid(AnamnesisBo anamnesis) {
        if (featureFlagsService.isOn(AppFeature.MAIN_DIAGNOSIS_REQUIRED)
                && anamnesis.getMainDiagnosis() == null)
            throw new ConstraintViolationException("Diagn??stico principal obligatorio", Collections.emptySet());
        if (anamnesis.getMainDiagnosis() == null)
            return;
        if (anamnesis.getAlternativeDiagnosis() == null)
            return;
        SnomedBo snomedMainDiagnosis = anamnesis.getMainDiagnosis().getSnomed();
        if(anamnesis.getAlternativeDiagnosis().stream()
                .map(DiagnosisBo::getSnomed)
                .anyMatch(d -> d.equals(snomedMainDiagnosis))){
            throw new ConstraintViolationException("Diagnostico principal duplicado en los secundarios", Collections.emptySet());
        }
    }

    private void assertDoesNotHaveAnamnesis(InternmentEpisode internmentEpisode) {
        if(internmentEpisode.getAnamnesisDocId() != null) {
            throw new ConstraintViolationException("Esta internaci??n ya posee una anamnesis", Collections.emptySet());
        }
    }

}
