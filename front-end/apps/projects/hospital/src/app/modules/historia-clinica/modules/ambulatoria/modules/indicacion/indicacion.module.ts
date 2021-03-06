import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from "@angular/forms";
import { PresentationModule } from "@presentation/presentation.module";

import { CardMedicacionesComponent } from "@historia-clinica/modules/ambulatoria/modules/indicacion/components/card-medicaciones/card-medicaciones.component";
import { InternmentIndicationsCardComponent } from "@historia-clinica/modules/ambulatoria/modules/indicacion/components/internment-indications-card/internment-indications-card.component";
import { ItemPrescripcionesComponent } from "@historia-clinica/modules/ambulatoria/modules/indicacion/components/item-prescripciones/item-prescripciones.component";
import { InternmentDietCardComponent } from './components/internment-diet-card/internment-diet-card.component';
import { InternmentPharmacoCardComponent } from './components/internment-pharmaco-card/internment-pharmaco-card.component';
import { InternmentOtherIndicationCardComponent } from './components/internment-other-indication-card/internment-other-indication-card.component';
import { InternmentParenteralPlanCardComponent } from './components/internment-parenteral-plan-card/internment-parenteral-plan-card.component';
import { NuevaPrescripcionComponent } from "@historia-clinica/modules/ambulatoria/modules/indicacion/dialogs/nueva-prescripcion/nueva-prescripcion.component";
import { SuspenderMedicacionComponent } from "@historia-clinica/modules/ambulatoria/modules/indicacion/dialogs/suspender-medicacion/suspender-medicacion.component";
import { DietComponent } from "@historia-clinica/modules/ambulatoria/modules/indicacion/dialogs/diet/diet.component";
import { OtherIndicationComponent } from './dialogs/other-indication/other-indication.component';
import { ParenteralPlanComponent } from './dialogs/parenteral-plan/parenteral-plan.component';
import { HistoriaClinicaModule } from '@historia-clinica/historia-clinica.module';

@NgModule({
	declarations: [
		CardMedicacionesComponent,
		ItemPrescripcionesComponent,
		InternmentIndicationsCardComponent,
		InternmentDietCardComponent,
		InternmentPharmacoCardComponent,
		InternmentOtherIndicationCardComponent,
		InternmentParenteralPlanCardComponent,
		NuevaPrescripcionComponent,
		SuspenderMedicacionComponent,
		DietComponent,
		OtherIndicationComponent,
  		ParenteralPlanComponent,
	],
	exports: [
		CardMedicacionesComponent,
		ItemPrescripcionesComponent,
		InternmentIndicationsCardComponent,
	],
	imports: [
		CommonModule,
		FormsModule,
		PresentationModule,
		HistoriaClinicaModule
	]
})
export class IndicacionModule { }
