package net.pladema.internation.repository.documents.searchdocument;

import com.google.common.base.Strings;
import lombok.NoArgsConstructor;
import net.pladema.internation.repository.ips.generalstate.DocumentObservationsVo;
import net.pladema.internation.repository.masterdata.entity.ConditionVerificationStatus;
import net.pladema.patient.service.StringHelper;
import net.pladema.sgx.repository.QueryPart;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor
public class DocumentSearchQuery {

    String plainText;
    String escapeSqlText;
    private static final String MESSAGE = "No se han creado documentos aún";

    public DocumentSearchQuery(String plainText){
        this.plainText = plainText;
        this.escapeSqlText = StringHelper.escapeSql(plainText);
    }

    public QueryPart select() {
        return new QueryPart("document.id, \n" +
                "document.creationable.createdOn, \n" +
                "creator.firstName, \n" +
                "creator.lastName, \n" +
                "snomed.pt as diagnosis, \n" +
                "hc.main, \n" +
                "physicalnote.description as physicalNote, \n" +
                "studiesnote.description as studiesNote, \n" +
                "evolutionnote.description as evolutionNote,\n" +
                "clinicalnote.description as clinicalNote, \n" +
                "illnessnote.description as illnessNote, \n" +
                "indicationnote.description as indicationNote \n");
    }

    public QueryPart from() {
        return new QueryPart("DocumentHealthCondition as dhc \n" +
                "join Document as document on (dhc.pk.documentId = document.id) \n" +
        //Creator
                "join User as users on (document.creationable.createdBy = users.id) \n" +
                "join Person as creator on (users.personId = creator.id) \n" +
        //Notes
                "left join Note physicalnote on (document.physicalExamNoteId = physicalnote.id) \n" +
                "left join Note studiesnote on (document.studiesSummaryNoteId = studiesnote.id) \n" +
                "left join Note evolutionnote on (document.evolutionNoteId = evolutionnote.id) \n" +
                "left join Note clinicalnote on (document.clinicalImpressionNoteId = clinicalnote.id) \n" +
                "left join Note illnessnote on (document.currentIllnessNoteId = illnessnote.id) \n" +
                "left join Note indicationnote on (document.indicationsNoteId = indicationnote.id) \n" +
         //Diagnosis
                "join HealthCondition as hc on (dhc.pk.healthConditionId = hc.id ) \n" +
                "join Snomed as snomed on (hc.sctidCode = snomed.id ) \n"
        );
    }

    public QueryPart where() {
        return new QueryPart("document.internmentEpisodeId = :internmentEpisodeId \n" +
                "and not hc.verificationStatusId = '" + ConditionVerificationStatus.ERROR +"' \n");
    }

    public QueryPart orderBy(){
        return new QueryPart("document.creationable.createdOn DESC ");
    }

    public String noResultMessage(){
        return MESSAGE;
    }

    public List<DocumentSearchVo> construct(List<Object[]> resultQuery){
        List<DocumentSearchVo> result = new ArrayList<>();

        Map<Long, List<Object[]>> diagnosisByDocuments = resultQuery
                .stream()
                .collect(Collectors.groupingBy(
                        (Object[] t) -> (Long)t[0],
                        LinkedHashMap::new,
                        toList())
                );
        diagnosisByDocuments.forEach((k,v) -> {
            Object[] tuple = v.get(0);
            result.add(new DocumentSearchVo(k,
                    mapNotes(tuple),
                    (LocalDateTime)tuple[1],
                    (String)tuple[2],
                    (String)tuple[3],
                    mapDiagnosis(v),
                    mapMainDiagnosis(v)));
        });
        return result;
    }

    private DocumentObservationsVo mapNotes(Object[] tuple){
        String physicalExam = (String)tuple[6];
        String studiesSummary = (String)tuple[7];
        String evolutionNote = (String)tuple[8];
        String clinicalImpression = (String)tuple[9];
        String currentIllness = (String)tuple[10];
        String indicationsNote = (String)tuple[11];

        boolean withoutNotes = Strings.isNullOrEmpty(physicalExam) &&
                Strings.isNullOrEmpty(studiesSummary) &&
                Strings.isNullOrEmpty(evolutionNote) &&
                Strings.isNullOrEmpty(clinicalImpression) &&
                Strings.isNullOrEmpty(currentIllness) &&
                Strings.isNullOrEmpty(indicationsNote);
        if(withoutNotes)
            return null;
        return new DocumentObservationsVo(null, physicalExam, studiesSummary, evolutionNote,
                clinicalImpression, currentIllness, indicationsNote);
    }

    private String mapMainDiagnosis(List<Object[]> tuples){
        return tuples.stream()
                .filter((Object[]t) -> (Boolean)t[5])
                .map((Object[]t) -> (String)t[4]).collect(Collectors.joining());
    }

    private List<String> mapDiagnosis(List<Object[]> tuples){
        return tuples.stream()
                .filter((Object[]t) -> !(Boolean)t[5])
                .map((Object[]t) -> (String)t[4]).collect(Collectors.toList());
    }
}
