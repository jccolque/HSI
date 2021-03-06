package net.pladema.staff.repository.entity;

import ar.lamansys.sgx.shared.auditable.entity.SGXAuditableEntity;
import ar.lamansys.sgx.shared.auditable.listener.SGXAuditListener;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "healthcare_professional_specialty")
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(SGXAuditListener.class)
@Where(clause = "deleted=false")
public class HealthcareProfessionalSpecialty extends SGXAuditableEntity<Integer> implements Serializable {
	
	private static final long serialVersionUID = -5292560767942911734L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Integer id;
	
	@Column(name = "healthcare_professional_id", nullable = false)
	private Integer healthcareProfessionalId;
	
	@Column(name = "professional_specialty_id", nullable = false)
	private Integer professionalSpecialtyId;

	@Column(name = "clinical_specialty_id", nullable = false)
	private Integer clinicalSpecialtyId;

	public HealthcareProfessionalSpecialty(Integer healthcareProfessionalId,
										   Integer professionalSpecialtyId,
										   Integer clinicalSpecialtyId){
		this.healthcareProfessionalId = healthcareProfessionalId;
		this.professionalSpecialtyId = professionalSpecialtyId;
		this.clinicalSpecialtyId = clinicalSpecialtyId;
	}
}
