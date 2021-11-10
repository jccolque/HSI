package net.pladema.permissions.controller.external;


import net.pladema.permissions.repository.enums.ERole;
import net.pladema.permissions.service.LoggedUserService;
import net.pladema.permissions.service.dto.RoleAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class LoggedUserExternalServiceImpl implements LoggedUserExternalService {

	private static final Logger LOG = LoggerFactory.getLogger(LoggedUserExternalServiceImpl.class);
	public static final String OUTPUT = "Output -> {}";

	private final LoggedUserService loggedUserService;

	public LoggedUserExternalServiceImpl(LoggedUserService loggedUserService) {
		this.loggedUserService = loggedUserService;
	}


	@Override
	public List<RoleAssignment> getPermissionAssignment() {
		LOG.debug("Without input parameters");
		List<RoleAssignment> result = loggedUserService.getPermissionAssignment();
		LOG.debug(OUTPUT, result);
		return result;
	}

	@Override
	public boolean hasAnyRoleInstitution(Integer institutionId, ERole... roles) {
		LOG.debug("Input parameters -> institutionId {}, roles {}", institutionId, roles);
		boolean result = this.hasAnyRoleInstitution(roles).apply(institutionId);
		LOG.debug(OUTPUT, result);
		return result;
	}

	@Override
	public Function<Integer, Boolean> hasAnyRoleInstitution(ERole... roles) {
		return loggedUserService.hasAnyInstitutionRole(roles);
	}

}
