package net.pladema.clinichistory.outpatient.repository;

import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentStatus;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentType;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.SourceType;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.hospitalizationState.entity.HealthConditionSummaryVo;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.hospitalizationState.entity.ProcedureSummaryVo;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.hospitalizationState.entity.ReasonSummaryVo;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.ips.Snomed;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.masterdata.entity.ProblemType;
import net.pladema.clinichistory.outpatient.repository.domain.OdontologyEvolutionSummaryVo;
import net.pladema.clinichistory.outpatient.repository.domain.OutpatientEvolutionSummaryVo;
import net.pladema.person.repository.entity.Person;
import net.pladema.staff.repository.entity.ClinicalSpecialty;
import net.pladema.staff.repository.entity.HealthcareProfessional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OdontologyConsultationSummaryRepositoryImpl implements OdontologyConsultationSummaryRepository {

    private final EntityManager entityManager;

    public OdontologyConsultationSummaryRepositoryImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Override
    public List<OdontologyEvolutionSummaryVo> getAllOdontologyEvolutionSummary(Integer patientId) {
        String sqlString =" SELECT oc.id, oc.performedDate, cs, hp, p, n.description"
                + " FROM OdontologyConsultation oc"
                + " LEFT JOIN ClinicalSpecialty cs ON (oc.clinicalSpecialtyId = cs.id)"
                + " JOIN Document doc ON (doc.sourceId = oc.id)"
                + " LEFT JOIN Note n ON (n.id = doc.otherNoteId)"
                + " JOIN HealthcareProfessional hp ON (hp.id = oc.doctorId)"
                + " JOIN Person p ON (p.id = hp.personId)"
                + " WHERE oc.billable = TRUE "
                + " AND oc.patientId = :patientId"
                + " AND doc.sourceTypeId = " + SourceType.ODONTOLOGY
                + " ORDER BY oc.performedDate DESC";

        List<Object[]> queryResult = entityManager.createQuery(sqlString)
                .setParameter("patientId", patientId)
                .getResultList();
        List<OdontologyEvolutionSummaryVo> result = new ArrayList<>();
        queryResult.forEach(a ->
                result.add(new OdontologyEvolutionSummaryVo(
                        (Integer)a[0],
                        a[1] != null ? (LocalDate)a[1] : null,
                        (ClinicalSpecialty) a[2],
                        (HealthcareProfessional) a[3],
                        (Person) a[4],
                        (String)a[5]))
        );
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<HealthConditionSummaryVo> getHealthConditionsByPatient(Integer patientId, List<Integer> odontologyConsultationIds) {
        String sqlString ="SELECT hc.id, s.sctid, s.pt, d.statusId, hc.startDate, hc.inactivationDate, hc.main, hc.problemId, oc.id"
                +"  FROM OdontologyConsultation oc"
                +"  JOIN Document d ON (d.sourceId = oc.id)"
                +"  JOIN DocumentHealthCondition dhc ON (d.id = dhc.pk.documentId)"
                +"  JOIN HealthCondition hc ON (dhc.pk.healthConditionId = hc.id)"
                +"  JOIN Snomed s ON (s.id = hc.snomedId)"
                +"  WHERE d.statusId = '" + DocumentStatus.FINAL + "'"
                +"  AND d.sourceTypeId =" + SourceType.ODONTOLOGY
                +"  AND d.typeId = "+ DocumentType.ODONTOLOGY
                +"  AND hc.patientId = :patientId "
                +"  AND hc.problemId IN ('"+ ProblemType.PROBLEM+"', '"+ ProblemType.CHRONIC+ "')"
                +"  AND oc.id IN (:odontologyConsultationIds) ";

        List<Object[]> queryResult = entityManager.createQuery(sqlString)
                .setParameter("patientId", patientId)
                .setParameter("odontologyConsultationIds", odontologyConsultationIds)
                .getResultList();
        List<HealthConditionSummaryVo> result = new ArrayList<>();
        queryResult.forEach(a ->
                result.add(new HealthConditionSummaryVo(
                        (Integer)a[0],
                        (String) a[1],
                        (String) a[2],
                        (String)a[3],
                        a[4] != null ? (LocalDate)a[4] : null,
                        a[5] != null ? (LocalDate)a[5] : null,
                        (boolean)a[6],
                        (String)a[7],
                        (Integer) a[8]))
        );
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ReasonSummaryVo> getReasonsByPatient(Integer patientId, List<Integer> odontologyConsultationIds) {
        String sqlString = "SELECT r.id, r.description, oc.id"
                +"  FROM OdontologyConsultation oc"
                +"  JOIN OdontologyConsultationReason ocr ON (ocr.pk.odontologyConsultationId = oc.id)"
                +"  JOIN OdontologyReason r ON (r.id = ocr.pk.reasonId)"
                +"  WHERE oc.patientId = :patientId"
                +"  AND oc.id IN (:odontologyConsultationIds) ";

        List<Object[]> queryResult = entityManager.createQuery(sqlString)
                .setParameter("patientId", patientId)
                .setParameter("odontologyConsultationIds", odontologyConsultationIds)
                .getResultList();
        List<ReasonSummaryVo> result = new ArrayList<>();
        queryResult.forEach(a ->
                result.add(new ReasonSummaryVo(
                        (String)a[0],
                        (String)a[1],
                        (Integer)a[2]))
        );
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProcedureSummaryVo> getProceduresByPatient(Integer patientId, List<Integer> odontologyConsultationIds) {
        String sqlString = "SELECT p.id, s, p.performedDate, oc.id"
                +"  FROM OdontologyConsultation oc"
                +"  JOIN Document d ON (d.sourceId = oc.id)"
                +"  JOIN DocumentProcedure dp ON (d.id = dp.pk.documentId)"
                +"  JOIN Procedure p ON (dp.pk.procedureId = p.id)"
                +"  JOIN Snomed s ON (p.snomedId = s.id) "
                +"  WHERE oc.patientId = :patientId"
                +"  AND d.statusId = '" + DocumentStatus.FINAL + "'"
                +"  AND d.typeId = "+ DocumentType.ODONTOLOGY
                +"  AND d.sourceTypeId =" + SourceType.ODONTOLOGY
                +"  AND oc.id IN (:odontologyConsultationIds) ";
        
        List<Object[]> queryResult = entityManager.createQuery(sqlString)
                .setParameter("patientId", patientId)
                .setParameter("odontologyConsultationIds", odontologyConsultationIds)
                .getResultList();
        List<ProcedureSummaryVo> result = new ArrayList<>();
        queryResult.forEach(a ->
                result.add(new ProcedureSummaryVo(
                        (Integer)a[0],
                        (Snomed) a[1],
                        a[2] != null ? (LocalDate)a[2] : null,
                        (Integer) a[3]))
        );
        return result;
    }

}