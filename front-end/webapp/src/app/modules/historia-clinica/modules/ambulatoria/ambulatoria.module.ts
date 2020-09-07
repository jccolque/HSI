import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponent } from './routes/home/home.component';
import { AmbulatoriaRoutingModule } from './ambulatoria-routing.module';
import { FormsModule } from '@angular/forms';
import { CoreModule } from '@core/core.module';
import { PacientesModule } from '../../../pacientes/pacientes.module';
import { PresentationModule } from '@presentation/presentation.module';
import { PatientProfileComponent } from './routes/patient-profile/patient-profile.component';
import { AmbulatoriaPacienteComponent } from './routes/ambulatoria-paciente/ambulatoria-paciente.component';
import { ResumenComponent } from './components/resumen/resumen.component';
import { ProblemasComponent } from './components/problemas/problemas.component';
import { VacunasComponent } from './components/vacunas/vacunas.component';
import { HistoriaClinicaModule } from '../../historia-clinica.module';
import { AplicarVacunaComponent } from './dialogs/aplicar-vacuna/aplicar-vacuna.component';
import { AppMaterialModule } from '../../../material/app.material.module';
import { NuevaConsultaComponent } from './routes/nueva-consulta/nueva-consulta.component';
import {SolveProblemComponent} from "../../dialogs/solve-problem/solve-problem.component";
import { HistoricalProblemsFiltersComponent } from './components/historical-problems-filters/historical-problems-filters.component';


@NgModule({
	declarations: [
		HomeComponent,
		PatientProfileComponent,
		AmbulatoriaPacienteComponent,
		ResumenComponent,
		ProblemasComponent,
		VacunasComponent,
		AplicarVacunaComponent,
		NuevaConsultaComponent,
		SolveProblemComponent,
		HistoricalProblemsFiltersComponent
	],
	imports: [
		AmbulatoriaRoutingModule,
		AppMaterialModule,
		CommonModule,
		CoreModule,
		FormsModule,
		HistoriaClinicaModule,
		PacientesModule,
		PresentationModule
	]
})
export class AmbulatoriaModule {
}
