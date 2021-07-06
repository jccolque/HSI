package ar.lamansys.immunization.application.FetchVaccines.exceptions;

import ar.lamansys.immunization.domain.vaccine.VaccineExceptionEnum;
import lombok.Getter;

@Getter
public class FetchVaccinesByPatientException extends RuntimeException {
	public final FetchVaccinesByPatientExceptionEnum code;

	public FetchVaccinesByPatientException(FetchVaccinesByPatientExceptionEnum code, String message) {
		super(message);
		this.code = code;
	}
}