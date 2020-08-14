import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TurnosRoutingModule } from './turnos-routing.module';
import { HomeComponent } from './routes/home/home.component';
import { CoreModule } from '@core/core.module';
import { NewAgendaComponent } from './routes/new-agenda/new-agenda.component';
import { NewAttentionComponent } from './dialogs/new-attention/new-attention.component';
import { PresentationModule } from '@presentation/presentation.module';
import { CalendarDateFormatter, CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { AppMaterialModule } from '../material/app.material.module';
import { SelectAgendaComponent } from './routes/home/routes/select-agenda/select-agenda.component';
import { AgendaComponent } from './routes/home/routes/select-agenda/routes/agenda/agenda.component';
import { CustomDateFormatter } from './services/custom-date-formatter.service';
import { NewAppointmentComponent } from './dialogs/new-appointment/new-appointment.component';


@NgModule({
	declarations: [
		HomeComponent,
	 	NewAgendaComponent,
	 	NewAttentionComponent,
	 	SelectAgendaComponent,
	 	AgendaComponent,
	 	NewAppointmentComponent
	],
	imports: [
		CommonModule,
		CoreModule,
		AppMaterialModule,
		PresentationModule,
		TurnosRoutingModule,
		CalendarModule.forRoot({ provide: DateAdapter, useFactory: adapterFactory }),
	],
	providers: [
		{
			provide: CalendarDateFormatter,
			useClass: CustomDateFormatter,
		}
	]
})
export class TurnosModule {
}
