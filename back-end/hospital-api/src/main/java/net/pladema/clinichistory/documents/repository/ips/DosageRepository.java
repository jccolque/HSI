package net.pladema.clinichistory.documents.repository.ips;

import net.pladema.clinichistory.documents.repository.ips.entity.Dosage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DosageRepository extends JpaRepository<Dosage, Integer> {

}
