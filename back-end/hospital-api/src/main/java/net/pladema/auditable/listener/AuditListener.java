package net.pladema.auditable.listener;

import net.pladema.auditable.Auditable;
import net.pladema.auditable.entity.Audit;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

public class AuditListener {

	@PrePersist
	public void setCreatedOn(Auditable auditable) {
		if (!auditable.getAudit().isPresent()) {
			Audit audit = new Audit();
			audit.setCreatedOn(LocalDateTime.now());
			audit.setUpdatedOn(LocalDateTime.now());
			audit.setCreatedBy(getCurrentAuditor());
			audit.setModifiedBy(getCurrentAuditor());
			auditable.setAudit(audit);
		}
	}

	@PreUpdate
	public void setUpdatedOn(Auditable auditable) {
		auditable.getAudit().ifPresent(a -> {
			a.setUpdatedOn(LocalDateTime.now());
			a.setModifiedBy(getCurrentAuditor());
		});
	}


	public Integer getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated())
			return -1;
		return (Integer) authentication.getPrincipal();
	}

}