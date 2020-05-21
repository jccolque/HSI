import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { InternacionesHomeComponent } from './routes/home/internaciones-home.component';
import { InternacionPacienteComponent } from './routes/internacion-paciente/internacion-paciente.component';
import { AnamnesisComponent } from './routes/anamnesis/anamnesis.component';
import { NewInternmentComponent } from "./routes/new-internment/new-internment.component";
import { EpicrisisComponent } from './routes/epicrisis/epicrisis.component';
import { NotaEvolucionComponent } from './routes/nota-evolucion/nota-evolucion.component';
import { RoleGuard } from '@core/guards/RoleGuard';
import { PatientDischargeComponent } from './routes/patient-discharge/patient-discharge.component';


const routes: Routes = [
	{
		path: '',
		component: InternacionesHomeComponent,
		canActivate: [RoleGuard],
		data: { allowedRoles: ['ESPECIALISTA_MEDICO', 'PROFESIONAL_DE_SALUD'] }
	},
	{
		path: 'internacion/:idInternacion/paciente/:idPaciente',
		component: InternacionPacienteComponent,
		canActivate: [RoleGuard],
		data: { allowedRoles: ['ESPECIALISTA_MEDICO', 'PROFESIONAL_DE_SALUD', 'ADMINISTRATIVO'] }
	},
	{
		path: 'internacion/:idInternacion/paciente/:idPaciente/anamnesis',
		component: AnamnesisComponent,
		canActivate: [RoleGuard],
		data: { allowedRoles: ['ESPECIALISTA_MEDICO'] }
	},
	{
		path: 'internacion/:idInternacion/paciente/:idPaciente/anamnesis/:anamnesisId',
		component: AnamnesisComponent,
		canActivate: [RoleGuard],
		data: { allowedRoles: ['ESPECIALISTA_MEDICO'] }
	},
	{
		path: 'internacion/:idInternacion/paciente/:idPaciente/nota-evolucion',
		component: NotaEvolucionComponent,
		canActivate: [RoleGuard],
		data: { allowedRoles: ['ESPECIALISTA_MEDICO', 'PROFESIONAL_DE_SALUD'] }
	},
	{
		path: 'internacion/:idInternacion/paciente/:idPaciente/epicrisis',
		component: EpicrisisComponent,
		canActivate: [RoleGuard],
		data: { allowedRoles: ['ESPECIALISTA_MEDICO'] }
	},
	{
		path: 'internacion/new',
		component: NewInternmentComponent,
		canActivate: [RoleGuard],
		data: { allowedRoles: ['ADMINISTRATIVO'] }
	},
	{
		path: 'internacion/:idInternacion/paciente/:idPaciente/alta',
		canActivate: [RoleGuard],
		component: PatientDischargeComponent,
		data: { allowedRoles: ['ADMINISTRATIVO'] }
	}
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class InternacionesRoutingModule { }
