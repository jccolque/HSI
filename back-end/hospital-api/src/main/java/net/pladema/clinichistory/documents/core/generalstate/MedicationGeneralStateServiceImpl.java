package net.pladema.clinichistory.documents.core.generalstate;

import net.pladema.clinichistory.documents.service.generalstate.MedicationGeneralStateService;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.generalstate.HCHMedicationStatementRepository;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.generalstate.entity.MedicationVo;
import ar.lamansys.sgh.clinichistory.domain.ips.MedicationBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicationGeneralStateServiceImpl implements MedicationGeneralStateService {

    private static final Logger LOG = LoggerFactory.getLogger(MedicationGeneralStateServiceImpl.class);

    public static final String OUTPUT = "Output -> {}";

    private final HCHMedicationStatementRepository hchMedicationStatementRepository;

    public MedicationGeneralStateServiceImpl(HCHMedicationStatementRepository hchMedicationStatementRepository){
        this.hchMedicationStatementRepository = hchMedicationStatementRepository;
    }

    @Override
    public List<MedicationBo> getMedicationsGeneralState(Integer internmentEpisodeId) {
        LOG.debug("Input parameters -> internmentEpisodeId {}", internmentEpisodeId);
        List<MedicationVo> resultQuery = hchMedicationStatementRepository.findGeneralState(internmentEpisodeId);
        List<MedicationBo> result = resultQuery.stream().map(MedicationBo::new).collect(Collectors.toList());
        LOG.debug(OUTPUT, result);
        return result;
    }

}
