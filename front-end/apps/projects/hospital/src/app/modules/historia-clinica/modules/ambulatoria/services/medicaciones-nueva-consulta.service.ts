import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SnomedDto, SnomedECL} from '@api-rest/api-model';
import { ColumnConfig } from '@presentation/components/document-section/document-section.component';
import { SnomedSemanticSearch, SnomedService } from '../../../services/snomed.service';
import { pushIfNotExists, removeFrom } from '@core/utils/array.utils';
import { TEXT_AREA_MAX_LENGTH } from '@core/constants/validation-constants';
import { TableColumnConfig } from "@presentation/components/document-section-table/document-section-table.component";
import { CellTemplates } from "@presentation/components/cell-templates/cell-templates.component";
import { SnackBarService } from '@presentation/services/snack-bar.service';
export interface Medicacion {
	snomed: SnomedDto;
	observaciones?: string;
	suspendido?: boolean;
}

export class MedicacionesNuevaConsultaService {

	private form: FormGroup;
	private snomedConcept: SnomedDto;
	private readonly columns: ColumnConfig[];
	private readonly tableColumnConfig: TableColumnConfig[];
	private data: Medicacion[];
	public readonly TEXT_AREA_MAX_LENGTH = TEXT_AREA_MAX_LENGTH;
	private readonly ECL = SnomedECL.MEDICINE;

	constructor(
		private readonly formBuilder: FormBuilder,
		private readonly snomedService: SnomedService,
		private readonly snackBarService: SnackBarService,

	) {
		this.form = this.formBuilder.group({
			snomed: [null, Validators.required],
			observaciones: [null, [Validators.maxLength(this.TEXT_AREA_MAX_LENGTH)]],
			suspendido: [false]
		});

		this.columns = [
			{
				def: 'medicacion',
				header: 'ambulatoria.paciente.nueva-consulta.medicaciones.NOMBRE_MEDICACION',
				text: v => v.snomed.pt
			},
			{
				def: 'observaciones',
				header: 'ambulatoria.paciente.nueva-consulta.medicaciones.OBSERVACIONES',
				text: v => v.observaciones
			},
		];

		this.tableColumnConfig = [
			{
				def: 'medicacion',
				header: 'ambulatoria.paciente.nueva-consulta.medicaciones.MEDICATION',
				template: CellTemplates.TEXT,
				text: v => v.snomed.pt
			},
			{
				def: 'estado',
				header: 'ambulatoria.paciente.nueva-consulta.medicaciones.STATE',
				template: CellTemplates.TEXT,
				text: (v) => this.getState(v.suspendido)
			},
			{
				def: 'eliminar',
				template: CellTemplates.REMOVE_BUTTON,
				action: (rowIndex) => this.remove(rowIndex)
			}
		]

		this.data = [];
	}

	setConcept(selectedConcept: SnomedDto): void {
		this.snomedConcept = selectedConcept;
		const pt = selectedConcept ? selectedConcept.pt : '';
		this.form.controls.snomed.setValue(pt);
	}

	resetForm(): void {
		delete this.snomedConcept;
		this.form.reset();
	}

	add(medicacion: Medicacion): boolean {
		const currentItems = this.data.length;
		this.data = pushIfNotExists<Medicacion>(this.data, medicacion, this.compareSpeciality);
	 	return currentItems === this.data.length;
	}

	addControl(medicacion: Medicacion): void {
		if (this.add(medicacion))
			this.snackBarService.showError("Medicaci??n duplicada");
	}

	compareSpeciality(data: Medicacion, data1: Medicacion): boolean {
		return data.snomed.sctid === data1.snomed.sctid;
	}

	addToList() {
		if (this.form.valid && this.snomedConcept) {
			const nuevaMedicacion: Medicacion = {
				snomed: this.snomedConcept,
				observaciones: this.form.value.observaciones,
				suspendido: this.form.value.suspendido
			};
			this.addControl(nuevaMedicacion);
			this.resetForm();
		}
	}

	remove(index: number): void {
		this.data = removeFrom<Medicacion>(this.data, index);
	}


	openSearchDialog(searchValue: string): void {
		if (searchValue) {
			const search: SnomedSemanticSearch = {
				searchValue,
				eclFilter: this.ECL
			};
			this.snomedService.openConceptsSearchDialog(search)
				.subscribe((selectedConcept: SnomedDto) => this.setConcept(selectedConcept));
		}
	}

	getForm(): FormGroup {
		return this.form;
	}

	getSnomedConcept(): SnomedDto {
		return this.snomedConcept;
	}

	getColumns(): ColumnConfig[] {
		return this.columns;
	}

	getTableColumnConfig(): TableColumnConfig[] {
		return this.tableColumnConfig;
	}

	getMedicaciones(): Medicacion[] {
		return this.data;
	}

	getState(suspendido :boolean ): string{
		return suspendido?'Suspendido':'Activo'
	}

	getECL(): SnomedECL {
		return this.ECL;
	}
}
