package net.pladema.clinichistory.hospitalization.controller.documents.anamnesis;

import io.swagger.annotations.Api;
import net.pladema.clinichistory.documents.events.exceptions.GenerateDocumentEventException;
import net.pladema.clinichistory.documents.repository.ips.masterdata.entity.DocumentType;
import net.pladema.clinichistory.documents.service.domain.PatientInfoBo;
import net.pladema.clinichistory.hospitalization.controller.constraints.DocumentValid;
import net.pladema.clinichistory.hospitalization.controller.constraints.InternmentValid;
import net.pladema.clinichistory.hospitalization.controller.documents.anamnesis.dto.AnamnesisDto;
import net.pladema.clinichistory.hospitalization.controller.documents.anamnesis.dto.ResponseAnamnesisDto;
import net.pladema.clinichistory.hospitalization.controller.documents.anamnesis.mapper.AnamnesisMapper;
import net.pladema.clinichistory.hospitalization.service.InternmentEpisodeService;
import net.pladema.clinichistory.hospitalization.service.anamnesis.AnamnesisService;
import net.pladema.clinichistory.hospitalization.service.anamnesis.CreateAnamnesisService;
import net.pladema.clinichistory.hospitalization.service.anamnesis.domain.AnamnesisBo;
import net.pladema.patient.controller.service.PatientExternalService;
import ar.lamansys.sgx.shared.exceptions.dto.ApiErrorDto;
import net.pladema.sgx.exceptions.NotFoundException;
import net.pladema.sgx.pdf.PDFDocumentException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/institutions/{institutionId}/internments/{internmentEpisodeId}/anamnesis")
@Api(value = "Anamnesis", tags = { "Anamnesis" })
@Validated
public class AnamnesisController {

    private static final Logger LOG = LoggerFactory.getLogger(AnamnesisController.class);
    public static final String OUTPUT = "Output -> {}";

    private final InternmentEpisodeService internmentEpisodeService;

    private final CreateAnamnesisService createAnamnesisService;

    private final AnamnesisService anamnesisService;

    private final AnamnesisMapper anamnesisMapper;

    private final MessageSource messageSource;

    private final PatientExternalService patientExternalService;

    public AnamnesisController(InternmentEpisodeService internmentEpisodeService,
                               CreateAnamnesisService createAnamnesisService,
                               AnamnesisService anamnesisService,
                               AnamnesisMapper anamnesisMapper,
                               MessageSource messageSource,
                               PatientExternalService patientExternalService) {
        this.internmentEpisodeService = internmentEpisodeService;
        this.createAnamnesisService = createAnamnesisService;
        this.anamnesisService = anamnesisService;
        this.anamnesisMapper = anamnesisMapper;
        this.messageSource = messageSource;
        this.patientExternalService = patientExternalService;
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, ENFERMERO_ADULTO_MAYOR')")
    public ResponseEntity<Boolean> createAnamnesis(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId,
            @RequestBody AnamnesisDto anamnesisDto) {
        LOG.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, ananmnesis {}",
                institutionId, internmentEpisodeId, anamnesisDto);
        AnamnesisBo anamnesis = anamnesisMapper.fromAnamnesisDto(anamnesisDto);
        anamnesis.setEncounterId(internmentEpisodeId);
        anamnesis.setInstitutionId(institutionId);

        internmentEpisodeService.getPatient(internmentEpisodeId)
                .map(patientExternalService::getBasicDataFromPatient)
                .map(patientDto -> new PatientInfoBo(patientDto.getId(), patientDto.getPerson().getGender().getId(), patientDto.getPerson().getAge()))
                .ifPresentOrElse(anamnesis::setPatientInfo, () -> new NotFoundException("El paciente no existe", "El paciente no existe"));

        createAnamnesisService.execute(anamnesis);

        LOG.debug(OUTPUT, Boolean.TRUE);
        return  ResponseEntity.ok().body(Boolean.TRUE);
    }


    @GetMapping("/{anamnesisId}")
    @InternmentValid
    @DocumentValid(isConfirmed = false, documentType = DocumentType.ANAMNESIS)
    @PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, ENFERMERO_ADULTO_MAYOR')")
    public ResponseEntity<ResponseAnamnesisDto> getAnamnesis(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId,
            @PathVariable(name = "anamnesisId") Long anamnesisId){
        LOG.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, anamnesisId {}",
                institutionId, internmentEpisodeId, anamnesisId);
        AnamnesisBo anamnesis = anamnesisService.getDocument(anamnesisId);
        ResponseAnamnesisDto result = anamnesisMapper.fromAnamnesis(anamnesis);
        LOG.debug(OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ ConstraintViolationException.class })
    public ApiErrorDto handleValidationExceptions(ConstraintViolationException ex, Locale locale) {
        List<String> errors = new ArrayList<>();
        if (ex.getConstraintViolations().isEmpty()) {
            String msg = ex.getMessage();
            try {
                String property = msg.substring(0, msg.indexOf(":"));
                msg = property + ": " + messageSource.getMessage(StringUtils.substringBetween(msg, "{", "}"), null, locale);
            } catch (Exception e) {
                LOG.error("No se tiene un mensaje para la siguiente clave -> {}", msg);
            }
            LOG.debug("Constraint validation error -> {}", msg);
            return new ApiErrorDto("Constraint violation", List.of(msg));
        }
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()){

            if(violation.getPropertyPath().toString().contains("<cross-parameter>"))
                errors.add(violation.getMessage());
            else
                errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }
        return new ApiErrorDto("Constraint violation", errors);
    }

}