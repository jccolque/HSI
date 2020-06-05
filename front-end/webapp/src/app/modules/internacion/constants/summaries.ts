import { SummaryHeader } from '../../presentation/components/summary-card/summary-card.component';
import { SearchField } from '../components/documents-summary/documents-summary.component';

export const INTERNACION: SummaryHeader = {
	title: 'Resumen de internación',
	matIcon: 'single_bed'
};

export const DIAGNOSTICO_PRINCIPAL: SummaryHeader = {
	title: 'internaciones.anamnesis.diagnosticos.TITLE_PRINCIPAL',
	matIcon: 'local_hospital'
};

export const DIAGNOSTICOS: SummaryHeader = {
	title: 'internaciones.anamnesis.diagnosticos.TITLE',
	matIcon: 'queue'
};

export const SIGNOS_VITALES: SummaryHeader = {
	title: 'Signos vitales',
	matIcon: 'favorite_border'
};

export const ANTROPOMETRICOS: SummaryHeader = {
	title: 'Información antropométrica',
	matIcon: 'accessibility_new'
};

export const ANTECEDENTES_PERSONALES: SummaryHeader = {
	title: 'Antecedentes personales',
	matIcon: 'error_outline'
};

export const ANTECEDENTES_FAMILIARES: SummaryHeader = {
	title: 'Antecedentes familiares',
	matIcon: 'error_outline'
};

export const MEDICACION: SummaryHeader = {
	title: 'Medicación habitual',
	matIcon: 'event_available'
};

export const ALERGIAS: SummaryHeader = {
	title: 'Alergias',
	matIcon: 'queue'
};

export const VACUNAS: SummaryHeader = {
	title: 'Vacunas suministradas',
	matIcon: 'event_available'
};

export const DOCUMENTS: SummaryHeader = {
	title: 'Evoluciones',
	matIcon: 'assignment'
};

export const DOCUMENTS_SEARCH_FIELDS: SearchField[] = [
	{
		field: 'DIAGNOSIS',
		label: 'internaciones.documents-summary.search-fields.DIAGNOSIS',
	},
	{
		field: 'DOCTOR',
		label: 'internaciones.documents-summary.search-fields.DOCTOR',
	},
	{
		field: 'CREATEDON',
		label: 'internaciones.documents-summary.search-fields.CREATEDON',
	},
	{
		field: 'ALL',
		label: 'internaciones.documents-summary.search-fields.ALL',
	}
];
