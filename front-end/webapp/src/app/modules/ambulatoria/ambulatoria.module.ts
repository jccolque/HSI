import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponent } from './routes/home/home.component';
import { AmbulatoriaRoutingModule } from './ambulatoria-routing.module';
import { FormsModule } from '@angular/forms';
import { CoreModule } from '@core/core.module';
import { PacientesModule } from '../pacientes/pacientes.module';
import { PresentationModule } from '@presentation/presentation.module';



@NgModule({
  declarations: [HomeComponent],
  imports: [
	AmbulatoriaRoutingModule,
	FormsModule,
	CommonModule,
	CoreModule,
	PacientesModule,
	PresentationModule
  ]
})
export class AmbulatoriaModule { }
