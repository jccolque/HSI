import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InicioComponent } from './route/inicio/inicio.component';
import { AppMaterialModule } from 'src/app/app.material.module';
import { FooterComponent } from './components/footer/footer.component';
import { BarComponent } from './components/bar/bar.component';
import { ContentComponent } from './components/content/content.component';
import { HttpClientModule } from '@angular/common/http';
import { PresentationModule } from '../presentation/presentation.module';
import { TranslateModule } from "@ngx-translate/core";
import { RECAPTCHA_SETTINGS, RecaptchaFormsModule, RecaptchaModule, RecaptchaSettings } from "ng-recaptcha";

const globalSettings: RecaptchaSettings = { siteKey: '6LcVh-UUAAAAAExavpQdi-VgdyObNuSOUI2s8cct' };

@NgModule({
	declarations: [
		InicioComponent,
		BarComponent,
		ContentComponent,
		FooterComponent,
	],
	imports: [
		CommonModule,
		AppMaterialModule,
		HttpClientModule,
		PresentationModule,
		TranslateModule,
		RecaptchaModule,
		RecaptchaFormsModule,
	],
	exports: [
		BarComponent,
		ContentComponent,
		FooterComponent,
		HttpClientModule,
		TranslateModule,
		RecaptchaModule,
		RecaptchaFormsModule,
	],
	providers: [{
		provide: RECAPTCHA_SETTINGS,
		useValue: globalSettings,
	}]
})
export class CoreModule {
}
