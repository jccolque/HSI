package net.pladema.medicalconsultation.appointment.service.impl;

import ar.lamansys.sgx.shared.security.UserInfo;
import net.pladema.medicalconsultation.appointment.service.AppointmentService;
import net.pladema.medicalconsultation.appointment.service.AppointmentValidatorService;
import net.pladema.medicalconsultation.appointment.service.domain.AppointmentBo;
import net.pladema.medicalconsultation.diary.service.DiaryService;
import net.pladema.medicalconsultation.diary.service.domain.DiaryBo;
import net.pladema.permissions.controller.external.LoggedUserExternalService;
import net.pladema.permissions.repository.enums.ERole;
import net.pladema.staff.service.HealthcareProfessionalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static net.pladema.medicalconsultation.appointment.repository.entity.AppointmentState.*;

@Service
public class AppointmentValidatorServiceImpl implements AppointmentValidatorService {

	public static final String OUTPUT = "Output -> {}";
	private static final Logger LOG = LoggerFactory.getLogger(AppointmentValidatorServiceImpl.class);

	private HashMap<Short, Collection<Short>> validStates;

	private Collection<Short> statesWithReason;

	private final DiaryService diaryService;
    private final HealthcareProfessionalService healthcareProfessionalService;
	private final AppointmentService appointmentService;
	private final Function<Integer, Boolean> hasAdministrativeRole;
	private final Function<Integer, Boolean> hasProfessionalRole;

	public AppointmentValidatorServiceImpl(DiaryService diaryService,
			HealthcareProfessionalService healthcareProfessionalService, AppointmentService appointmentService,
			LoggedUserExternalService loggedUserExternalService) {
		this.diaryService = diaryService;
		this.healthcareProfessionalService = healthcareProfessionalService;
		this.appointmentService = appointmentService;
		this.hasAdministrativeRole = loggedUserExternalService.hasAnyRoleInstitution(
				ERole.ADMINISTRADOR_AGENDA, ERole.ADMINISTRATIVO
		);
		this.hasProfessionalRole = loggedUserExternalService.hasAnyRoleInstitution(
				ERole.ESPECIALISTA_MEDICO, ERole.PROFESIONAL_DE_SALUD, ERole.ESPECIALISTA_EN_ODONTOLOGIA, ERole.ENFERMERO
		);

		validStates = new HashMap<>();
		validStates.put(ASSIGNED, Arrays.asList(CONFIRMED, CANCELLED));
		validStates.put(CONFIRMED, Arrays.asList(ABSENT, CANCELLED));
		validStates.put(ABSENT, Arrays.asList(CONFIRMED,ABSENT));
		validStates.put(SERVED, Arrays.asList());
		validStates.put(CANCELLED, Arrays.asList(CANCELLED));
		statesWithReason = Arrays.asList(CANCELLED, ABSENT);
	}

	@Override
	public boolean validateStateUpdate(Integer institutionId, Integer appointmentId, short appointmentStateId, String reason) {
		LOG.debug("Input parameters -> appointmentId {}, appointmentStateId {}, reason {}", appointmentId,
				appointmentStateId, reason);
		Optional<AppointmentBo> apmtOpt = appointmentService.getAppointment(appointmentId);

		if (apmtOpt.isPresent() && !validStateTransition(appointmentStateId, apmtOpt.get())) {
			throw new ValidationException("appointment.state.transition.invalid");
		}
		if (!validReason(appointmentStateId, reason)) {
			throw new ValidationException("appointment.state.reason.invalid");
		}
		validateRole(institutionId, apmtOpt);
		LOG.debug(OUTPUT, Boolean.TRUE);
		return Boolean.TRUE;
	}

	private void validateRole(Integer institutionId, Optional<AppointmentBo> apmtOpt) {

        if (apmtOpt.isPresent() && !hasAdministrativeRole.apply(institutionId)) {
        	DiaryBo diary = diaryService.getDiaryById(apmtOpt.get().getDiaryId());

            Integer professionalId = healthcareProfessionalService.getProfessionalId(UserInfo.getCurrentAuditor());
            if (hasProfessionalRole.apply(institutionId) && !diary.getHealthcareProfessionalId().equals(professionalId)) {
            	throw new ValidationException("appointment.new.professional.id.invalid}");
            }
        }
	}

	private boolean validReason(short appointmentStateId, String reason) {
		return !statesWithReason.contains(appointmentStateId) || reason != null;
	}

	private boolean validStateTransition(short appointmentStateId, AppointmentBo apmt) {
		return validStates.get(apmt.getAppointmentStateId()).contains(Short.valueOf(appointmentStateId));
	}

}
