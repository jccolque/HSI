package net.pladema.clinichistory.hospitalization.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentStatus;
import net.pladema.clinichistory.hospitalization.repository.domain.InternmentEpisodeStatus;
import net.pladema.patient.repository.domain.PatientMedicalCoverageVo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.pladema.clinichistory.hospitalization.repository.domain.InternmentEpisode;
import net.pladema.clinichistory.hospitalization.repository.domain.processepisode.InternmentEpisodeProcessVo;
import net.pladema.clinichistory.hospitalization.repository.domain.summary.InternmentSummaryVo;
import net.pladema.clinichistory.hospitalization.service.domain.BasicListedPatientBo;
import net.pladema.clinichistory.hospitalization.service.domain.InternmentEpisodeBo;

@Repository
public interface InternmentEpisodeRepository extends JpaRepository<InternmentEpisode, Integer> {


    @Transactional(readOnly = true)
    @Query("SELECT NEW net.pladema.clinichistory.hospitalization.repository.domain.summary.InternmentSummaryVo(" +
            "ie.id,  ie.entryDate, " +
            "ie.anamnesisDocId, da.statusId as anamnesisStatusId, " +
            "ie.epicrisisDocId, de.statusId as epicrisisStatusId, " +
            "b.id as bedId, b.bedNumber, " +
            "r.id as roomId, r.roomNumber, sector.description, " +
            "hpg.pk.healthcareProfessionalId, hp.licenseNumber, p.firstName, p.lastName," +
            "rc, ie.probableDischargeDate, pd.administrativeDischargeDate, ie.statusId) " +
            "FROM InternmentEpisode ie " +
            "JOIN Bed b ON (b.id = ie.bedId) " +
            "JOIN Room r ON (r.id = b.roomId) " +
            "JOIN Sector sector ON (sector.id = r.sectorId) " +
            "LEFT JOIN Document da ON (da.id = ie.anamnesisDocId) " +
            "LEFT JOIN Document de ON (de.id = ie.epicrisisDocId) " +
            "LEFT JOIN HealthcareProfessionalGroup hpg ON (hpg.pk.internmentEpisodeId = ie.id and hpg.responsible = true) " +
            "LEFT JOIN HealthcareProfessional hp ON (hpg.pk.healthcareProfessionalId = hp.id) " +
            "LEFT JOIN Person p ON (hp.personId = p.id) " +
            "LEFT JOIN ResponsibleContact rc ON (ie.id = rc.internmentEpisodeId) " +
            "LEFT JOIN PatientDischarge pd ON (ie.id = pd.internmentEpisodeId) " +
            "WHERE ie.id = :internmentEpisodeId")
    Optional<InternmentSummaryVo> getSummary(@Param("internmentEpisodeId") Integer internmentEpisodeId);


    @Transactional(readOnly = true)
    @Query("SELECT ie.patientId " +
            "FROM InternmentEpisode ie " +
            "WHERE ie.id = :internmentEpisodeId")
    Optional<Integer> getPatient(@Param("internmentEpisodeId") Integer internmentEpisodeId);

    @Transactional
    @Modifying
    @Query("UPDATE InternmentEpisode AS ie " +
            "SET ie.anamnesisDocId = :anamnesisDocumentId, " +
            "ie.updateable.updatedOn = :today " +
            "WHERE ie.id = :internmentEpisodeId")
    void updateAnamnesisDocumentId(@Param("internmentEpisodeId") Integer internmentEpisodeId,
                                   @Param("anamnesisDocumentId") Long anamnesisDocumentId,
                                   @Param("today") LocalDateTime today);

    @Transactional
    @Modifying
    @Query("UPDATE InternmentEpisode AS ie " +
            "SET ie.epicrisisDocId = :epicrisisDocumentId, " +
            "ie.updateable.updatedOn = :today " +
            "WHERE ie.id = :internmentEpisodeId")
    void updateEpicrisisDocumentId(@Param("internmentEpisodeId") Integer internmentEpisodeId,
                                   @Param("epicrisisDocumentId") Long epicrisisDocumentId,
                                   @Param("today") LocalDateTime today);

    @Transactional(readOnly = true)
    @Query("SELECT NEW net.pladema.clinichistory.hospitalization.service.domain.BasicListedPatientBo(pa.id, pe.identificationTypeId, " +
            "pe.identificationNumber, pe.firstName, pe.lastName, petd.nameSelfDetermination, pe.birthDate, pe.genderId, ie.id ) " +
            " FROM InternmentEpisode as ie " +
            " JOIN Patient as pa ON (ie.patientId = pa.id) " +
            " JOIN Person as pe ON (pa.personId = pe.id) " +
            " JOIN PersonExtended petd ON (pe.id = petd.id)"+
            " WHERE ie.institutionId = :institutionId "+
            " AND ie.statusId <> " + InternmentEpisodeStatus.INACTIVE)
    List<BasicListedPatientBo> findAllPatientsListedData(@Param("institutionId") Integer institutionId);

    @Transactional(readOnly = true)
    @Query("SELECT (case when count(ie.id)> 0 then true else false end) " +
            "FROM InternmentEpisode ie " +
            "WHERE ie.id = :internmentEpisodeId " +
            "AND ie.anamnesisDocId IS NOT NULL")
    boolean haveAnamnesis(@Param("internmentEpisodeId") Integer internmentEpisodeId);

    @Transactional(readOnly = true)
    @Query("SELECT NEW net.pladema.clinichistory.hospitalization.service.domain.InternmentEpisodeBo(" +
            "ie.id as internmentEpisodeId, " +
            "pt.id as patientId, ps.firstName, ps.lastName, petd.nameSelfDetermination, " +
            "b.id as bedId, b.bedNumber, " +
            "r.id as roomId, r.roomNumber, " +
            "s.id as sectorId, s.description) " +
            "FROM InternmentEpisode ie " +
            "JOIN Patient pt ON (ie.patientId = pt.id) " +
            "JOIN Person ps ON (pt.personId = ps.id) " +
            "JOIN PersonExtended petd ON (pt.personId = petd.id) " +
            "JOIN Bed b ON (ie.bedId = b.id) " +
            "JOIN Room r ON (b.roomId = r.id) " +
            "JOIN Sector s ON (r.sectorId = s.id) " +
            "WHERE ie.institutionId = :institutionId " +
            "AND NOT EXISTS (select pd.id " +
            "                FROM PatientDischarge pd" +
            "                WHERE pd.internmentEpisodeId = ie.id) " +
            " ORDER BY ps.firstName ASC, ps.lastName ASC")
    List<InternmentEpisodeBo> getAllInternmentPatient(@Param("institutionId") Integer institutionId);

    @Transactional(readOnly = true)
    @Query("SELECT (case when count(ie.id)> 0 then true else false end) " +
            "FROM InternmentEpisode ie " +
            "WHERE ie.id = :internmentEpisodeId " +
            "AND ie.epicrisisDocId IS NOT NULL")
    boolean haveEpicrisis(@Param("internmentEpisodeId") Integer internmentEpisodeId);

    @Transactional(readOnly = true)
    @Query(" SELECT NEW net.pladema.clinichistory.hospitalization.repository.domain.processepisode.InternmentEpisodeProcessVo(ie.id, ie.institutionId) " +
            "FROM InternmentEpisode  ie " +
            "WHERE  ie.patientId = :patientId and ie.statusId <> " + InternmentEpisodeStatus.INACTIVE)
    Optional<InternmentEpisodeProcessVo> internmentEpisodeInProcess(@Param("patientId") Integer patientId);

    @Transactional(readOnly = true)
    @Query("SELECT ie.entryDate " +
            "FROM InternmentEpisode ie " +
            "WHERE ie.id = :internmentEpisodeId ")
    LocalDateTime getEntryDate(@Param("internmentEpisodeId")  Integer internmentEpisodeId);


    @Transactional(readOnly = true)
    @Query("SELECT (case when count(ie.id)> 0 then true else false end) " +
            "FROM InternmentEpisode ie " +
            "JOIN Document da ON (da.id = ie.anamnesisDocId and da.statusId = '"+ DocumentStatus.FINAL + "') " +
            "WHERE ie.id = :internmentEpisodeId " +
            "AND ie.epicrisisDocId IS NULL " +
            "AND EXISTS (" +
            "               SELECT d.id " +
            "               FROM EvolutionNoteDocument evnd " +
            "               JOIN Document d ON (d.id = evnd.pk.documentId) " +
            "               WHERE evnd.pk.internmentEpisodeId = ie.id " +
            "               AND d.statusId = '"+ DocumentStatus.FINAL + "'" +
            "           )")
    boolean canCreateEpicrisis(@Param("internmentEpisodeId")  Integer internmentEpisodeId);

    @Transactional(readOnly = true)
    @Query(" SELECT ie " +
            "FROM InternmentEpisode  ie " +
            "WHERE ie.id = :internmentEpisodeId and ie.institutionId = :institutionId")
    Optional<InternmentEpisode> getInternmentEpisode(@Param("internmentEpisodeId")  Integer internmentEpisodeId, @Param("institutionId") Integer institutionId);

	List<InternmentEpisode> findByBedId(Integer bedId);

    @Transactional
    @Modifying
    @Query("UPDATE InternmentEpisode AS ie " +
            "SET ie.probableDischargeDate = :probableDischargeDate, " +
            "ie.updateable.updatedOn = :today, " +
            "ie.updateable.updatedBy = :currentUser " +
            "WHERE ie.id = :internmentEpisodeId")
    void updateInternmentEpisodeProbableDischargeDate(@Param("internmentEpisodeId") Integer internmentEpisodeId,
                                                      @Param("probableDischargeDate") LocalDateTime probableDischargeDate,
                                                      @Param("currentUser") Integer currentUser,
                                                      @Param("today") LocalDateTime today);
    
    @Transactional
    @Modifying
    @Query("UPDATE InternmentEpisode AS ie " +
            "SET ie.bedId = :bedId, " +
            "ie.updateable.updatedOn = :today, " +
            "ie.updateable.updatedBy = :currentUser " +
            "WHERE ie.id = :internmentEpisodeId")
    void updateInternmentEpisodeBed(@Param("internmentEpisodeId") Integer internmentEpisodeId,
                                                      @Param("bedId") Integer bedId,
                                                      @Param("currentUser") Integer currentUser,
                                                      @Param("today") LocalDateTime today);

	@Transactional(readOnly = true)
	@Query("SELECT NEW net.pladema.patient.repository.domain.PatientMedicalCoverageVo" +
			"(pmc.id, pmc.affiliateNumber, pmc.vigencyDate, pmc.active, mc.id, mc.name, " +
			"mc.cuit, hi.rnos, hi.acronym, pmc.conditionId, pmc.startDate, pmc.endDate, pmc.planId, hi.id ) " +
			"FROM PatientMedicalCoverageAssn pmc " +
			"JOIN InternmentEpisode ie ON (ie.patientMedicalCoverageId = pmc.id) " +
			"JOIN MedicalCoverage mc ON (pmc.medicalCoverageId = mc.id) " +
			"LEFT JOIN HealthInsurance hi ON (mc.id = hi.id) " +
			"LEFT JOIN PrivateHealthInsurance phi ON (mc.id = phi.id) " +
			"WHERE pmc.id = ie.patientMedicalCoverageId " +
			"AND ie.id = :internmentEpisodeId")
	Optional<PatientMedicalCoverageVo> getInternmentEpisodeMedicalCoverage(@Param("internmentEpisodeId")  Integer internmentEpisodeId);

}
