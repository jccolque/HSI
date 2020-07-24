import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SnomedDto } from '@api-rest/api-model';
import { ColumnConfig } from '@presentation/components/document-section/document-section.component';
import { SEMANTICS_CONFIG } from '../../../constants/snomed-semantics';
import { SnomedSemanticSearch, SnomedService } from '../../../services/snomed.service';
import { pushTo } from '@core/utils/array.utils';
import { DateFormat, momentFormat, newMoment } from '@core/utils/moment.utils';
import { Moment } from 'moment';

export interface Problema {
	snomed: SnomedDto;
	cronico?: boolean;
	fechaInicio: Moment;
	fechaFin?: Moment;
}

export class ProblemasNuevaConsultaService {

	readonly SEMANTICS_CONFIG = SEMANTICS_CONFIG;

	private form: FormGroup;
	private snomedConcept: SnomedDto;
	private readonly columns: ColumnConfig[];
	private data: Problema[];

	constructor(
		private readonly formBuilder: FormBuilder,
		private readonly snomedService: SnomedService
	) {
		this.form = this.formBuilder.group({
			snomed: [null, Validators.required],
			cronico: [null],
			fechaInicio: [null, Validators.required],
			fechaFin: [null]
		});

		this.columns = [
			{
				def: 'diagnosticos',
				header: 'ambulatoria.paciente.nueva-consulta.problemas.PROBLEMA',
				text: (row) => row.snomed.pt
			},
			{
				def: 'cronico',
				header: 'ambulatoria.paciente.nueva-consulta.problemas.CRONICO',
				text: (row) => row.cronico ? 'Si' : 'No'
			},
			{
				def: 'fecha_inicio',
				header: 'ambulatoria.paciente.nueva-consulta.problemas.FECHA_INICIO',
				text: (row) => momentFormat(row.fechaInicio, DateFormat.VIEW_DATE)
			},
			{
				def: 'fecha_fin',
				header: 'ambulatoria.paciente.nueva-consulta.problemas.FECHA_FIN',
				text: (row) => row.fechaFin ? momentFormat(row.fechaFin, DateFormat.VIEW_DATE) : ''
			}
		];

		this.data = [];
	}


	setConcept(selectedConcept: SnomedDto): void {
		this.snomedConcept = selectedConcept;
		const pt = selectedConcept ? selectedConcept.pt : '';
		this.form.controls.snomed.setValue(pt);
	}

	add(problema: Problema): void {
		this.data = pushTo<Problema>(this.data, problema);
	}

	addToList() {
		if (this.form.valid && this.snomedConcept) {
			const nuevoProblema: Problema = {
				snomed: this.snomedConcept,
				cronico: this.form.value.cronico,
				fechaInicio: this.form.value.fechaInicio,
				fechaFin: this.form.value.fechaFin
			};
			this.add(nuevoProblema);
			this.resetForm();
		}
	}

	resetForm(): void {
		delete this.snomedConcept;
		this.form.reset();
	}

	openSearchDialog(searchValue: string): void {
		if (searchValue) {
			const search: SnomedSemanticSearch = {
				searchValue,
				eclFilter: this.SEMANTICS_CONFIG.diagnosis
			};
			this.snomedService.openConceptsSearchDialog(search)
				.subscribe((selectedConcept: SnomedDto) => this.setConcept(selectedConcept));
		}
	}

	getFechaInicioMax(): Moment {
		return newMoment();
	}

	getFechaFinMin(): Moment {
		return this.form.value.fechaInicio ? this.form.value.fechaInicio : newMoment();
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

	getProblemas(): Problema[] {
		return this.data;
	}
}
