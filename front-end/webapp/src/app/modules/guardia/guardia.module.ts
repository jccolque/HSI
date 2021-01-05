import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GuardiaRoutingModule } from './guardia-routing.module';
import { HomeComponent } from './routes/home/home.component';
import { AdmisionAdministrativaComponent } from './routes/admision-administrativa/admision-administrativa.component';
import { CoreModule } from '@core/core.module';
import { InstitucionModule } from '../institucion/institucion.module';
import { PresentationModule } from '@presentation/presentation.module';
import { TriageComponent } from './components/triage/triage.component';
import { AdministrativeTriageComponent } from './components/administrative-triage/administrative-triage.component';
import { NewEpisodeAdminTriageComponent } from './routes/new-episode-admin-triage/new-episode-admin-triage.component';
import { AdministrativeTriageDialogComponent } from './dialogs/administrative-triage-dialog/administrative-triage-dialog.component';

import { HistoriaClinicaModule } from '../historia-clinica/historia-clinica.module';
import { NewEpisodeService } from './services/new-episode.service';
import { EpisodeStateService } from './services/episode-state.service';
import { SelectConsultorioComponent } from './dialogs/select-consultorio/select-consultorio.component';
import { EpisodeDetailsComponent } from './routes/episode-details/episode-details.component';
import { AdultGynecologicalTriageComponent } from './components/adult-gynecological-triage/adult-gynecological-triage.component';
import { NewEpisodeAdultGynecologicalTriageComponent } from './routes/new-episode-adult-gynecological-triage/new-episode-adult-gynecological-triage.component';
import { PediatricTriageComponent } from './components/pediatric-triage/pediatric-triage.component';
import { NewEpisodePediatricTriageComponent } from './routes/new-episode-pediatric-triage/new-episode-pediatric-triage.component';

@NgModule({
	declarations: [
		HomeComponent,
		TriageComponent,
		AdministrativeTriageComponent,
		NewEpisodeAdminTriageComponent,
		AdmisionAdministrativaComponent,
		AdministrativeTriageDialogComponent,
		AdultGynecologicalTriageComponent,
		NewEpisodeAdultGynecologicalTriageComponent,
		PediatricTriageComponent,
		NewEpisodePediatricTriageComponent,
		SelectConsultorioComponent,
		EpisodeDetailsComponent,
	],
	imports: [
		CommonModule,
		GuardiaRoutingModule,
		CoreModule,
		PresentationModule,
		GuardiaRoutingModule,
		InstitucionModule,
		PresentationModule,
		HistoriaClinicaModule, // TODO Quitar este modulo
	],
	providers: [
		NewEpisodeService,
		EpisodeStateService
	]
})
export class GuardiaModule {
}
