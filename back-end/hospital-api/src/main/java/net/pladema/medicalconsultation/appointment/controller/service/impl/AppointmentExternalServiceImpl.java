package net.pladema.medicalconsultation.appointment.controller.service.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import ar.lamansys.sgh.shared.infrastructure.input.service.SharedAppointmentPort;
import ar.lamansys.sgx.shared.security.UserInfo;
import lombok.extern.slf4j.Slf4j;
import net.pladema.medicalconsultation.appointment.controller.service.AppointmentExternalService;
import net.pladema.medicalconsultation.appointment.repository.entity.AppointmentState;
import net.pladema.medicalconsultation.appointment.service.AppointmentService;

@Slf4j
@Service
public class AppointmentExternalServiceImpl implements AppointmentExternalService, SharedAppointmentPort {

	private static final String OUTPUT = "Output -> {}";

	private final AppointmentService appointmentService;

	public AppointmentExternalServiceImpl(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@Override
	public boolean hasConfirmedAppointment(Integer patientId, Integer healthProfessionalId, LocalDate date) {
		log.debug("Input parameters -> patientId {}, healthProfessionalId {}, date {}", patientId, healthProfessionalId, date);
		boolean result = appointmentService.hasConfirmedAppointment(patientId, healthProfessionalId, date);
		log.debug(OUTPUT, result);
		return result;
	}

	@Override
	public void serveAppointment(Integer patientId, Integer healthcareProfessionalId, LocalDate date) {
		log.debug("Input parameters -> patientId {}, healthcareProfessionalId {}, date {}", patientId, healthcareProfessionalId, date);
		Integer appointmentId = appointmentService.getAppointmentsId(patientId, healthcareProfessionalId, date).get(0);
		appointmentService.updateState(appointmentId, AppointmentState.SERVED, UserInfo.getCurrentAuditor(), null);
		log.debug(OUTPUT, Boolean.TRUE);
	}

	@Override
	public Integer getMedicalCoverage(Integer patientId, Integer healthcareProfessionalId) {
		log.debug("Appointment Service -> method: {}", "getMedicalCoverage");
		log.debug("Input parameters -> patientId {}, healthcareProfessionalId {}", patientId, healthcareProfessionalId);

		LocalDate currentDate = LocalDate.now();
		Integer medicalCoverage = appointmentService
				.getMedicalCoverage(patientId, healthcareProfessionalId, currentDate);
		log.debug(OUTPUT, medicalCoverage);
		return medicalCoverage;
	}

}
