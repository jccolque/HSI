import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EmergencyCareEpisodeService } from '@api-rest/services/emergency-care-episode.service';
import {
	DateTimeDto,
	DoctorsOfficeDto, EmergencyCareEpisodeListTriageDto,
	EmergencyCareListDto,
	EmergencyCarePatientDto,
	MasterDataDto,
	PatientPhotoDto
} from '@api-rest/api-model';
import { dateTimeDtoToDate } from '@api-rest/mapper/date-dto.mapper';
import { differenceInMinutes } from 'date-fns';
import { EstadosEpisodio, Triages } from '../../constants/masterdata';
import { ImageDecoderService } from '@presentation/services/image-decoder.service';
import { EpisodeStateService } from '../../services/episode-state.service';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { MatDialog } from '@angular/material/dialog';
import { SelectConsultorioComponent } from '../../dialogs/select-consultorio/select-consultorio.component';
import { ConfirmDialogComponent } from '@core/dialogs/confirm-dialog/confirm-dialog.component';
import { PermissionsService } from '@core/services/permissions.service';
import { TriageDefinitionsService } from '../../services/triage-definitions.service';
import { PatientService } from '@api-rest/services/patient.service';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

const TRANSLATE_KEY_PREFIX = 'guardia.home.episodes.episode.actions';

@Component({
	selector: 'app-home',
	templateUrl: './home.component.html',
	styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

	constructor(
		private router: Router,
		private emergencyCareEpisodeService: EmergencyCareEpisodeService,
		private imageDecoderService: ImageDecoderService,
		private snackBarService: SnackBarService,
		private readonly dialog: MatDialog,
		public readonly episodeStateService: EpisodeStateService,
		private readonly permissionsService: PermissionsService,
		private readonly triageDefinitionsService: TriageDefinitionsService,
		private readonly patientService: PatientService
	) {
	}

	readonly estadosEpisodio = EstadosEpisodio;
	readonly triages = Triages;
	readonly PACIENTE_TEMPORAL = 3;

	loading = true;
	episodes: any[];
	patientsPhotos: PatientPhotoDto[];

	private static calculateWaitingTime(dateTime: DateTimeDto): number {
		const creationDate = dateTimeDtoToDate(dateTime);
		const now = new Date();
		return differenceInMinutes(now, creationDate);
	}

	ngOnInit(): void {
		this.loadEpisodes();
	}

	loadEpisodes(): void {
		this.emergencyCareEpisodeService.getAll()
			.pipe(
				map((episodes: EmergencyCareListDto[]) =>
					episodes.map(episode => this.setWaitingTime(episode))
				)
			)
			.subscribe((episodes: any[]) => {
				this.episodes = episodes;
				this.loading = false;
				this.completePatientPhotos();

			}, _ => this.loading = false);
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
			data: {title : 'guardia.select_consultorio.ATENDER'}
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
							this.loadEpisodes();
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

	nuevoTriage(episode: EmergencyCareListDto): void {
		this.triageDefinitionsService.getTriagePath(episode.type?.id)
			.subscribe( ({component}) => {
				const dialogRef = this.dialog.open(component, {data: episode.id});
				dialogRef.afterClosed().subscribe(idReturned => {
					if (idReturned) {
						this.loadEpisodes();
					}
				});
			});
	}

	private completePatientPhotos() {
		if (this.patientsPhotos) {
			this.patientsPhotos.forEach(patientPhoto => {
				this.setEpisodePhoto(patientPhoto.patientId, patientPhoto.imageData);
			});
		} else {
			const patientsIds = getPatientsIds(this.episodes);
			if (patientsIds.length) {
				this.patientService.getPatientsPhotos(patientsIds).subscribe(patientsPhotos => {
					this.patientsPhotos = patientsPhotos;
					this.patientsPhotos.forEach(patientPhoto => {
						this.setEpisodePhoto(patientPhoto.patientId, patientPhoto.imageData);
					});
				});
			}
		}

		function getPatientsIds(episodes: any[]) {
			const ids = [];
			episodes.forEach(ep => {
				if (ep.patient?.id) {
					ids.push(ep.patient.id);
				}
			});
			return ids;
		}
	}

	private setEpisodePhoto(patientId: number, imageData: string) {
		const episode = this.episodes.find(ep => ep.patient?.id === patientId);
		if (episode) {
			episode.decodedPatientPhoto = this.imageDecoderService.decode(imageData);
		}
	}

	private setWaitingTime(episode: EmergencyCareListDto): Episode {
		return {
			...episode,
			waitingTime: episode.state.id === this.estadosEpisodio.EN_ESPERA ?
				HomeComponent.calculateWaitingTime(episode.creationDate) : undefined
		};
	}
}

export interface Episode {
	waitingTime: number;
	decodedPatientPhoto?: Observable<string>;
	creationDate: DateTimeDto;
	doctorsOffice: DoctorsOfficeDto;
	id: number;
	patient: EmergencyCarePatientDto;
	state: MasterDataDto;
	triage: EmergencyCareEpisodeListTriageDto;
	type: MasterDataDto;
}
