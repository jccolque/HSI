import { Component, OnInit } from '@angular/core';
import {
	PersonalInformationDto,
	CompletePatientDto,
	PatientMedicalCoverageDto,
	PersonPhotoDto,
	ProfessionalDto,
	HealthcareProfessionalSpecialtyDto,
	ClinicalSpecialtyDto,
	UserDataDto,
	UserRoleDto,
	RoleDto,
	InternmentSummaryDto,
	PatientDischargeDto,
	EpicrisisSummaryDto,
	InstitutionDto,
	EmergencyCareEpisodeInProgressDto,
	EmergencyCareListDto,
	HealthcareProfessionalCompleteDto
} from '@api-rest/api-model';
import { ERole } from '@api-rest/api-model';
import { AppFeature } from '@api-rest/api-model';
import { PatientService } from '@api-rest/services/patient.service';
import { MapperService } from '../../../presentation/services/mapper.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PersonService } from '@api-rest/services/person.service';
import { PatientBasicData } from '@presentation/components/patient-card/patient-card.component';
import { PersonalInformation } from '@presentation/components/personal-information/personal-information.component';
import { PatientTypeData } from '@presentation/components/patient-type-logo/patient-type-logo.component';
import { ContextService } from '@core/services/context.service';
import { InternmentPatientService } from '@api-rest/services/internment-patient.service';
import { PatientMedicalCoverageService } from '@api-rest/services/patient-medical-coverage.service';
import { FeatureFlagService } from "@core/services/feature-flag.service";
import { MatDialog } from "@angular/material/dialog";
import { ReportsComponent } from "@pacientes/dialogs/reports/reports.component";
import { patientCompleteName } from '@core/utils/patient.utils';
import { UserService } from "@api-rest/services/user.service";
import { SnackBarService } from "@presentation/services/snack-bar.service";
import { FormBuilder, FormGroup } from "@angular/forms";
import { processErrors } from "@core/utils/form.utils";
import { PermissionsService } from "@core/services/permissions.service";
import { UserPasswordResetService } from "@api-rest/services/user-password-reset.service";
import { EditProfessionsComponent, ProfessionDto } from '@pacientes/dialogs/edit-professions/edit-professions.component';
import { ProfessionalService } from '@api-rest/services/professional.service';
import { SpecialtyService } from '@api-rest/services/specialty.service';
import { EditRolesComponent } from '@pacientes/dialogs/edit-roles/edit-roles.component';
import { RolesService } from '@api-rest/services/roles.service';
import { InstitutionService } from '@api-rest/services/institution.service';
import { INTERNACION } from "@historia-clinica/constants/summaries";
import { InternacionService } from "@api-rest/services/internacion.service";
import { InternmentEpisodeService } from "@api-rest/services/internment-episode.service";
import { EstadosEpisodio, Triages } from "@historia-clinica/modules/guardia/constants/masterdata";
import { EmergencyCareEpisodeSummaryService } from "@api-rest/services/emergency-care-episode-summary.service";
import { AppRoutes } from "../../../../app-routing.module";
import { InternmentEpisodeSummary } from "@historia-clinica/modules/ambulatoria/modules/internacion/components/internment-episode-summary/internment-episode-summary.component";

const ROUTE_NEW_INTERNMENT = 'internaciones/internacion/new';
const ROUTE_EDIT_PATIENT = 'pacientes/edit';
const ROUTE_EMERGENCY_CARE_EPISODE_PREFIX = 'guardia/episodio/';
const ROLES_TO_VIEW_USER_DATA: ERole[] = [ERole.ADMINISTRADOR_INSTITUCIONAL_BACKOFFICE];
const ROLES_TO_VIEW_CLINIC_HISTORY_DATA: ERole[] = [ERole.ADMINISTRATIVO];
const DIALOG_SIZE = '30%';

export interface ProfessionAndSpecialtyDto {
	professionDescription: string,
	professionId: number,
	specialtyId: number,
	specialtyName: string,
}

@Component({
	selector: 'app-profile',
	templateUrl: './profile.component.html',
	styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
	userRoles: string[] = [];
	roles: RoleDto[] = [];
	userId: number = null;
	rolesByUser: UserRoleDto[] = [];
	patientId: number;
	showDischarge = false;
	epicrisisDoc: EpicrisisSummaryDto;
	canLoadProbableDischargeDate: boolean;
	allProfessions: ProfessionDto[] = [];
	allSpecialties: ClinicalSpecialtyDto[] = [];
	ownProfessionsAndSpecialties: ProfessionAndSpecialtyDto[] = [];
	isProfessional = false;
	institutionName: string;
	license: string = '';
	private institution: number[] = [];
	private rolesAdmin = false;
	public patientBasicData: PatientBasicData;
	public personalInformation: PersonalInformation;
	public patientMedicalCoverage: PatientMedicalCoverageDto[];
	public patientTypeData: PatientTypeData;
	public person: PersonalInformationDto;
	public personPhoto: PersonPhotoDto;
	public codigoColor: string;
	public internacionSummary = INTERNACION;
	public internmentEpisodeSummary: InternmentEpisodeSummary;
	private readonly routePrefix;
	public internmentEpisode;
	public userData: UserDataDto;
	public personId: number;
	private professionalId: number;
	private professionalSpecialtyId: number[] = [];
	public form: FormGroup;

	public downloadReportIsEnabled: boolean;
	public createUsersIsEnable: boolean;

	emergencyCareEpisodeInProgress: EmergencyCareEpisodeInProgressDto;
	emergencyCareEpisodeSummary: EmergencyCareListDto;
	readonly triages = Triages;
	readonly episodeStates = EstadosEpisodio;

	constructor(
		private patientService: PatientService,
		private mapperService: MapperService,
		private route: ActivatedRoute,
		private router: Router,
		private personService: PersonService,
		private contextService: ContextService,
		private userPasswordResetService: UserPasswordResetService,
		private internmentPatientService: InternmentPatientService,
		private internmentEpisodeService: InternmentEpisodeService,
		private readonly patientMedicalCoverageService: PatientMedicalCoverageService,
		private readonly featureFlagService: FeatureFlagService,
		public dialog: MatDialog,
		private readonly userService: UserService,
		private readonly snackBarService: SnackBarService,
		private readonly professionalService: ProfessionalService,
		private readonly specialtyService: SpecialtyService,
		private readonly formBuilder: FormBuilder,
		private readonly rolesService: RolesService,
		private readonly institutionService: InstitutionService,
		private readonly permissionService: PermissionsService,
		private readonly internmentService: InternacionService,
		private readonly emergencyCareEpisodeSummaryService: EmergencyCareEpisodeSummaryService,
	) {
		this.routePrefix = 'institucion/' + this.contextService.institutionId + '/';
		this.featureFlagService.isActive(AppFeature.HABILITAR_INFORMES).subscribe(isOn => this.downloadReportIsEnabled = isOn);
		this.featureFlagService.isActive(AppFeature.HABILITAR_CREACION_USUARIOS).subscribe(isOn => this.createUsersIsEnable = isOn);
	}

	ngOnInit(): void {

		this.route.paramMap.subscribe(
			(params) => {
				this.patientId = Number(params.get('id'));
				this.patientService.getPatientCompleteData<CompletePatientDto>(this.patientId)
					.subscribe(completeData => {
						this.patientTypeData = this.mapperService.toPatientTypeData(completeData.patientType);
						this.patientBasicData = this.mapperService.toPatientBasicData(completeData);
						this.personId = completeData.person.id;
						this.patientMedicalCoverageService.getActivePatientMedicalCoverages(this.patientId)
							.subscribe(patientMedicalCoverageDto => this.patientMedicalCoverage = patientMedicalCoverageDto);
						this.personService.getPersonalInformation<PersonalInformationDto>(completeData.person.id)
							.subscribe(personInformationData => {
								this.personalInformation = this.mapperService.toPersonalInformationData(completeData, personInformationData);
							});
						this.permissionService.hasContextAssignments$(ROLES_TO_VIEW_USER_DATA).subscribe(hasRoleToViewUserData => {
							if (hasRoleToViewUserData) {
								this.checkIfProfessional();
								if (this.createUsersIsEnable) {
									this.userService.getUserData(this.personId)
										.subscribe(userDataDto => {
											this.userData = userDataDto
											this.form.controls.enable.setValue(this.userData.enable);
										});
								}
								this.userService.getUserData(completeData.person.id).subscribe((userData: UserDataDto) => {
									this.userData = userData;
									if (userData?.id) {
										this.rolesService.getRolesByUser(this.userData?.id).subscribe((roles: UserRoleDto[]) => {
											this.rolesByUser = roles;
											roles.forEach(e => this.userRoles.push(e.roleDescription));
										})
										this.rolesService.hasBackofficeRole(userData?.id).subscribe((hasRolesAdmin: boolean) => {
											this.rolesAdmin = hasRolesAdmin;
										});
									}
								});
								this.rolesService.getAllInstitutionalRoles().subscribe((roles: RoleDto[]) => {
									this.roles = roles;
								});

								this.institution.push(this.contextService.institutionId);
								this.institutionService.getInstitutions(this.institution).subscribe((institutions: InstitutionDto[]) => {
									this.institutionName = institutions[0].name;
								});

								this.professionalService.getList().subscribe((allProfessions: ProfessionDto[]) => {
									this.allProfessions = allProfessions;
								});

								this.specialtyService.getAll().subscribe((allSpecialties: ClinicalSpecialtyDto[]) => {
									this.allSpecialties = allSpecialties;
								});
							}
						})

					});
				this.featureFlagService.isActive(AppFeature.HABILITAR_CARGA_FECHA_PROBABLE_ALTA).subscribe(isOn => {
					this.canLoadProbableDischargeDate = isOn;
				});
				this.permissionService.hasContextAssignments$(ROLES_TO_VIEW_CLINIC_HISTORY_DATA).subscribe(hasRoleToViewClinicHistoryData => {
					if (hasRoleToViewClinicHistoryData) {
						this.internmentPatientService.internmentEpisodeIdInProcess(this.patientId)
							.subscribe(internmentEpisodeProcessDto => {
								if (internmentEpisodeProcessDto) {
									this.internmentEpisode = internmentEpisodeProcessDto;
									if (internmentEpisodeProcessDto.id) {
										this.internmentService.getInternmentEpisodeSummary(internmentEpisodeProcessDto.id)
											.subscribe((internmentEpisode: InternmentSummaryDto) => {
												this.internmentEpisodeSummary = this.mapperService.toInternmentEpisodeSummary(internmentEpisode)
												this.epicrisisDoc = internmentEpisode.documents?.epicrisis;
											});
										this.internmentEpisodeService.getPatientDischarge(internmentEpisodeProcessDto.id)
											.subscribe((patientDischarge: PatientDischargeDto) => {
												this.featureFlagService.isActive(AppFeature.HABILITAR_ALTA_SIN_EPICRISIS).subscribe(isOn => {
													this.showDischarge = isOn || (patientDischarge.dischargeTypeId !== 0);
												});
											});
									}
								}
							});


						this.emergencyCareEpisodeSummaryService.getEmergencyCareEpisodeInProgress(this.patientId)
							.subscribe(emergencyCareEpisodeInProgressDto => {
								this.emergencyCareEpisodeInProgress = emergencyCareEpisodeInProgressDto
								if (this.emergencyCareEpisodeInProgress?.inProgress) {
									this.emergencyCareEpisodeSummaryService.getEmergencyCareEpisodeSummary(this.emergencyCareEpisodeInProgress.id)
										.subscribe(emergencyCareEpisodeListDto => this.emergencyCareEpisodeSummary = emergencyCareEpisodeListDto);
								}
							});
					}
				})
				this.patientService.getPatientPhoto(this.patientId)
					.subscribe((personPhotoDto: PersonPhotoDto) => {
						this.personPhoto = personPhotoDto;
					});
			});

		this.form = this.formBuilder.group({
			enable: [null]
		});

	}

	isInstitutionalAdministrator(): boolean {
		return this.rolesAdmin;
	}

	checkIfProfessional(): void {
		this.professionalService.get(this.personId).subscribe((profession: ProfessionalDto) => {
			if (profession) {
				this.license = profession.licenceNumber;
				this.professionalId = profession.id;
				this.isProfessional = true;
				this.setProfessionsAndSpecialties();
			}
		})
	}

	setProfessionsAndSpecialties(): void {
		this.professionalService.getProfessionsByProfessional(this.professionalId).subscribe(
			(professionalsByClinicalSpecialty: HealthcareProfessionalSpecialtyDto[]) => {
				professionalsByClinicalSpecialty.forEach((index: HealthcareProfessionalSpecialtyDto) => {
					this.professionalSpecialtyId.push(index.id);
					this.ownProfessionsAndSpecialties.push(
						{
							professionDescription: (this.allProfessions.find(element =>
								element.id === index.professionalSpecialtyId
							).description),
							professionId: index.professionalSpecialtyId,
							specialtyId: index.clinicalSpecialtyId,
							specialtyName: (this.allSpecialties.find(element =>
								element.id === index.clinicalSpecialtyId
							).name)
						})
				})

			})

	}

	goNewInternment(): void {
		this.router.navigate([this.routePrefix + ROUTE_NEW_INTERNMENT],
			{
				queryParams: { patientId: this.patientId }
			});
	}

	goToEditProfile(): void {
		const person = {
			id: this.patientBasicData.id,
		};
		this.router.navigate([this.routePrefix + ROUTE_EDIT_PATIENT], {
			queryParams: person
		});
	}
	roleAccordingToId(roleId: number): string {

		return this.roles.find(rol => rol.id === roleId)?.description;
	}

	editRoles(): void {
		const dialog = this.dialog.open(EditRolesComponent, {
			width: DIALOG_SIZE,
			disableClose: true,
			data: {
				personId: this.personId,
				isProfessional: this.isProfessional,
				roles: this.roles,
				userId: this.userData.id,
				rolesByUser: this.rolesByUser
			}
		});
		dialog.afterClosed().subscribe((userRoles: UserRoleDto[]) => {
			if (userRoles) {
				this.rolesService.updateRoles(this.userData.id, userRoles).subscribe(_ => {
					this.snackBarService.showSuccess('pacientes.edit_roles.messages.SUCCESS');
					this.userRoles = [];
					this.rolesByUser = [];

					userRoles.forEach(e => {
						if (!this.userRoles.includes(this.roleAccordingToId(e.roleId))) {
							this.userRoles.push(this.roles.find(rol => rol.id === e.roleId)?.description);
							this.rolesByUser.push(e);
						}
					});
				},
					error => {
						error?.text ?
							this.snackBarService.showError(error.text) : this.snackBarService.showError('pacientes.edit_roles.messages.ERROR');
					});
			}
		});
	}
	toUpdateProfessionsAndSpecialties(professional: HealthcareProfessionalCompleteDto): void {
		this.ownProfessionsAndSpecialties = [];
		this.isProfessional = true;
		professional.professionalSpecialtyDtos.forEach((e: HealthcareProfessionalSpecialtyDto) => {
			this.ownProfessionsAndSpecialties.push(
				{
					professionDescription: (this.allProfessions.find(element =>
						element.id === e.professionalSpecialtyId,
					).description),
					professionId: e.professionalSpecialtyId,
					specialtyId: e.clinicalSpecialtyId,
					specialtyName: (this.allSpecialties.find(element =>
						element.id === e.clinicalSpecialtyId
					).name)
				})
		})
	}
	editProfessions(): void {

		const dialog = this.dialog.open(EditProfessionsComponent, {
			width: DIALOG_SIZE,
			data: {
				personId: this.personId, professionalId: this.professionalId, ownLicense: this.license,
				id: this.professionalSpecialtyId, allSpecialties: this.allSpecialties, allProfessions: this.allProfessions, ownProfessionsAndSpecialties: this.ownProfessionsAndSpecialties
			}
		});
		dialog.afterClosed().subscribe((professional: HealthcareProfessionalCompleteDto) => {
			if (professional) {
				this.license = professional.licenseNumber;
				if (this.professionalId === undefined) {
					this.professionalService.addProfessional(professional).subscribe(_ => {
						this.snackBarService.showSuccess('pacientes.edit_professions.messages.SUCCESS');
						this.toUpdateProfessionsAndSpecialties(professional);
						this.professionalService.get(this.personId).subscribe((profession: ProfessionalDto) => {
							if (profession)
								this.professionalId = profession.id;
						});
					},
						error => {
							error?.text ?
								this.snackBarService.showError(error.text) : this.snackBarService.showError('pacientes.edit_professions.messages.ERROR');
						})
				}
				else {
					this.professionalService.editProfessional(this.professionalId, professional).subscribe(_ => {
						this.snackBarService.showSuccess('pacientes.edit_professions.messages.SUCCESS');
						this.toUpdateProfessionsAndSpecialties(professional);
					},
						error => {
							error?.text ?
								this.snackBarService.showError(error.text) : this.snackBarService.showError('pacientes.edit_professions.messages.ERROR');
						}
					);
				}
			}
		})
	}

	reports(): void {
		this.dialog.open(ReportsComponent, {
			width: '700px',
			data: { patientId: this.patientId, patientName: patientCompleteName(this.patientBasicData) }
		});
	}

	addUser() {
		this.userService.addUser(this.personId)
			.subscribe(userId => {
				this.userService.getUserData(this.personId).subscribe(userDataDto => this.userData = userDataDto);
				this.snackBarService.showSuccess('pacientes.user_data.messages.SUCCESS');
				this.userId = userId;
				this.rolesService.getRolesByUser(userId).subscribe((roles: UserRoleDto[]) => {
					this.rolesByUser = roles;
				});
			}, _ => this.snackBarService.showError('pacientes.user_data.messages.ERROR'));
	}

	enableUser() {
		this.userService.enableUser(this.userData.id, this.form.value.enable)
			.subscribe(userId => {
				this.snackBarService.showSuccess('pacientes.user_data.messages.UPDATE_SUCCESS');
			}, error => {
				processErrors(error, (msg) => this.snackBarService.showError(msg));
				this.form.controls.enable.setValue(this.userData.enable);
			});
	}

	goResetAccessData() {
		this.userPasswordResetService.createTokenPasswordReset(this.userData.id)
			.subscribe(token => {
				window.open("/auth/access-data-reset/" + token, "_blank")
			}, (error) => {
				processErrors(JSON.parse(error), (msg) => this.snackBarService.showError(msg));
			});
	}


	goToEmergencyCareEpisode(): void {
		const url = `${AppRoutes.Institucion}/${this.contextService.institutionId}/${ROUTE_EMERGENCY_CARE_EPISODE_PREFIX}/${this.emergencyCareEpisodeSummary.id}`;
		this.router.navigate([url]);
	}

	newAppointment() {
		const url = `${AppRoutes.Institucion}/${this.contextService.institutionId}/turnos`;
		this.router.navigate([url], { queryParams: { idPaciente: this.patientId } });
	}

}
