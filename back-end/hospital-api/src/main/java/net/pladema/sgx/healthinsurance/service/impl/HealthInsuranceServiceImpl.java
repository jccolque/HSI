package net.pladema.sgx.healthinsurance.service.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import net.pladema.person.repository.HealthInsuranceRepository;
import net.pladema.person.repository.entity.HealthInsurance;
import net.pladema.renaper.services.domain.PersonMedicalCoverageBo;
import net.pladema.sgx.healthinsurance.service.HealthInsuranceService;

import static net.pladema.sgx.healthinsurance.service.impl.HealthInsuranceRnosGenerator.calculateRnos;

@Service
public class HealthInsuranceServiceImpl implements HealthInsuranceService {


	private static final Logger LOG = LoggerFactory.getLogger(HealthInsuranceServiceImpl.class);

    private static final String OUTPUT = "output -> {}";

    private final HealthInsuranceRepository healthInsuranceRepository;

    public HealthInsuranceServiceImpl(HealthInsuranceRepository healthInsuranceRepository){
        super();
        this.healthInsuranceRepository = healthInsuranceRepository;
    }

    @Override
    public Collection<PersonMedicalCoverageBo> getAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        Collection<HealthInsurance> medicalCoveragedata = healthInsuranceRepository.findAll(sort);
        Collection<PersonMedicalCoverageBo> result = medicalCoveragedata.stream()
                .map(PersonMedicalCoverageBo::new)
                .collect(Collectors.toList());
        LOG.debug(OUTPUT, result);
        return result;
    }
    
	@Override
	public void addAll(Collection<PersonMedicalCoverageBo> newHealthInsurances) {
		LOG.debug("Input-> newHealthInsurances {}", newHealthInsurances);
		newHealthInsurances.stream().filter(hi -> !healthInsuranceRepository.existsById(calculateRnos(hi)))
				.forEach(hi -> {
					healthInsuranceRepository
							.save(new HealthInsurance(calculateRnos(hi), hi.getName(), hi.getAcronym()));
					LOG.debug("HealthInsurance Added-> newHealthInsurance {}", hi);
				});
	}
	

	
}
