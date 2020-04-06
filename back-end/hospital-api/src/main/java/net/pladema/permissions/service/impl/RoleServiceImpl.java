package net.pladema.permissions.service.impl;

import net.pladema.permissions.repository.RoleRepository;
import net.pladema.permissions.repository.UserRoleRepository;
import net.pladema.permissions.repository.entity.Role;
import net.pladema.permissions.repository.entity.UserRole;
import net.pladema.permissions.repository.enums.ERole;
import net.pladema.permissions.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

	private static final String INVALID_ROLE = "role.invalid";

	private static final String INVALID_USER = "invalid.iduser";

	private static final Logger LOG = LoggerFactory.getLogger(RoleServiceImpl.class);

	private final UserRoleRepository userRoleRepository;

	private final RoleRepository roleRepository;

	public RoleServiceImpl(UserRoleRepository userLicenseRepository, RoleRepository roleRepository) {
		super();
		this.userRoleRepository = userLicenseRepository;
		this.roleRepository = roleRepository;
	}

	@Override
	public UserRole createUserRole(Integer userId, ERole eRole) {
		Role role = roleRepository.findByDescription(eRole.getValue())
				.orElseThrow(() -> new EntityNotFoundException(INVALID_ROLE));
		return saveRole(userId, role.getId());
	}

	@Override
	public UserRole createUserRole(Integer userId, Short roleId) {
		return saveRole(userId, roleId);
	}

	protected UserRole saveRole(Integer userId, Short roleId) {
		UserRole userRole = new UserRole(userId, roleId);
		userRole = userRoleRepository.save(userRole);
		LOG.debug("{}", "Role creada");
		return userRole;
	}

	@Override
	public List<String> getAuthoritiesClaims(Integer userId) {
		List<String> authorities = userRoleRepository.getActiveRole(userId, PageRequest.of(0, 1)).getContent();
		LOG.debug("Generated authorities {}", authorities);
		return authorities;
	}

	@Override
	public void saveRoles(List<Role> licences) {
		roleRepository.saveAll(licences);
	}

	@Override
	public boolean existRole(String license) {
		return roleRepository.existLicense(license);
	}

	@Override
	public boolean existRole(Short licenseId) {
		return roleRepository.existsById(licenseId);
	}

	@Override
	public void updateAdminRole(Integer userId, ERole admin) {
		Role role = roleRepository.findByDescription(admin.getValue())
				.orElseThrow(() -> new EntityNotFoundException(INVALID_ROLE));
		userRoleRepository.updateRole(userId, role.getId());
	}

	@Override
	public void deleteByUserId(Integer userId) {
		userRoleRepository.deleteByUserId(userId);
	}
}
