package net.pladema.user.infrastructure.output;

import lombok.RequiredArgsConstructor;
import net.pladema.permissions.repository.UserRoleRepository;
import net.pladema.permissions.repository.entity.UserRole;
import net.pladema.permissions.repository.enums.ERole;
import net.pladema.permissions.service.RoleService;
import net.pladema.staff.repository.HealthcareProfessionalRepository;
import net.pladema.user.application.port.UserRoleStorage;
import net.pladema.user.application.port.exceptions.UserPersonStorageEnumException;
import net.pladema.user.application.port.exceptions.UserPersonStorageException;
import net.pladema.user.application.port.exceptions.UserRoleStorageEnumException;
import net.pladema.user.application.port.exceptions.UserRoleStorageException;
import net.pladema.user.domain.UserRoleBo;
import net.pladema.user.repository.UserPersonRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleStorageImpl implements UserRoleStorage {

    private final UserRoleRepository userRoleRepository;

    private final HealthcareProfessionalRepository healthcareProfessionalRepository;

    private final UserPersonRepository userPersonRepository;

    private final RoleService roleService;

    @Override
    public List<UserRoleBo> getRolesByUser(Integer userId) {
        List<UserRoleBo> result = new ArrayList<>();
        userRoleRepository.getRoleAssignments(userId)
                .forEach(r ->
                        result.add(new UserRoleBo(
                                r.getRole().getId(),
                                roleService.getRoleDescription(r.getRole()),
                                userId,
                                r.institutionId)));
        return result;
    }

    @Override
    public void updateUserRole(List<UserRoleBo> userRolesBo, Integer userId) {
        checkValidRoles(userRolesBo);
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        List<UserRole> newUserRoles = new ArrayList<>();
        userRolesBo.forEach(userRoleBo -> newUserRoles.add(new UserRole(userId, userRoleBo.getRoleId(), userRoleBo.getInstitutionId())));
        userRoleRepository.deleteAll(roleToDelete(userRoles, newUserRoles));
        userRoleRepository.saveAll(newUserRoles);
    }

    private List<UserRole> roleToDelete(List<UserRole> currentRoles, List<UserRole> newRoles) {
        return currentRoles.stream()
                .filter(userRole ->
                        newRoles.stream().noneMatch(userRole::equals)
                )
                .collect(Collectors.toList());
    }


    private void checkValidRoles(List<UserRoleBo> userRolesBo) {
        userRolesBo.stream().filter(role -> !isValidRole(role)).findAny()
                .ifPresent(userRoleDto -> {
                    String role = ERole.map(userRoleDto.getRoleId()).getValue();
                    throw new UserRoleStorageException(UserRoleStorageEnumException.PROFESSIONAL_REQUIRED,
                            String.format("El rol %s asignado requiere que el usuario sea un profesional", role));
                });
    }

    private boolean isValidRole(UserRoleBo role) {
        return !isProfessional(role) || userPersonRepository.getPersonIdByUserId(role.getUserId())
                .map(personId -> healthcareProfessionalRepository.findProfessionalByPersonId(personId).isPresent())
                .orElseThrow(() -> new UserPersonStorageException(UserPersonStorageEnumException.UNEXISTED_USER, "El usuario no existe"));
    }

    private boolean isProfessional(UserRoleBo role) {
        Short roleId = role.getRoleId();
        return ERole.ENFERMERO.getId().equals(roleId) ||
                ERole.ESPECIALISTA_MEDICO.getId().equals(roleId) ||
                ERole.ENFERMERO_ADULTO_MAYOR.getId().equals(roleId) ||
                ERole.PROFESIONAL_DE_SALUD.getId().equals(roleId) ||
                ERole.ESPECIALISTA_EN_ODONTOLOGIA.getId().equals(roleId);
    }
}
