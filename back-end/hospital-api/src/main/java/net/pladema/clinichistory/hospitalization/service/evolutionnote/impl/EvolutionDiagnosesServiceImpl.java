package net.pladema.clinichistory.hospitalization.service.evolutionnote.impl;

import ar.lamansys.sgh.clinichistory.domain.ips.services.HealthConditionService;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentStatus;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentType;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.SourceType;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.entity.Document;
import ar.lamansys.sgh.clinichistory.application.document.DocumentService;
import ar.lamansys.sgh.clinichistory.application.notes.NoteService;
import ar.lamansys.sgh.clinichistory.domain.ips.DocumentObservationsBo;
import net.pladema.clinichistory.hospitalization.service.InternmentEpisodeService;
import net.pladema.clinichistory.hospitalization.service.evolutionnote.EvolutionDiagnosesService;
import net.pladema.clinichistory.hospitalization.service.evolutionnote.domain.evolutiondiagnosis.EvolutionDiagnosisBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvolutionDiagnosesServiceImpl implements EvolutionDiagnosesService {

    private static final Logger LOG = LoggerFactory.getLogger(EvolutionDiagnosesServiceImpl.class);

    public static final String OUTPUT = "Output -> {}";

    private final DocumentService documentService;

    private final InternmentEpisodeService internmentEpisodeService;

    private final HealthConditionService healthConditionService;

    private final NoteService noteService;

    public EvolutionDiagnosesServiceImpl(DocumentService documentService,
                                         InternmentEpisodeService internmentEpisodeService,
                                         HealthConditionService healthConditionService,
                                         NoteService noteService) {
        this.documentService = documentService;
        this.internmentEpisodeService = internmentEpisodeService;
        this.healthConditionService = healthConditionService;
        this.noteService = noteService;
    }

    @Override
    public Long execute(Integer internmentEpisodeId, Integer patientId, EvolutionDiagnosisBo evolutionDiagnosis) {
        LOG.debug("Input parameters -> intermentEpisodeId {}, patientId {}, evolutionDiagnosis {}", internmentEpisodeId, patientId, evolutionDiagnosis);

        Document doc = new Document(internmentEpisodeId, DocumentStatus.FINAL, DocumentType.EVALUATION_NOTE, SourceType.HOSPITALIZATION);
        loadNotes(doc, Optional.ofNullable(evolutionDiagnosis.getNotes()));
        doc = documentService.save(doc);
        List<Integer> diagnoses = healthConditionService.copyDiagnoses(evolutionDiagnosis.getDiagnosesId());
        Long result = doc.getId();
        diagnoses.forEach( id -> documentService.createDocumentHealthCondition(result, id));
        internmentEpisodeService.addEvolutionNote(internmentEpisodeId, doc.getId());

        LOG.debug(OUTPUT, result);
        return result;
    }

    private Document loadNotes(Document evolutionNote, Optional<DocumentObservationsBo> optNotes) {
        LOG.debug("Input parameters -> evolutionNote {}, notes {}", evolutionNote, optNotes);
        optNotes.ifPresent(notes -> {
            evolutionNote.setCurrentIllnessNoteId(noteService.createNote(notes.getCurrentIllnessNote()));
            evolutionNote.setPhysicalExamNoteId(noteService.createNote(notes.getPhysicalExamNote()));
            evolutionNote.setStudiesSummaryNoteId(noteService.createNote(notes.getStudiesSummaryNote()));
            evolutionNote.setEvolutionNoteId(noteService.createNote(notes.getEvolutionNote()));
            evolutionNote.setClinicalImpressionNoteId(noteService.createNote(notes.getClinicalImpressionNote()));
            evolutionNote.setOtherNoteId(noteService.createNote(notes.getOtherNote()));
        });
        LOG.debug(OUTPUT, evolutionNote);
        return evolutionNote;
    }
}
