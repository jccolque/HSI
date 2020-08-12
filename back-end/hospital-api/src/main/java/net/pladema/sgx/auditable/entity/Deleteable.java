package net.pladema.sgx.auditable.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
public class Deleteable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1845551268050864290L;

	@Column(name = "deleted", nullable = false)
	private Boolean deleted = false;

	@Column(name = "deleted_on")
	private LocalDateTime deletedOn;
	
	@Column(name = "deleted_by")
	private Integer deletedBy;

	public boolean isDeleted() {
		return deleted;
	}

}