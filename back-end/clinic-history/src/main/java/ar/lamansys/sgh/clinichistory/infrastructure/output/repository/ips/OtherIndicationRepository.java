package ar.lamansys.sgh.clinichistory.infrastructure.output.repository.ips;

import java.util.List;

import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.EDocumentType;
import ar.lamansys.sgx.shared.auditable.repository.SGXAuditableEntityJPARepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.ips.entity.indication.OtherIndication;


@Repository
public interface OtherIndicationRepository extends SGXAuditableEntityJPARepository<OtherIndication, Integer> {

	@Transactional(readOnly = true)
	@Query(value = "SELECT oi "
			+ "FROM OtherIndication oi "
			+ "JOIN Indication i ON i.id = oi.id "
			+ "JOIN DocumentIndication di ON di.pk.indicationId = i.id "
			+ "JOIN Document doc ON di.pk.documentId = doc.id "
			+ "WHERE doc.sourceId = :internmentEpisodeId "
			+ "AND doc.typeId = :documentTypeId")
	List<OtherIndication> getByInternmentEpisodeId(@Param("internmentEpisodeId") Integer internmentEpisodeId,
												   @Param("documentTypeId") Short documentTypeId);

}
