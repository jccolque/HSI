import { Component, OnInit, Input } from '@angular/core';
import { HCEImmunizationDto, OutpatientImmunizationDto, ProfessionalInfoDto } from '@api-rest/api-model';
import { AppFeature } from '@api-rest/api-model';
import { HceGeneralStateService } from '@api-rest/services/hce-general-state.service';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AplicarVacunaComponent } from '../../dialogs/aplicar-vacuna/aplicar-vacuna.component';
import { AddInmunizationComponent, Immunization } from '@historia-clinica/dialogs/add-inmunization/add-inmunization.component';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { HceImmunizationService } from '@api-rest/services/hce-immunization.service';
import { VACUNAS } from '@historia-clinica/constants/summaries';
import { AppointmentsService } from '@api-rest/services/appointments.service';
import { AgregarVacunasComponent } from '../../dialogs/agregar-vacunas/agregar-vacunas.component';
import { FeatureFlagService } from '@core/services/feature-flag.service';
import { Observable } from 'rxjs';
import { DetalleVacunaComponent } from '../../dialogs/detalle-vacuna/detalle-vacuna.component';

@Component({
	selector: 'app-vacunas',
	templateUrl: './vacunas.component.html',
	styleUrls: ['./vacunas.component.scss']
})
export class VacunasComponent implements OnInit {

	enableVaccineV2: boolean; // for feature flag
	public hasNewConsultationEnabled$: Observable<boolean>;

	private patientId: number;
	public readonly vacunasSummary = VACUNAS;
	public vaccines: HCEImmunizationDto[];

	@Input() hasConfirmedAppointment: boolean;
	dialogRef: any;

	constructor(
		private readonly hceGeneralStateService: HceGeneralStateService,
		private readonly route: ActivatedRoute,
		private readonly appointmentsService: AppointmentsService,
		public dialog: MatDialog,
		private readonly snackBarService: SnackBarService,
		private readonly hceImmunizationService: HceImmunizationService,
		private featureFlagService: FeatureFlagService
	) {
	}

	ngOnInit(): void {
		this.featureFlagService.isActive(AppFeature.HABILITAR_VACUNAS_V2).subscribe(
			(isOn: boolean) => {
				this.enableVaccineV2 = isOn;
			}
		);

		this.route.paramMap.subscribe(
			(params) => {
				this.patientId = Number(params.get('idPaciente'));
				this.hceGeneralStateService.getImmunizations(this.patientId).subscribe(dataTable => {
					this.vaccines = dataTable;
				});
			});
	}

	goToAplicarVacuna() {
		const dialogRef = this.dialog.open(AplicarVacunaComponent, {
			disableClose: true,
			width: '45%',
			data: {
				patientId: this.patientId
			}
		});

		dialogRef.afterClosed().subscribe(submitted => {
			if (submitted) {
				this.hceGeneralStateService.getImmunizations(this.patientId).subscribe(dataTable => {
					this.vaccines = dataTable;
				});
				this.appointmentsService.hasNewConsultationEnabled(this.patientId).subscribe(response => {
					this.hasConfirmedAppointment = response;
				});
			}
		});
	}

	goToAgregarVacunas() {
		const dialogRef = this.dialog.open(AgregarVacunasComponent, {
			disableClose: true,
			width: '40%',
			data: {
				patientId: this.patientId
			}
		});

		dialogRef.afterClosed().subscribe(submitted => {
			if (submitted) {
				this.hceGeneralStateService.getImmunizations(this.patientId).subscribe(dataTable => {
					this.vaccines = dataTable;
				});
				this.appointmentsService.hasNewConsultationEnabled(this.patientId).subscribe(response => {
					this.hasConfirmedAppointment = response;
				});
			}
		});
	}

	openDialog() {
		const dialogRef = this.dialog.open(AddInmunizationComponent, {
			disableClose: true
		});

		dialogRef.afterClosed().subscribe(submitted => {
			if (submitted) {
				this.hceImmunizationService.updateImmunization(this.buildApplyImmunization(submitted), this.patientId).subscribe(_ => {
					this.hceGeneralStateService.getImmunizations(this.patientId).subscribe(dataTable => {
						this.vaccines = dataTable;
					});
					this.snackBarService.showSuccess('internaciones.internacion-paciente.vacunas-summary.save.SUCCESS');

				}, _ => {
					this.snackBarService.showError('internaciones.internacion-paciente.vacunas-summary.save.ERROR');

				});
			}
		});
	}

	private buildApplyImmunization(immunization: Immunization): OutpatientImmunizationDto {
		return {
			administrationDate: immunization.administrationDate,
			note: null,
			snomed: immunization.snomed
		};
	}

	goToDetailsVaccine(vaccine: HCEImmunizationDto) {
		const dialogRef = this.dialog.open(DetalleVacunaComponent, {
			disableClose: false,
			width: '30%',
			data: {
				vaccineTitleName: vaccine.snomed.pt,
				appliedDoses: vaccine.dose?.description,
				applicationDate: vaccine.administrationDate,
				lotNumber: vaccine.lotNumber,
				institutionName: vaccine.institution?.name,
				ProfessionalCompleteName:this.setCompletName(vaccine.doctor),
				vaccineConditinDescription: vaccine.condition?.description,
				vaccinationSchemeDescription: vaccine.scheme?.description,
				vaccineObservations: vaccine.note,
			}
		});
	}

	setCompletName(doctor:ProfessionalInfoDto){
		if (doctor==null)
			return null
		if (doctor.firstName!=null && doctor.lastName==null)
			return doctor.firstName
		if (doctor.firstName==null && doctor.lastName!=null)
			return doctor?.lastName
		if (doctor.firstName!=null && doctor.lastName!=null)
			return doctor.firstName +' '+doctor?.lastName
		return null
	}
}
