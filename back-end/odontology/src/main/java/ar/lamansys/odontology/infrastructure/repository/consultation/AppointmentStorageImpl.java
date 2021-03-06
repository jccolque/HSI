package ar.lamansys.odontology.infrastructure.repository.consultation;

import ar.lamansys.odontology.domain.consultation.AppointmentStorage;
import ar.lamansys.sgh.shared.infrastructure.input.service.SharedAppointmentPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AppointmentStorageImpl implements AppointmentStorage {

    private static final Logger LOG = LoggerFactory.getLogger(AppointmentStorageImpl.class);

    private final SharedAppointmentPort sharedAppointmentPort;

    private final boolean disableValidation;

    public AppointmentStorageImpl(@Value("${test.stress.disable.validation:false}") boolean disableValidation,
                                  SharedAppointmentPort sharedAppointmentPort) {
        this.sharedAppointmentPort = sharedAppointmentPort;
        this.disableValidation = disableValidation;
    }

    @Override
    public Integer getPatientMedicalCoverageId(Integer patientId, Integer doctorId) {
        LOG.debug("Input parameters -> patientId {}, doctorId {}", patientId, doctorId);
        Integer result = sharedAppointmentPort.getMedicalCoverage(patientId, doctorId);
        LOG.debug("Output -> {}", result);
        return result;
    }

    @Override
    public void serveAppointment(Integer patientId, Integer doctorId, LocalDate date) {
        LOG.debug("Input parameters -> patientId {}, doctorId {}, date {}", patientId, doctorId, date);
        if (!disableValidation && sharedAppointmentPort.hasConfirmedAppointment(patientId,doctorId,date))
            sharedAppointmentPort.serveAppointment(patientId, doctorId, date);
    }
}
