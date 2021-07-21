package net.pladema.reports.repository.impl;

import net.pladema.reports.repository.AnnexReportRepository;
import net.pladema.reports.repository.entity.AnnexIIVo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class AnnexReportRepositoryImpl implements AnnexReportRepository {

    private final EntityManager entityManager;

    public AnnexReportRepositoryImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AnnexIIVo> getAnexoInfo(Integer appointmentId) {
        String query = "SELECT NEW net.pladema.reports.repository.entity.AnnexIIVo(i.name, " +
                "           CONCAT(pe.firstName, ' ',pe.lastName) as nombresApellidosPaciente, it.description, pe.identificationNumber, " +
                "           g.description, pe.birthDate, a.dateTypeId) " +
                "       FROM Appointment AS a " +
                "           JOIN AppointmentAssn AS assn ON (a.id = assn.pk.appointmentId) " +
                "           JOIN Diary AS d ON (assn.pk.diaryId = d.id) " +
                "           JOIN DoctorsOffice AS doff ON (d.doctorsOfficeId = doff.id) " +
                "           JOIN Institution AS i ON (doff.institutionId = i.id) " +
                "           JOIN Patient AS pa ON (a.patientId = pa.id) " +
                "           LEFT JOIN Person AS pe ON (pe.id = pa.personId) " +
                "           JOIN IdentificationType AS it ON (it.id = pe.identificationTypeId) " +
                "           JOIN Gender AS g ON (pe.genderId = g.id) " +
                "       WHERE a.id = :appointmentId ";

        return entityManager.createQuery(query)
                .setParameter("appointmentId", appointmentId)
                .setMaxResults(1)
                .getResultList().stream().findFirst();

    }
}
