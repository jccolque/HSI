package ar.lamansys.sgh.clinichistory.infrastructure.input.service;

import java.util.List;
import java.util.stream.Collectors;

import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentIndicationRepository;

import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.entity.DocumentIndication;

import ar.lamansys.sgx.shared.dates.configuration.LocalDateMapper;

import org.springframework.stereotype.Service;

import ar.lamansys.sgh.clinichistory.application.indication.creatediet.CreateDiet;
import ar.lamansys.sgh.clinichistory.application.indication.getinternmentepisodediets.GetInternmentEpisodeDiets;
import ar.lamansys.sgh.clinichistory.domain.ips.DietBo;
import ar.lamansys.sgh.shared.infrastructure.input.service.DietDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.SharedIndicationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SharedIndicationPortImpl implements SharedIndicationPort {

	private final GetInternmentEpisodeDiets getInternmentEpisodeDiets;

	private final CreateDiet createDiet;

	private final DocumentIndicationRepository documentIndicationRepository;

	private final LocalDateMapper localDateMapper;

	@Override
	public List<DietDto> getInternmentEpisodeDiets(Integer internmentEpisodeId) {
		log.debug("Input parameter -> internmentEpisodeId {}", internmentEpisodeId);
		List<DietDto> result = getInternmentEpisodeDiets.run(internmentEpisodeId)
				.stream()
				.map(this::mapToDietDto)
				.collect(Collectors.toList());
		log.debug("Output -> {}", result);
		return result;
	}

	@Override
	public Integer addDiet(DietDto dietDto) {
		log.debug("Input parameter -> dietDto {}", dietDto);
		Integer result = createDiet.run(mapToDietBo(dietDto));
		log.debug("Output -> {}", result);
		return result;
	}

	@Override
	public 	void saveDocument(Long id, Integer indicationId){
		log.debug("Input parameter -> id {}, indicationId {}", id, indicationId);
		documentIndicationRepository.save(new DocumentIndication(id,indicationId));
	}

	private DietBo mapToDietBo(DietDto dto){
		return new DietBo(
				dto.getId(),
				dto.getPatientId(),
				dto.getType().getId(),
				dto.getStatus().getId(),
				null,
				dto.getProfessionalId(),
				localDateMapper.fromDateDto(dto.getIndicationDate()),
				localDateMapper.fromDateTimeDto(dto.getCreatedOn()),
				dto.getDescription());
	}

	private DietDto mapToDietDto(DietBo bo){
		return new DietDto(
				bo.getId(),
				bo.getPatientId(),
				bo.getTypeId(),
				bo.getStatusId(),
				bo.getProfessionalId(),
				bo.getCreatedByName(),
				localDateMapper.toDateDto(bo.getIndicationDate()),
				localDateMapper.toDateTimeDto(bo.getCreatedOn()),
				bo.getDescription());
	}
}