import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { VALIDATIONS, hasError } from "@core/utils/form.utils";
import { PatientService } from "@api-rest/services/patient.service";

const ROUTE_SEARCH = 'pacientes/search';
const ROUTE_NEW = 'pacientes/temporary';
const ROUTE_PROFILE = 'pacientes/profile';

@Component({
	selector: 'app-search-create',
	templateUrl: './search-create.component.html',
	styleUrls: ['./search-create.component.scss']
})
export class SearchCreateComponent implements OnInit {

	public formSearch: FormGroup;
	public formSearchSubmitted: boolean = false;
	public formAdd: FormGroup;
	public genderOptions;
	public noIdentity = false;
	public causeOptionsArray = [
		{id: '0', description: 'Alta de emergencia'},
		{id: '1', description: 'Falta documento'},
		{id: '2', description: 'Recien nacido'},
		{id: '3', description: 'Otros'}];
	public identifyTypeArray;
	public hasError = hasError;

	constructor(private formBuilder: FormBuilder,
				private router: Router,
				private patientService: PatientService) {
	}

	ngOnInit(): void {
		this.formSearch = this.formBuilder.group({
			identifType: [null, Validators.required],
			identifNumber: [null, [Validators.required, Validators.maxLength(VALIDATIONS.MAX_LENGTH.identif_number)]],
		});
		this.patientService.getIdentitifacionType().subscribe(
			identificationTypes => { 
				this.identifyTypeArray = identificationTypes;
				this.formSearch.addControl('identifType',new FormControl(null, Validators.required));
		});
		this.patientService.getGenders().subscribe(
			genders => { 
				this.genderOptions = genders;
				this.formSearch.addControl('gender',new FormControl(null, Validators.required));
		});
		this.formAdd = this.formBuilder.group({
			causeOptions: [null, Validators.required],
			comments: [],
		});
	}

	search(): void {
		this.formSearchSubmitted = true;
		if (this.formSearch.valid) {
			let searchRequest = {
				identificationTypeId: this.formSearch.controls.identifType.value,
				identificationNumber: this.formSearch.controls.identifNumber.value,
				genderId: this.formSearch.controls.gender.value,
			}
			this.patientService.quickGetPatient(searchRequest).subscribe(
				data => {
					if (!data.length) {
						this.router.navigate([ROUTE_SEARCH],
							{
								queryParams: {
									identificationTypeId: this.formSearch.controls.identifType.value,
									identificationNumber: this.formSearch.controls.identifNumber.value,
									genderId: this.formSearch.controls.gender.value
								}
							});
					} else {
						let idPatient = data[0].id;
						this.router.navigate([ROUTE_PROFILE, {id: idPatient}]);
					}
				}
			);
		}
	}

	add(): void {
		if (this.formAdd.valid) {
			this.router.navigate([ROUTE_NEW],
				{
					queryParams: {
						causeOptions: this.formAdd.controls.causeOptions.value,
						comments: this.formAdd.controls.comments.value
					}
				});
		}
	}

	noIdentityChange() {
		this.noIdentity = !this.noIdentity;
	}

}
