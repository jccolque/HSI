import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {
	EmergencyCareEpisodeDto,
	EmergencyCareEpisodeService
} from '@api-rest/services/emergency-care-episode.service';
import { DateTimeDto } from '@api-rest/api-model';
import { dateTimeDtoToDate } from '@api-rest/mapper/date-dto.mapper';
import { differenceInMinutes } from 'date-fns';
import { EstadosEpisodio, Triages } from '../../constants/masterdata';
import { ImageDecoderService } from '@presentation/services/image-decoder.service';
import { EpisodeStateService } from '../../services/episode-state.service';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { MatDialog } from '@angular/material/dialog';
import { SelectConsultorioComponent } from '../../dialogs/select-consultorio/select-consultorio.component';
import { ConfirmDialogComponent } from '@core/dialogs/confirm-dialog/confirm-dialog.component';

const TRANSLATE_KEY_PREFIX = 'guardia.home.episodes.episode.actions';

@Component({
	selector: 'app-home',
	templateUrl: './home.component.html',
	styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

	readonly estadosEpisodio = EstadosEpisodio;
	readonly triages = Triages;
	readonly PACIENTE_TEMPORAL = 3;
	episodes: any[];

	constructor(
		private router: Router,
		private emergencyCareEpisodeService: EmergencyCareEpisodeService,
		private imageDecoderService: ImageDecoderService,
		private snackBarService: SnackBarService,
		private readonly dialog: MatDialog,
		public readonly episodeStateService: EpisodeStateService
	) {
	}

	ngOnInit(): void {
		this.emergencyCareEpisodeService.getAll().subscribe(episodes => {
			this.episodes = episodes.map(episode => this.mapPhotoAndWaitingTime(episode));
		});
	}

	private mapPhotoAndWaitingTime(episode: EmergencyCareEpisodeDto) {
		return {
			...episode,
			waitingTime: episode.state.id === this.estadosEpisodio.EN_ESPERA ?
				this.calculateWaitingTime(episode.creationDate) : undefined,
			patient: episode.patient ? {
				...episode.patient,
				person: {
					...episode.patient?.person,
					decodedPhoto$: this.imageDecoderService.decode(episode.patient?.person?.photo)
				}
			} : undefined
		};
	}

	private calculateWaitingTime(dateTime: DateTimeDto): number {
		const creationDate = dateTimeDtoToDate(dateTime);
		const now = new Date();
		return differenceInMinutes(now, creationDate);
	}

	goToEpisode(id: number) {
		this.router.navigate([`${this.router.url}/episodio/${id}`]);
	}

	goToMockup(): void {
		this.router.navigate([`${this.router.url}/mock`]);
	}

	goToAdmisionAdministrativa(): void {
		this.router.navigate([`${this.router.url}/nuevo-episodio/administrativa`]);
	}

	atender(episodeId: number): void {

		const dialogRef = this.dialog.open(SelectConsultorioComponent, {
			width: '25%',
		});

		dialogRef.afterClosed().subscribe(consultorio => {
			if (consultorio) {
				this.episodeStateService.atender(episodeId, consultorio.id).subscribe(changed => {
						if (changed) {
							this.snackBarService.showSuccess(`${TRANSLATE_KEY_PREFIX}.atender.SUCCESS`);
							this.goToEpisode(episodeId);
						} else {
							this.snackBarService.showError(`${TRANSLATE_KEY_PREFIX}.atender.ERROR`);
						}
					}, _ => this.snackBarService.showError(`${TRANSLATE_KEY_PREFIX}.atender.ERROR`)
				);
			}
		});
	}

	finalizar(episodeId: number): void {
		const dialogRef = this.dialog.open(ConfirmDialogComponent, {
			data: {
				title: 'guardia.home.episodes.episode.actions.finalizar_ausencia.TITLE',
				content: 'guardia.home.episodes.episode.actions.finalizar_ausencia.CONFIRM'
			}
		});

		dialogRef.afterClosed().subscribe(confirmed => {
			if (confirmed) {
				this.episodeStateService.finalizarPorAusencia(episodeId).subscribe(changed => {
						if (changed) {
							this.snackBarService
								.showSuccess(`${TRANSLATE_KEY_PREFIX}.finalizar_ausencia.SUCCESS`);
						} else {
							this.snackBarService
								.showError(`${TRANSLATE_KEY_PREFIX}.finalizar_ausencia.ERROR`);
						}
					}, _ => this.snackBarService
						.showError(`${TRANSLATE_KEY_PREFIX}.finalizar_ausencia.ERROR`)
				);
			}
		});
	}

	nuevoTriage(): void {
		console.log('Nuevo triage');
	}

}
