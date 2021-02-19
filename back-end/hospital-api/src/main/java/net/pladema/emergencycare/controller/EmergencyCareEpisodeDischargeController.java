package net.pladema.emergencycare.controller;

import io.swagger.annotations.Api;
import net.pladema.clinichistory.documents.service.domain.PatientInfoBo;
import net.pladema.emergencycare.controller.dto.MedicalDischargeDto;
import net.pladema.emergencycare.controller.mapper.EmergencyCareDischargeMapper;
import net.pladema.emergencycare.repository.entity.EmergencyCareState;
import net.pladema.emergencycare.service.EmergencyCareEpisodeDischargeService;
import net.pladema.emergencycare.service.EmergencyCareEpisodeService;
import net.pladema.emergencycare.service.EmergencyCareEpisodeStateService;
import net.pladema.emergencycare.service.domain.EmergencyCareBo;
import net.pladema.patient.controller.dto.BasicPatientDto;
import net.pladema.patient.controller.service.PatientExternalService;
import net.pladema.sgx.exceptions.NotFoundException;
import net.pladema.sgx.security.utils.UserInfo;
import net.pladema.staff.controller.service.HealthcareProfessionalExternalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/institution/{institutionId}/emergency-care/episodes/{episodeId}/discharge")
@Api(value = "Emergency care Episodes Discharge", tags = {"Emergency care Episodes Discharge"})
public class EmergencyCareEpisodeDischargeController {

    private static final Logger LOG = LoggerFactory.getLogger(EmergencyCareEpisodeDischargeController.class);

    private final EmergencyCareEpisodeDischargeService emergencyCareEpisodeDischargeService;
    private final EmergencyCareDischargeMapper emergencyCareDischargeMapper;
    private final HealthcareProfessionalExternalService healthcareProfessionalExternalService;
    private final PatientExternalService patientExternalService;
    private final EmergencyCareEpisodeService emergencyCareEpisodeService;
    private final EmergencyCareEpisodeStateService emergencyCareEpisodeStateService;

    EmergencyCareEpisodeDischargeController(
            EmergencyCareEpisodeDischargeService emergencyCareEpisodeDischargeService,
            EmergencyCareDischargeMapper emergencyCareDischargeMapper,
            HealthcareProfessionalExternalService healthcareProfessionalExternalService,
            PatientExternalService patientExternalService,
            EmergencyCareEpisodeService emergencyCareEpisodeService,
            EmergencyCareEpisodeStateService emergencyCareEpisodeStateService

    ) {
        this.emergencyCareEpisodeDischargeService = emergencyCareEpisodeDischargeService;
        this.emergencyCareDischargeMapper = emergencyCareDischargeMapper;
        this.healthcareProfessionalExternalService = healthcareProfessionalExternalService;
        this.patientExternalService = patientExternalService;
        this.emergencyCareEpisodeService = emergencyCareEpisodeService;
        this.emergencyCareEpisodeStateService = emergencyCareEpisodeStateService;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<Boolean> newMedicalDischarge(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "episodeId") Integer episodeId,
            @RequestBody MedicalDischargeDto medicalDischargeDto) {
        LOG.debug("Change emergency care state -> episodeId {}, institutionId {}, medicalDischargeDto {}", episodeId, institutionId, medicalDischargeDto);

        Integer medicalDischargeBy = healthcareProfessionalExternalService.getProfessionalId(UserInfo.getCurrentAuditor());

        EmergencyCareBo emergencyCareBo = emergencyCareEpisodeService.get(episodeId, institutionId);
        if (emergencyCareBo.getPatient() == null)
            throw new NotFoundException("El episodio debe tener asociado un paciente", "El episodio debe tener asociado un paciente");

        Integer patientId = emergencyCareBo.getPatient().getId();
        BasicPatientDto patientDto = patientExternalService.getBasicDataFromPatient(patientId);
        PatientInfoBo patientInfo =  new PatientInfoBo(patientDto.getId(), patientDto.getPerson().getGender().getId(), patientDto.getPerson().getAge());
        boolean saved = emergencyCareEpisodeDischargeService.newMedicalDischarge(emergencyCareDischargeMapper.toMedicalDischargeBo(medicalDischargeDto,medicalDischargeBy,patientInfo, episodeId));
        emergencyCareEpisodeStateService.changeState(episodeId, institutionId, EmergencyCareState.CON_ALTA_MEDICA, null);
        LOG.debug("Output -> {}", saved);
        return ResponseEntity.ok().body(saved);
    }

}