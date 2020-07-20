package net.pladema.clinichistory.ips.repository.entity;

import lombok.*;
import net.pladema.clinichistory.hospitalization.repository.listener.InternationAuditableEntity;
import net.pladema.clinichistory.hospitalization.repository.listener.InternationListener;
import net.pladema.clinichistory.ips.repository.masterdata.entity.ProceduresStatus;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "procedures")
@EntityListeners(InternationListener.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Procedures extends InternationAuditableEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -3053291021636483828L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Integer id;

	@Column(name = "patient_id", nullable = false)
	private Integer patientId;

	@Column(name = "sctid_code", length = 20, nullable = false)
	private String sctidCode;

	@Column(name = "status_id", length = 20, nullable = false)
	private String statusId = ProceduresStatus.COMPLETE;

	@Column(name = "performed_date", nullable = false)
	private LocalDate performedDate;

	@Column(name = "note_id")
	private Long noteId;

	public Procedures(Integer patientId, String sctId, String statusId, LocalDate performedDate, Long noteId) {
		super();
		this.patientId = patientId;
		this.sctidCode = sctId;
		if (statusId != null)
			this.statusId = statusId;
		this.performedDate = performedDate;
		this.noteId = noteId;
	}
}
