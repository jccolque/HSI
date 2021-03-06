package net.pladema.establishment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.pladema.address.controller.dto.AddressDto;
import net.pladema.address.controller.service.AddressExternalService;
import net.pladema.establishment.controller.dto.InstitutionAddressDto;
import net.pladema.establishment.controller.dto.InstitutionBasicInfoDto;
import net.pladema.establishment.controller.dto.InstitutionDto;
import net.pladema.establishment.controller.mapper.InstitutionMapper;
import net.pladema.establishment.repository.InstitutionRepository;
import net.pladema.establishment.repository.entity.Institution;
import net.pladema.establishment.service.domain.InstitutionBo;
import net.pladema.establishment.service.fetchInstitutions.FetchAllInstitutions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Institution", description = "Institution")
@RequestMapping("institution")
public class InstitutionController {

	private final AddressExternalService addressExternalService;
	private final InstitutionRepository repository;
	private final InstitutionMapper mapper;
	private final FetchAllInstitutions fetchAllInstitutions;
	private final Logger logger;

	public InstitutionController(InstitutionRepository repository,
								 InstitutionMapper mapper,
								 AddressExternalService addressExternalService,
								 FetchAllInstitutions fetchAllInstitutions) {
		this.repository = repository;
		this.mapper = mapper;
		this.addressExternalService = addressExternalService;
		this.fetchAllInstitutions = fetchAllInstitutions;
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	@GetMapping(params = "ids")
	public @ResponseBody
	List<InstitutionDto> getMany(@RequestParam List<Integer> ids) {
		List<Integer> allowedIds = ids;

		List<Institution> institutions = repository.findAllById(allowedIds);
		List<InstitutionDto> institutionDtoList = mapper.toListInstitutionDto(institutions);

		List<Integer> addressesIds = institutions.stream().map(Institution::getAddressId).collect(Collectors.toList());
		List<AddressDto> addresses = addressExternalService.getAddressesByIds(addressesIds);

		Map<Integer,InstitutionAddressDto> institutionAddressHashDtoMap=
				addresses.stream()
						.map(InstitutionAddressDto::new)
						.collect(Collectors.toMap(InstitutionAddressDto::getAddressId, item -> item));


		institutionDtoList.forEach(institutionDto -> institutionDto.setInstitutionAddressDto(institutionAddressHashDtoMap.get(institutionDto.getInstitutionAddressDto().getAddressId())));
		return institutionDtoList ;

	}

	@GetMapping("/all")
	public @ResponseBody
	List<InstitutionBasicInfoDto> getAll() {
		List<InstitutionBo> institutionBos = fetchAllInstitutions.run();
		var result = mapper.toListInstitutionBasicInfoDto(institutionBos);
		logger.debug("Ids results -> {}", result.stream().map(InstitutionBasicInfoDto::getId));
		logger.trace("Results -> {}", result);
		return result;
	}

}
