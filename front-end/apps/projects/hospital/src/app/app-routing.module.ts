import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { BackofficeComponent } from '@core/components/backoffice/backoffice.component';

export enum AppRoutes {
	Institucion = 'institucion',
	Backoffice = 'backoffice',
}

const routes: Routes = [
	{
		path: '',
		redirectTo: 'home',
		pathMatch: 'full',
	},
	{
		path: 'home',
		loadChildren: () => import('./modules/home/home.module').then(m => m.HomeModule),
	},
	{
		path: AppRoutes.Institucion,
		loadChildren: () => import('./modules/institucion/institucion.module').then(m => m.InstitucionModule),
	},
	{
		path: 'paciente',
		loadChildren: () => import('./modules/portal-paciente/portal-paciente.module').then(m => m.PortalPacienteModule),
	},
	{
		path: AppRoutes.Backoffice,
		component: BackofficeComponent,
	},
];

@NgModule({
	imports: [RouterModule.forRoot(routes, {
	// habilitar log de routeos en consola
	// enableTracing: true,
	scrollPositionRestoration: 'enabled',
	relativeLinkResolution: 'legacy'
})],
	exports: [RouterModule]
})
export class AppRoutingModule {
}
