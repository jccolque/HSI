import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PatientMedicalCoverageService } from '@api-rest/services/patient-medical-coverage.service';
import { MedicalCoverageComponent, PatientMedicalCoverage } from '@core/dialogs/medical-coverage/medical-coverage.component';
import { MapperService } from '@core/services/mapper.service';
import { map } from 'rxjs/operators';
import { AgregarPrescripcionItemComponent, NewPrescriptionItem } from '../agregar-prescripcion-item/agregar-prescripcion-item.component';
import { BasicPatientDto, PatientMedicalCoverageDto, PrescriptionDto } from '@api-rest/api-model';
import { PrescriptionTypes } from '../../../services/prescripciones.service';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { PatientService } from '@api-rest/services/patient.service';

@Component({
  selector: 'app-nueva-prescripcion',
  templateUrl: './nueva-prescripcion.component.html',
  styleUrls: ['./nueva-prescripcion.component.scss']
})
export class NuevaPrescripcionComponent implements OnInit {

	prescriptionItems: NewPrescriptionItem[];
	patientMedicalCoverages: PatientMedicalCoverage[];
	prescriptionForm: FormGroup;
	itemCount = 0;
	private patientData: BasicPatientDto;

	constructor(
		private readonly formBuilder: FormBuilder,
		private readonly mapperService: MapperService,
		private readonly patientMedicalCoverageService: PatientMedicalCoverageService,
		private readonly dialog: MatDialog,
		private readonly snackBarService: SnackBarService,
		private readonly patientService: PatientService,
		public dialogRef: MatDialogRef<NuevaPrescripcionComponent>,
		@Inject(MAT_DIALOG_DATA) public data: NewPrescriptionData) { }

	ngOnInit(): void {
		this.prescriptionForm = this.formBuilder.group({
			patientMedicalCoverage: [null],
			withoutRecipe: [false],
		});
		this.prescriptionItems = this.data.prescriptionItemList ? this.data.prescriptionItemList : [];
		this.setMedicalCoverages();
		this.patientService.getPatientBasicData(Number(this.data.patientId)).subscribe((basicData: BasicPatientDto) => {
			this.patientData = basicData;
		});
	}

	closeModal(newPrescription?: PrescriptionDto): void {
		this.dialogRef.close(newPrescription);
	}

	openPrescriptionItemDialog(item?: NewPrescriptionItem): void {
		const newPrescriptionItemDialog = this.dialog.open(AgregarPrescripcionItemComponent,
		{
			data: {
				patientId: this.data.patientId,
				titleLabel: this.data.addPrescriptionItemDialogData.titleLabel,
				searchSnomedLabel: this.data.addPrescriptionItemDialogData.searchSnomedLabel,
				showDosage: this.data.addPrescriptionItemDialogData.showDosage,
				showStudyCategory: this.data.addPrescriptionItemDialogData.showStudyCategory,
				eclTerm: this.data.addPrescriptionItemDialogData.eclTerm,
				item: item,
			},
			width: '35%',
		});

		newPrescriptionItemDialog.afterClosed().subscribe((prescriptionItem: NewPrescriptionItem) => {
			if (prescriptionItem) {
				if (!prescriptionItem.id) {
					prescriptionItem.id = ++this.itemCount;
					this.prescriptionItems.push(prescriptionItem);
				} else {
					this.editPrescriptionItem(prescriptionItem);
				}
			}
		});
	}

	confirmPrescription(): void {
		const newPrescription: PrescriptionDto = {
			hasRecipe: this.isMedication ? !this.prescriptionForm.controls.withoutRecipe.value : true,
			medicalCoverageId: this.prescriptionForm.controls.patientMedicalCoverage.value?.id,
			items: this.prescriptionItems.map(pi => {
				return {
					healthConditionId: pi.healthProblem.id,
					observations: pi.observations,
					snomed: pi.snomed,
					categoryId: pi.studyCategory?.id,
					dosage: {
						chronic: pi.isChronicAdministrationTime,
						diary: pi.isDailyInterval,
						duration: Number(pi.administrationTimeDays),
						frequency: Number(pi.intervalHours)
					}
				}
			})
		}

		this.closeModal(newPrescription);
	}

	deletePrescriptionItem(prescriptionItem: NewPrescriptionItem): void {
		this.prescriptionItems.splice(this.prescriptionItems.findIndex(item => item.id === prescriptionItem.id) , 1);
	}

	getDosage(prescriptionItem: NewPrescriptionItem): string {
		const intervalText = prescriptionItem.isDailyInterval ? 'Diario - ' : `Cada ${prescriptionItem.intervalHours} hs - `;
		const administrationTimeText = prescriptionItem.isChronicAdministrationTime ? 'Habitual' : `Durante ${prescriptionItem.administrationTimeDays} días`;
		return (intervalText + administrationTimeText);
	}

	getFullMedicalCoverageText(patientMedicalCoverage): string {
		const medicalCoverageText = [patientMedicalCoverage.medicalCoverage.acronym, patientMedicalCoverage.medicalCoverage.name]
			.filter(Boolean).join(' - ');
		return [medicalCoverageText, patientMedicalCoverage.affiliateNumber].filter(Boolean).join(' / ');
	}

	isMedication(): boolean {
		return this.data.prescriptionType === PrescriptionTypes.MEDICATION;
	}

	private editPrescriptionItem(prescriptionItem: NewPrescriptionItem): void {
		let editPrescriptionItem = this.prescriptionItems.find(pi => pi.id === prescriptionItem.id);

		editPrescriptionItem.snomed = prescriptionItem.snomed;
		editPrescriptionItem.healthProblem = prescriptionItem.healthProblem;
		editPrescriptionItem.administrationTimeDays = prescriptionItem.administrationTimeDays;
		editPrescriptionItem.isChronicAdministrationTime = prescriptionItem.isChronicAdministrationTime;
		editPrescriptionItem.intervalHours = prescriptionItem.intervalHours;
		editPrescriptionItem.isDailyInterval = prescriptionItem.isDailyInterval;
		editPrescriptionItem.studyCategory = prescriptionItem.studyCategory;
		editPrescriptionItem.observations = prescriptionItem.observations;
	}

	private setMedicalCoverages(): void {
		this.patientMedicalCoverageService.getActivePatientMedicalCoverages(Number(this.data.patientId))
			.pipe(
				map(
					patientMedicalCoveragesDto =>
						patientMedicalCoveragesDto.map(s => this.mapperService.toPatientMedicalCoverage(s))
				)
			)
			.subscribe((patientMedicalCoverages: PatientMedicalCoverage[]) => this.patientMedicalCoverages = patientMedicalCoverages);
	}

	openMedicalCoverageDialog(): void {
		const dialogRef = this.dialog.open(MedicalCoverageComponent, {
			data: {
				genderId: this.patientData.person.gender.id,
				identificationNumber: this.patientData.person.identificationNumber,
				identificationTypeId: this.patientData.person.identificationTypeId,
				initValues: this.patientMedicalCoverages,
			}
		});

		dialogRef.afterClosed().subscribe(
			values => {
				if (values) {
					const patientCoverages: PatientMedicalCoverageDto[] =
						values.patientMedicalCoverages.map(s => this.mapperService.toPatientMedicalCoverageDto(s));

					this.patientMedicalCoverageService.addPatientMedicalCoverages(Number(this.data.patientId), patientCoverages).subscribe(_ => {
						this.setMedicalCoverages();
						this.snackBarService.showSuccess('ambulatoria.paciente.ordenes_prescripciones.toast_messages.POST_UPDATE_COVERAGE_SUCCESS');
					}), _ => this.snackBarService.showError('ambulatoria.paciente.ordenes_prescripciones.toast_messages.POST_UPDATE_COVERAGE_ERROR');
				}
			}
		);
	}

}

export class NewPrescriptionData {
	patientId: string;
	titleLabel: string;
	addLabel: string;
	prescriptionType: PrescriptionTypes;
	prescriptionItemList: NewPrescriptionItem[];
	addPrescriptionItemDialogData: {
		titleLabel: string;
		searchSnomedLabel: string;
		showDosage: boolean;
		showStudyCategory: boolean;
		eclTerm: string;
	}
}
