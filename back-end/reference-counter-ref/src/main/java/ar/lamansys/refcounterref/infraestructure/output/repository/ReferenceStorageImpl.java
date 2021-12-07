package ar.lamansys.refcounterref.infraestructure.output.repository;

import ar.lamansys.refcounterref.application.port.ReferenceCounterReferenceFileStorage;
import ar.lamansys.refcounterref.application.port.ReferenceStorage;
import ar.lamansys.refcounterref.domain.enums.EReferenceCounterReferenceType;
import ar.lamansys.refcounterref.domain.file.ReferenceCounterReferenceFileBo;
import ar.lamansys.refcounterref.domain.reference.ReferenceBo;
import ar.lamansys.refcounterref.domain.reference.ReferenceGetBo;
import ar.lamansys.refcounterref.domain.referenceproblem.ReferenceProblemBo;
import ar.lamansys.refcounterref.infraestructure.output.repository.reference.Reference;
import ar.lamansys.refcounterref.infraestructure.output.repository.reference.ReferenceRepository;
import ar.lamansys.refcounterref.infraestructure.output.repository.referencehealthcondition.ReferenceHealthCondition;
import ar.lamansys.refcounterref.infraestructure.output.repository.referencehealthcondition.ReferenceHealthConditionPk;
import ar.lamansys.refcounterref.infraestructure.output.repository.referencehealthcondition.ReferenceHealthConditionRepository;
import ar.lamansys.refcounterref.infraestructure.output.repository.referencenote.ReferenceNote;
import ar.lamansys.refcounterref.infraestructure.output.repository.referencenote.ReferenceNoteRepository;
import ar.lamansys.sgh.clinichistory.application.healthCondition.HealthConditionStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReferenceStorageImpl implements ReferenceStorage {

    private final ReferenceRepository referenceRepository;
    private final ReferenceNoteRepository referenceNoteRepository;
    private final ReferenceHealthConditionRepository referenceHealthConditionRepository;
    private final HealthConditionStorage healthConditionStorage;
    private final ReferenceCounterReferenceFileStorage referenceCounterReferenceFileStorage;

    @Override
    public void save(List<ReferenceBo> referenceBoList) {
        log.debug("Input parameters -> referenceBoList {}", referenceBoList);
        referenceBoList.stream().forEach(referenceBo -> {
            Reference ref = new Reference(referenceBo);
            if (referenceBo.getNote() != null) {
                Integer referenceNoteId = referenceNoteRepository.save(new ReferenceNote(referenceBo.getNote())).getId();
                ref.setReferenceNoteId(referenceNoteId);
            }
            Integer referenceId = referenceRepository.save(ref).getId();
            List<ReferenceHealthCondition> referenceHealthConditionList = saveProblems(referenceId, referenceBo);
            log.debug("referenceHealthConditionList, referenceId -> {} {}", referenceHealthConditionList, referenceId);
            referenceCounterReferenceFileStorage.updateReferenceCounterReferenceId(referenceId, referenceBo.getFileIds());
        });
    }

    @Override
    public List<ReferenceGetBo> getReferences(Integer institutionId, Integer patientId, List<Integer> clinicalSpecialtyIds) {
        List<ReferenceGetBo> queryResult = referenceRepository.getReferences(institutionId, patientId, clinicalSpecialtyIds);
        List<Integer> referenceIds = queryResult.stream().map(ReferenceGetBo::getId).collect(Collectors.toList());

        Map<Integer, List<ReferenceProblemBo>> problems = referenceHealthConditionRepository.getReferencesProblems(referenceIds)
                .stream()
                .map(ReferenceProblemBo::new)
                .collect(Collectors.groupingBy(ReferenceProblemBo::getReferenceId));

        queryResult = queryResult.stream().map(ref -> {
            ref.setProblems(problems.get(ref.getId()));
            return ref;
        }).collect(Collectors.toList());

        Map<Integer, List<ReferenceCounterReferenceFileBo>> files = referenceCounterReferenceFileStorage.getFilesByReferenceCounterReferenceIdsAndType(referenceIds, EReferenceCounterReferenceType.REFERENCIA);
        List<ReferenceGetBo> result = queryResult.stream().map(ref -> {
            ref.setFiles(files.get(ref.getId()));
            return ref;
        }).collect(Collectors.toList());

        return result;
    }

    public List<ReferenceHealthCondition> saveProblems(Integer referenceId, ReferenceBo referenceBo) {
        return referenceBo.getProblems().stream().map(problem -> {
            ReferenceHealthConditionPk refPk = new ReferenceHealthConditionPk();
            if (problem.getId() == null) {
                Integer healthConditionId = healthConditionStorage.getHealthConditionIdByEncounterAndSnomedConcept(
                        referenceBo.getEncounterId(), referenceBo.getSourceTypeId(), problem.getSnomed().getSctid(), problem.getSnomed().getPt());
                refPk.setHealthConditionId(healthConditionId);
            } else {
                refPk.setHealthConditionId(problem.getId());
            }
            refPk.setReferenceId(referenceId);
            return referenceHealthConditionRepository.save(new ReferenceHealthCondition(refPk));
        }).collect(Collectors.toList());
    }

}
