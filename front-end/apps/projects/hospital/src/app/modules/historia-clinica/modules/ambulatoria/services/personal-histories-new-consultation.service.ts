import { SnomedDto } from "@api-rest/api-model";
import { Moment } from "moment";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { SnomedSemanticSearch, SnomedService } from "@historia-clinica/services/snomed.service";
import { DateFormat, momentFormat, newMoment } from "@core/utils/moment.utils";
import { pushTo, removeFrom } from "@core/utils/array.utils";
import { SEMANTICS_CONFIG } from "@historia-clinica/constants/snomed-semantics";
import { TableColumnConfig } from "@presentation/components/document-section-table/document-section-table.component";
import { CellTemplates } from "@presentation/components/cell-templates/cell-templates.component";

export interface AntecedentePersonal {
	snomed: SnomedDto;
	fecha: Moment;
}

export class PersonalHistoriesNewConsultationService {

	private form: FormGroup;
	private data: AntecedentePersonal[];
	private snomedConcept: SnomedDto;
	readonly SEMANTICS_CONFIG = SEMANTICS_CONFIG;

	private readonly tableColumnConfig : TableColumnConfig[];

	constructor(
		private readonly formBuilder: FormBuilder,
		private readonly snomedService: SnomedService) {

		this.form = this.formBuilder.group({
			snomed: [null, Validators.required],
			fecha: [null, Validators.required]
		});

		this.tableColumnConfig =[
			{
				def: 'problemType',
				header: 'ambulatoria.paciente.nueva-consulta.antecedentes-personales.table.columns.ANTECEDENTE_PERSONAL',
				template: CellTemplates.TEXT,
				text: af => af.snomed.pt
			},
			{
				def: 'date',
				header: 'ambulatoria.paciente.nueva-consulta.antecedentes-personales.table.columns.FECHA',
				template: CellTemplates.TEXT,
				text: (row) => momentFormat(row.fecha, DateFormat.VIEW_DATE)
			},
			{
				def: 'delete',
				template: CellTemplates.REMOVE_BUTTON,
				action: (rowIndex) => this.removeAntecedente(rowIndex)
			}
		]

		this.data = [];
	}

	getTableColumnConfig(): TableColumnConfig[] {
		return this.tableColumnConfig;
	}

	getAntecedentesPersonales(): AntecedentePersonal[] {
		return this.data;
	}

	getSnomedConcept(): SnomedDto {
		return this.snomedConcept;
	}

	setConcept(selectedConcept: SnomedDto): void {
		this.snomedConcept = selectedConcept;
		const pt = selectedConcept ? selectedConcept.pt : '';
		this.form.controls.snomed.setValue(pt);
	}

	addAntecedente(antecedente: AntecedentePersonal): void {
		this.data = pushTo<any>(this.data, antecedente);
	}

	removeAntecedente(index: number): void {
		this.data = removeFrom<AntecedentePersonal>(this.data, index);
	}

	addToList() {
		if (this.form.valid && this.snomedConcept) {
			const antecedente: AntecedentePersonal = {
				snomed: this.snomedConcept,
				fecha: this.form.value.fecha
			};
			this.addAntecedente(antecedente);
			this.resetForm();
		}
	}

	getForm(): FormGroup {
		return this.form;
	}


	resetForm(): void {
		delete this.snomedConcept;
		this.form.reset();
	}

	openSearchDialog(searchValue: string): void {
		if (searchValue) {
			const search: SnomedSemanticSearch = {
				searchValue,
				eclFilter: this.SEMANTICS_CONFIG.personalRecord
			};
			this.snomedService.openConceptsSearchDialog(search)
				.subscribe((selectedConcept: SnomedDto) => this.setConcept(selectedConcept));
		}
	}

	getMaxDate(): Moment {
		return newMoment();
	}
}