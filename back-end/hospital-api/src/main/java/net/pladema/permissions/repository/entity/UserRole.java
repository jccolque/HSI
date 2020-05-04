package net.pladema.permissions.repository.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import net.pladema.auditable.entity.AuditableEntity;
import net.pladema.auditable.listener.AuditListener;

@Entity
@Table(name = "user_role")
@EntityListeners(AuditListener.class)
@EqualsAndHashCode
public class UserRole extends AuditableEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2826874188803670205L;

	@EmbeddedId
	public UserRolePK userRolePK;

	public UserRole() {

	}

	public UserRole(Integer userId, Short roleId) {
		userRolePK = new UserRolePK(userId, roleId);
	}
	
	public UserRole(Integer userId, Short roleId, Integer institutionId) {
		userRolePK = new UserRolePK(userId, roleId, institutionId);
	}
	
	public void setUserId(Integer userId) {
		if (this.userRolePK == null)
			this.userRolePK = new UserRolePK();
		this.userRolePK.setUserId(userId);
	}

	public void setRoleId(Short roleId) {
		if (this.userRolePK == null)
			this.userRolePK = new UserRolePK();
		this.userRolePK.setRoleId(roleId);
	}
	
	public void setInstitutionId(Integer institutionId) {
		if (this.userRolePK == null)
			this.userRolePK = new UserRolePK();
		this.userRolePK.setInstitutionId(institutionId);
	}
	
	public UserRolePK getUserRolePK() {
		return userRolePK;
	}

	public Integer getUserId() {
		return userRolePK.getUserId();
	}
	
	public Short getRoleId() {
		return userRolePK.getRoleId();
	}

	public Integer getInstitutionId() {
		return userRolePK.getInstitutionId();
	}
}
