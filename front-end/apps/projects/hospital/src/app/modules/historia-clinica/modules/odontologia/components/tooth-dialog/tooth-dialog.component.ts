import { AfterViewInit, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { OdontologyConceptDto, ToothDto, ToothSurfacesDto } from '@api-rest/api-model';
import { ConceptsService } from '../../api-rest/concepts.service';
import { OdontogramService as OdontogramRestService } from '../../api-rest/odontogram.service';
import { ProcedureOrder, ToothAction } from '../../services/actions.service';
import { OdontogramService } from '../../services/odontogram.service';
import { ToothTreatment } from '../../services/surface-drawer.service';
import { getSurfaceShortName } from '../../utils/surfaces';
import { CommonActions, ToothComponent } from '../tooth/tooth.component';

@Component({
	selector: 'app-tooth-dialog',
	templateUrl: './tooth-dialog.component.html',
	styleUrls: ['./tooth-dialog.component.scss']
})
export class ToothDialogComponent implements OnInit, AfterViewInit {

	@ViewChild('tooth') toothComponent: ToothComponent;

	constructor(
		private readonly formBuilder: FormBuilder,
		private readonly odontogramRestService: OdontogramRestService,
		@Inject(MAT_DIALOG_DATA) public data: { tooth: ToothDto, quadrantCode: number, currentActions: ToothAction[] },
		private readonly conceptsService: ConceptsService,
		private dialogRef: MatDialogRef<ToothDialogComponent>,
		public odontogramService: OdontogramService
	) {

	}

	readonly toothTreatment = ToothTreatment.AS_FRACTIONAL_TOOTH;

	form: FormGroup;
	newHallazgoId: number;

	selectedSurfacesText: string;

	selectedSurfaces: string[] = [];

	private surfacesDto: ToothSurfacesDto;

	private diagnostics: OdontologyConceptDto[];
	filteredDiagnostics: OdontologyConceptDto[];

	private procedures: OdontologyConceptDto[];
	filteredProcedures: OdontologyConceptDto[];

	firstProcedureId: string;
	secondProcedureId: string;
	thirdProcedureId: string;

	ngAfterViewInit(): void {
		this.toothComponent.setFindingsAndProcedures(this.data.currentActions);
	}

	ngOnInit(): void {

		this.form = this.formBuilder.group(
			{
				findingId: [undefined],
				procedures: this.formBuilder.group({
					firstProcedureId: [undefined],
					secondProcedureId: [undefined],
					thirdProcedureId: [undefined],
				})
			}
		);

		this.odontogramRestService.getToothSurfaces(this.data.tooth.snomed.sctid).subscribe(surfaces => this.surfacesDto = surfaces);
		const actions = this.data.currentActions?.filter(a => !a.surfaceId);
		const currentProcedures = actions?.filter(a => a.isProcedure);
		const currentFinding = actions?.find(a => !a.isProcedure);
		this.conceptsService.getDiagnostics().subscribe(
			diagnostics => {
				this.diagnostics = diagnostics;
				this.filteredDiagnostics = diagnostics;
				this.form.controls.findingId.patchValue(currentFinding?.actionSctid);
			}
		);
		this.conceptsService.getProcedures().subscribe(
			procedures => {
				this.procedures = procedures;
				this.filteredProcedures = procedures;
				this.form.controls.procedures.patchValue({
					firstProcedureId: currentProcedures?.find(p => p.wholeProcedureOrder === ProcedureOrder.FIRST)?.actionSctid,
					secondProcedureId: currentProcedures?.find(p => p.wholeProcedureOrder === ProcedureOrder.SECOND)?.actionSctid,
					thirdProcedureId: currentProcedures?.find(p => p.wholeProcedureOrder === ProcedureOrder.THIRD)?.actionSctid,
				})
			}
		);
	}

	confirm() {
		this.dialogRef.close(this.toothComponent.getFindingsAndProcedures());
	}

	reciveSelectedSurfaces(surfaces: string[]) {
		this.selectedSurfaces = surfaces;
		this.concatNames();

		let filterFuncion = (diagnostic: OdontologyConceptDto) => { return diagnostic.applicableToTooth }
		if (surfaces.length) {
			filterFuncion = (diagnostic: OdontologyConceptDto) => { return diagnostic.applicableToSurface }
		}

		this.filteredDiagnostics = this.diagnostics?.filter(filterFuncion);
		this.filteredProcedures = this.procedures?.filter(filterFuncion);
	}

	findingChanged(hallazgoId) {
		this.newHallazgoId = hallazgoId.value;
	}

	reciveCommonActions(inCommon: CommonActions) {
		if (this.form) {
			if (inCommon?.findingId) {
				this.form.controls.findingId.patchValue(inCommon.findingId);
			} else {
				this.form.controls.findingId.patchValue(undefined);
				this.newHallazgoId = undefined;
			}
			const procedures = {
				firstProcedureId: inCommon.procedures?.firstProcedureId,
				secondProcedureId: inCommon.procedures?.secondProcedureId,
				thirdProcedureId: inCommon.procedures?.thirdProcedureId
			};
			this.form.patchValue({
				procedures
			});
		}
	}

	private concatNames() {
		this.selectedSurfacesText = '';
		if (this.selectedSurfaces.length) {
			this.selectedSurfacesText = this.selectedSurfaces.length === 1 ? 'Cara ' : 'Caras ';
			const mappedNames = this.selectedSurfaces.map(surface => this.findSutableName(surface));
			this.selectedSurfacesText += mappedNames.filter(Boolean).join(', ');
		}
	}

	private findSutableName(surface: string): string {
		const sctid = this.surfacesDto[surface].sctid;
		return getSurfaceShortName(sctid);
	}

	firstProcedureChanged() {
		this.firstProcedureId = this.form.value.procedures.firstProcedureId;
	}

	secondProcedureChanged() {
		this.secondProcedureId = this.form.value.procedures.secondProcedureId;
	}

	thirdProcedureChanged() {
		this.thirdProcedureId = this.form.value.procedures.thirdProcedureId;
	}

}

