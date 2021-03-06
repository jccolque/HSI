package net.pladema.emergencycare.repository.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "emergency_care_entrance_type")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmergencyCareEntranceType implements Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = 1344557556345002647L;

		@Id
		@Column(name = "id")
		private Short id;

		@Column(name = "description", length = 25, nullable = false)
		private String description;
}
