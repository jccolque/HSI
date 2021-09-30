package ar.lamansys.odontology.infrastructure.controller.consultation;

import ar.lamansys.odontology.application.createConsultation.CreateOdontologyConsultation;
import ar.lamansys.odontology.application.fetchCpoCeoIndices.FetchCpoCeoIndices;
import ar.lamansys.odontology.domain.consultation.ConsultationBo;
import ar.lamansys.odontology.domain.consultation.CpoCeoIndicesBo;
import ar.lamansys.odontology.infrastructure.controller.consultation.dto.OdontologyConsultationIndicesDto;
import ar.lamansys.odontology.infrastructure.controller.consultation.dto.OdontologyConsultationDto;
import ar.lamansys.odontology.infrastructure.controller.consultation.mapper.CpoCeoIndicesMapper;
import ar.lamansys.odontology.infrastructure.controller.consultation.mapper.OdontologyConsultationMapper;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping("/institutions/{institutionId}/patient/{patientId}/odontology/consultation")
@Api(value="Odontology consultation", tags= { "Odontology Consultation" } )
public class OdontologyConsultationController {

    private static final Logger LOG = LoggerFactory.getLogger(OdontologyConsultationController.class);

    private final CreateOdontologyConsultation createOdontologyConsultation;

    private final OdontologyConsultationMapper odontologyConsultationMapper;

    private final FetchCpoCeoIndices fetchCpoCeoIndices;

    private final CpoCeoIndicesMapper cpoCeoIndicesMapper;

    public OdontologyConsultationController(CreateOdontologyConsultation createOdontologyConsultation,
                                            OdontologyConsultationMapper odontologyConsultationMapper,
                                            FetchCpoCeoIndices fetchCpoCeoIndices,
                                            CpoCeoIndicesMapper cpoCeoIndicesMapper) {
        this.createOdontologyConsultation = createOdontologyConsultation;
        this.odontologyConsultationMapper = odontologyConsultationMapper;
        this.fetchCpoCeoIndices = fetchCpoCeoIndices;
        this.cpoCeoIndicesMapper = cpoCeoIndicesMapper;
    }

    @Transactional
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping
    public boolean createConsultation(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "patientId")  Integer patientId,
            @RequestBody @Valid OdontologyConsultationDto consultationDto) {
        LOG.debug("Input parameters -> institutionId {}, patientId {}, odontologyConsultationDto {}", institutionId, patientId, consultationDto);

        ConsultationBo consultationBo = odontologyConsultationMapper.fromOdontologyConsultationDto(consultationDto);
        consultationBo.setInstitutionId(institutionId);
        consultationBo.setPatientId(patientId);

        createOdontologyConsultation.run(consultationBo);
        return true;
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/indices")
    public List<OdontologyConsultationIndicesDto> getConsultationIndices(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "patientId")  Integer patientId) {
        LOG.debug("Input parameters -> institutionId {}, patientId {}", institutionId, patientId);
        List<CpoCeoIndicesBo> cpoCeoIndicesBoList = fetchCpoCeoIndices.run(patientId);
        List<OdontologyConsultationIndicesDto> result = cpoCeoIndicesMapper.fromCpoCeoIndicesBoList(cpoCeoIndicesBoList);
        LOG.debug("Output -> {}", result);
        return result;
    }
}
