package net.pladema.establishment.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.establishment.repository.MedicalCoveragePlanRepository;
import net.pladema.patient.controller.dto.BackofficeCoverageDto;
import net.pladema.patient.controller.dto.EMedicalCoverageType;
import net.pladema.patient.repository.MedicalCoverageRepository;
import net.pladema.patient.repository.PatientMedicalCoverageRepository;
import net.pladema.patient.repository.PrivateHealthInsuranceDetailsRepository;
import net.pladema.patient.repository.PrivateHealthInsuranceRepository;
import net.pladema.patient.repository.entity.MedicalCoverage;
import net.pladema.person.repository.HealthInsuranceRepository;
import net.pladema.sgx.backoffice.repository.BackofficeStore;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BackofficeMedicalCoverageMergeStore implements BackofficeStore<BackofficeCoverageDto, Integer> {

    private final MedicalCoverageRepository medicalCoverageRepository;

    private final PrivateHealthInsuranceRepository privateHealthInsuranceRepository;

    private final MedicalCoveragePlanRepository medicalCoveragePlanRepository;

    private final PrivateHealthInsuranceDetailsRepository privateHealthInsuranceDetailsRepository;

    private final HealthInsuranceRepository healthInsuranceRepository;

    private final PatientMedicalCoverageRepository patientMedicalCoverageRepository;

    @Override
    public Page<BackofficeCoverageDto> findAll(BackofficeCoverageDto coverage, Pageable pageable) {
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        List<BackofficeCoverageDto> result = medicalCoverageRepository.findAll(Example.of(new MedicalCoverage(coverage.getId(), coverage.getName(),coverage.getCuit()), customExampleMatcher)).stream()
                .map(this::mapToDto)
                .filter(backofficeCoverageDto -> backofficeCoverageDto.getCuit()==null&&backofficeCoverageDto.getEnabled())
                .collect(Collectors.toList());
        if(coverage.getType()!=null)
            result = result.stream().filter(backofficeCoverage -> backofficeCoverage.getType().equals(coverage.getType())).collect(Collectors.toList());
        int minIndex = pageable.getPageNumber() * pageable.getPageSize();
        int maxIndex = minIndex + pageable.getPageSize();
        return new PageImpl<>(result.subList(minIndex, Math.min(maxIndex, result.size())), pageable, result.size());
    }

    @Override
    public List<BackofficeCoverageDto> findAll() {
        return medicalCoverageRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BackofficeCoverageDto> findAllById(List<Integer> ids) {
        return medicalCoverageRepository.findAllById(ids).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BackofficeCoverageDto> findById(Integer id) {
        return medicalCoverageRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    public BackofficeCoverageDto save(BackofficeCoverageDto dto) {
        return null;
    }



    @Override
    public void deleteById(Integer id) {
    }

    @Override
    public Example<BackofficeCoverageDto> buildExample(BackofficeCoverageDto entity) {
        return Example.of(entity);
    }

    private BackofficeCoverageDto mapToDto(MedicalCoverage entity) {
        return privateHealthInsuranceRepository.findById(entity.getId()).map(insurance -> new BackofficeCoverageDto(entity.getId(), entity.getName(), entity.getCuit(),EMedicalCoverageType.PREPAGA.getId(), null, null, !insurance.isDeleted()))
                .orElseGet(() -> healthInsuranceRepository.findById(entity.getId())
                        .map(healthInsurance -> new BackofficeCoverageDto(entity.getId(), entity.getName(), entity.getCuit(),EMedicalCoverageType.OBRASOCIAL.getId(), healthInsurance.getRnos(), healthInsurance.getAcronym(), !healthInsurance.isDeleted()))
                        .orElse(new BackofficeCoverageDto(entity.getId(), entity.getName(), entity.getCuit(),EMedicalCoverageType.OBRASOCIAL.getId(), null,null, true)));
    }

    public BackofficeCoverageDto merge(Integer id, Integer baseMedicalCoverageId){
		patientMedicalCoverageRepository.getByMedicalCoverageId(id)
				.forEach(pmc -> {
					pmc.setMedicalCoverageId(baseMedicalCoverageId);
					patientMedicalCoverageRepository.save(pmc);
				});
		if(!privateHealthInsuranceRepository.existsById(id))
            healthInsuranceRepository.deleteById(id);
        else {
			medicalCoveragePlanRepository.findByMedicalCoverageId(id).forEach(plan -> {
				patientMedicalCoverageRepository.findByPlanId(plan.getId()).forEach(pmc -> {
					pmc.setPlanId(null);
					patientMedicalCoverageRepository.save(pmc);
				});
				privateHealthInsuranceDetailsRepository.findAllByPlanId(plan.getId())
						.forEach(phid -> {
							phid.setPlanId(null);
							privateHealthInsuranceDetailsRepository.save(phid);
						});
				medicalCoveragePlanRepository.deleteById(plan.getId());
			});
			privateHealthInsuranceRepository.deleteById(id);
		}
        medicalCoverageRepository.deleteMergedCoverage(id);
        return findById(baseMedicalCoverageId).get();
    }

}
