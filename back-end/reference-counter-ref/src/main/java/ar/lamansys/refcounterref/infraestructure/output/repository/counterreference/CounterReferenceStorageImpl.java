package ar.lamansys.refcounterref.infraestructure.output.repository.counterreference;

import ar.lamansys.refcounterref.application.port.CounterReferenceStorage;
import ar.lamansys.refcounterref.application.port.ReferenceCounterReferenceFileStorage;
import ar.lamansys.refcounterref.domain.counterreference.CounterReferenceInfoBo;
import ar.lamansys.refcounterref.domain.counterreference.CounterReferenceSummaryBo;
import ar.lamansys.refcounterref.domain.procedure.CounterReferenceProcedureBo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CounterReferenceStorageImpl implements CounterReferenceStorage {

    private final CounterReferenceRepository counterReferenceRepository;
    private final ReferenceCounterReferenceFileStorage referenceCounterReferenceFileStorage;

    @Override
    public Integer save(CounterReferenceInfoBo counterReferenceInfoBo) {
        log.debug("Input parameters -> counterReferenceInfoBo {}", counterReferenceInfoBo);
        CounterReference counterReference = new CounterReference(counterReferenceInfoBo);
        Integer counterReferenceId = counterReferenceRepository.save(counterReference).getId();
        referenceCounterReferenceFileStorage.updateReferenceCounterReferenceId(counterReferenceId, counterReferenceInfoBo.getFileIds());
        log.debug("Output -> {}", counterReferenceId);
        return counterReferenceId;
    }

    @Override
    public Optional<CounterReferenceSummaryBo> getCounterReference(Integer referenceId) {
        log.debug("Input parameter -> referenceId {}", referenceId);
        return counterReferenceRepository.findByReferenceId(referenceId);
    }

    @Override
    public List<CounterReferenceProcedureBo> getProceduresByCounterReference(Integer counterReferenceId) {
        log.debug("Input parameter -> counterReferenceId {}", counterReferenceId);
        return counterReferenceRepository.getProcedures(counterReferenceId);
    }


}