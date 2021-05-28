package ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document;

import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.entity.Document;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.searchdocuments.DocumentRepositoryCustom;
import ar.lamansys.sgx.shared.auditable.entity.Updateable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, DocumentRepositoryCustom {

    @Query(value = "SELECT d.updateable " +
            "FROM Document d " +
            "WHERE d.sourceId = :internmentEpisodeId " +
            "and d.sourceTypeId = " + SourceType.HOSPITALIZATION)
    List<Updateable> getUpdatablesDocuments(@Param("internmentEpisodeId") Integer internmentEpisodeId);

    @Query(value = "SELECT DISTINCT new  ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.ProcedureReduced(snomedPr.pt, pr.performedDate) " +
            "FROM DocumentProcedure AS dp " +
            "JOIN Procedure AS pr ON (pr.id = dp.pk.procedureId) " +
            "JOIN Snomed AS snomedPr ON (pr.snomedId = snomedPr.id) " +
            "WHERE dp.pk.documentId = :documentId")
    List<ProcedureReduced> getProceduresByDocuments(@Param("documentId") Long documentId);

    @Transactional(readOnly = true)
    @Query(value = "SELECT d.id " +
            "FROM Document d " +
            "WHERE d.sourceId = :sourceId AND d.sourceTypeId = :sourceTypeId")
    List<Long> findBySourceIdAndSourceTypeId(@Param("sourceId") Integer sourceId, @Param("sourceTypeId") Short sourceTypeId );
}
