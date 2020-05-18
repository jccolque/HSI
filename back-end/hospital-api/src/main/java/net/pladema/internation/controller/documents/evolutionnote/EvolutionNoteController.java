package net.pladema.internation.controller.documents.evolutionnote;

import com.itextpdf.text.DocumentException;
import io.swagger.annotations.Api;
import net.pladema.internation.controller.constraints.DocumentValid;
import net.pladema.internation.controller.constraints.EvolutionNoteDiagnosisValid;
import net.pladema.internation.controller.constraints.InternmentValid;
import net.pladema.internation.controller.documents.evolutionnote.dto.EvolutionNoteDto;
import net.pladema.internation.controller.documents.evolutionnote.dto.ResponseEvolutionNoteDto;
import net.pladema.internation.controller.documents.evolutionnote.mapper.EvolutionNoteMapper;
import net.pladema.internation.controller.ips.constraints.EffectiveVitalSignTimeValid;
import net.pladema.internation.events.OnGenerateDocumentEvent;
import net.pladema.internation.repository.masterdata.entity.DocumentType;
import net.pladema.internation.service.documents.epicrisis.domain.Epicrisis;
import net.pladema.internation.service.documents.evolutionnote.CreateEvolutionNoteService;
import net.pladema.internation.service.documents.evolutionnote.EvolutionNoteService;
import net.pladema.internation.service.documents.evolutionnote.UpdateEvolutionNoteService;
import net.pladema.internation.service.documents.evolutionnote.domain.EvolutionNote;
import net.pladema.internation.service.internment.InternmentEpisodeService;
import net.pladema.pdf.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/institutions/{institutionId}/internments/{internmentEpisodeId}/evolutionNote")
@Api(value = "Evolution Note", tags = { "Evolution note" })
@Validated
public class EvolutionNoteController {

    private static final Logger LOG = LoggerFactory.getLogger(EvolutionNoteController.class);

    public static final String OUTPUT = "Output -> {}";
    
    public static final String INVALID_INTERNMENT_EPISODE = "internmentepisode.invalid";

    private final InternmentEpisodeService internmentEpisodeService;

    private final CreateEvolutionNoteService createEvolutionNoteService;

    private final UpdateEvolutionNoteService updateEvolutionNoteService;

    private final EvolutionNoteService evolutionNoteService;

    private final EvolutionNoteMapper evolutionNoteMapper;

    private final PdfService pdfService;

    public EvolutionNoteController(InternmentEpisodeService internmentEpisodeService,
                                   CreateEvolutionNoteService createEvolutionNoteService,
                                   UpdateEvolutionNoteService updateEvolutionNoteService,
                                   EvolutionNoteService evolutionNoteService,
                                   EvolutionNoteMapper evolutionNoteMapper,
                                   PdfService pdfService) {
        this.internmentEpisodeService = internmentEpisodeService;
        this.createEvolutionNoteService = createEvolutionNoteService;
        this.updateEvolutionNoteService = updateEvolutionNoteService;
        this.evolutionNoteService = evolutionNoteService;
        this.evolutionNoteMapper = evolutionNoteMapper;
        this.pdfService = pdfService;
    }

    @PostMapping
    @Transactional
    @InternmentValid
    @EvolutionNoteDiagnosisValid
    @EffectiveVitalSignTimeValid
    //TODO validar que diagnosticos descatados solo tengan estado REMISSION o SOLVED
    //TODO vaidar que diagnosticos ingresador por error solo tengan estado INACTIVE
    public ResponseEntity<ResponseEvolutionNoteDto> createDocument(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId,
            @RequestBody @Valid EvolutionNoteDto evolutionNoteDto) throws IOException, DocumentException{
        LOG.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, evolutionNote {}",
                institutionId, internmentEpisodeId, evolutionNoteDto);
        Integer patientId = internmentEpisodeService.getPatient(internmentEpisodeId)
                .orElseThrow(() -> new EntityNotFoundException(INVALID_INTERNMENT_EPISODE));
        EvolutionNote evolutionNote = evolutionNoteMapper.fromEvolutionNoteDto(evolutionNoteDto);
        evolutionNote = createEvolutionNoteService.createDocument(internmentEpisodeId, patientId, evolutionNote);
        ResponseEvolutionNoteDto result = evolutionNoteMapper.fromEvolutionNote(evolutionNote);
        LOG.debug(OUTPUT, result);
        generateDocument(evolutionNote, institutionId, internmentEpisodeId, patientId);
        return  ResponseEntity.ok().body(result);
    }

    @PutMapping("/{evolutionNoteId}")
    @InternmentValid
    @EvolutionNoteDiagnosisValid
    @EffectiveVitalSignTimeValid
    @DocumentValid(isConfirmed = false, documentType = DocumentType.EVALUATION_NOTE)
    public ResponseEntity<ResponseEvolutionNoteDto> updateDocument(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId,
            @PathVariable(name = "evolutionNoteId") Long evolutionNoteId,
            @Valid @RequestBody EvolutionNoteDto evolutionNoteDto) throws IOException, DocumentException{
        LOG.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, evolutionNoteId {}, evolutionNote {}",
                institutionId, internmentEpisodeId, evolutionNoteId, evolutionNoteDto);
        EvolutionNote evolutionNote = evolutionNoteMapper.fromEvolutionNoteDto(evolutionNoteDto);
        Integer patientId = internmentEpisodeService.getPatient(internmentEpisodeId)
                .orElseThrow(() -> new EntityNotFoundException(INVALID_INTERNMENT_EPISODE));
        evolutionNote = updateEvolutionNoteService.updateDocument(internmentEpisodeId, patientId, evolutionNote);
        ResponseEvolutionNoteDto result = evolutionNoteMapper.fromEvolutionNote(evolutionNote);
        LOG.debug(OUTPUT, result);
        generateDocument(evolutionNote, institutionId, internmentEpisodeId, patientId);
        return  ResponseEntity.ok().body(result);
    }

    private void generateDocument(EvolutionNote evolutionNote, Integer institutionId, Integer internmentEpisodeId,
                                  Integer patientId) throws IOException, DocumentException {
        OnGenerateDocumentEvent event = new OnGenerateDocumentEvent(evolutionNote, institutionId, internmentEpisodeId,
                DocumentType.EVALUATION_NOTE, "evolutionnote", patientId);
        pdfService.loadDocument(event);
    }

    @GetMapping("/{evolutionNoteId}")
    @InternmentValid
    @DocumentValid(isConfirmed = false, documentType = DocumentType.EVALUATION_NOTE)
    public ResponseEntity<ResponseEvolutionNoteDto> getDocument(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId,
            @PathVariable(name = "evolutionNoteId") Long evolutionNoteId){
        LOG.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, evolutionNoteId {}",
                institutionId, internmentEpisodeId, evolutionNoteId);
        EvolutionNote evolutionNote = evolutionNoteService.getDocument(evolutionNoteId);
        ResponseEvolutionNoteDto result = evolutionNoteMapper.fromEvolutionNote(evolutionNote);
        LOG.debug(OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }
}