import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
	HCEImmunizationDto,
	HCEMedicationDto,
	HCELast2RiskFactorsDto,
	HCEAnthropometricDataDto,
	HCEPersonalHistoryDto,
	HCEAllergyDto,
	HCEHospitalizationHistoryDto,
	HCEToothRecordDto, HCEEvolutionSummaryDto,
	ExternalPatientCoverageDto
} from '@api-rest/api-model';
import { HttpClient } from '@angular/common/http';
import { ContextService } from '@core/services/context.service';
import { environment } from '@environments/environment';

@Injectable({
	providedIn: 'root'
})
export class HceGeneralStateService {

	private readonly HCE_URL_BASE = `${environment.apiBase}/institutions/${this.contextService.institutionId}/patient/`;

	constructor(
		private http: HttpClient,
		private contextService: ContextService
	) {
	}

	getAllergies(patientId: number): Observable<HCEAllergyDto[]> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/allergies`;
		return this.http.get<HCEAllergyDto[]>(url);
	}

	getCriticalAllergies(patientId: number): Observable<HCEAllergyDto[]> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/critical-allergies`;
		return this.http.get<HCEAllergyDto[]>(url);
	}

	getFamilyHistories(patientId: number): Observable<HCEPersonalHistoryDto[]> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/familyHistories`;
		return this.http.get<HCEPersonalHistoryDto[]>(url);
	}

	getPersonalHistories(patientId: number): Observable<HCEPersonalHistoryDto[]> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/personalHistories`;
		return this.http.get<HCEPersonalHistoryDto[]>(url);
	}

	getMedications(patientId: number): Observable<HCEMedicationDto[]> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/medications`;
		return this.http.get<HCEMedicationDto[]>(url);
	}

	getImmunizations(patientId: number): Observable<HCEImmunizationDto[]> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/immunizations`;
		return this.http.get<HCEImmunizationDto[]>(url);
	}

	getRiskFactors(patientId: number): Observable<HCELast2RiskFactorsDto> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/riskFactors`;
		return this.http.get<HCELast2RiskFactorsDto>(url);
	}

	getAnthropometricData(patientId: number): Observable<HCEAnthropometricDataDto> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/anthropometricData`;
		return this.http.get<HCEAnthropometricDataDto>(url);
	}

	getActiveProblems(patientId: number): Observable<HCEPersonalHistoryDto[]> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/activeProblems`;
		return this.http.get<HCEPersonalHistoryDto[]>(url);
	}

	getSolvedProblems(patientId: number): Observable<HCEPersonalHistoryDto[]> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/solvedProblems`;
		return this.http.get<HCEPersonalHistoryDto[]>(url);
	}

	getChronicConditions(patientId: number): Observable<HCEPersonalHistoryDto[]> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/chronic`;
		return this.http.get<HCEPersonalHistoryDto[]>(url);
	}

	getHospitalizationHistory(patientId: number): Observable<HCEHospitalizationHistoryDto[]> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/hospitalization`;
		return this.http.get<HCEHospitalizationHistoryDto[]>(url);
	}

	getToothRecords(patientId: number, toothSctid: string): Observable<HCEToothRecordDto[]> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/toothRecords/tooth/${toothSctid}`;
		return this.http.get<HCEToothRecordDto[]>(url);
	}

	getEvolutionSummaryList(patientId: number): Observable<HCEEvolutionSummaryDto[]> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/summary-list`;
		return this.http.get<HCEEvolutionSummaryDto[]>(url);
	}

	getInternmentEpisodeMedicalCoverage(patientId: number, internmentId: number): Observable<ExternalPatientCoverageDto> {
		const url = this.HCE_URL_BASE + `${patientId}/hce/general-state/active-internment-episode/${internmentId}/medical-coverage`;
		return this.http.get<ExternalPatientCoverageDto>(url);
	}

}
