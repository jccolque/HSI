package net.pladema.reports.repository;

import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.masterdata.entity.ProblemType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class QueryFactory {

    @PersistenceContext
    private final EntityManager entityManager;

    public QueryFactory(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
    public List<ConsultationDetail> query(Integer institutionId, LocalDate startDate, LocalDate endDate,
                                          Integer clinicalSpecialtyId, Integer doctorId) {

        Query query = entityManager.createNamedQuery("Reports.ConsultationDetail");
        query.setParameter("institutionId", institutionId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("problemTypes", List.of(ProblemType.PROBLEM, ProblemType.CHRONIC));
        List<ConsultationDetail> data = query.getResultList();

        //Optional filter: by specialty or professional if specified
        return data.stream()
                .filter(doctorId != null ? oc -> oc.getProfessionalId().equals(doctorId) : c -> true)
                .filter(clinicalSpecialtyId != null ? oc -> oc.getClinicalSpecialtyId().equals(clinicalSpecialtyId) : c -> true)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<ConsultationSummary> fetchConsultationSummaryData(Integer institutionId, LocalDate startDate, LocalDate endDate){
        return entityManager.createNamedQuery("Reports.ConsultationSummary")
                .setParameter("institutionId", institutionId)
                .setParameter("from", startDate)
                .setParameter("to", endDate)
                .getResultList();
    }
}
