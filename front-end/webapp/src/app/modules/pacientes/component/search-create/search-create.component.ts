import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { VALIDATIONS } from "@core/utils/form.utils";
import { PatientService } from "@api-rest/services/patient.service";

const ROUTE_SEARCH = 'pacientes/search';
const ROUTE_NEW = 'pacientes/new';

@Component({
	selector: 'app-search-create',
	templateUrl: './search-create.component.html',
	styleUrls: ['./search-create.component.scss']
})
export class SearchCreateComponent implements OnInit {

	public formSearch: FormGroup;
	public formSearchSubmitted: boolean = false;
	public formAdd: FormGroup;
	public genderOptions = ['masculino', 'femenino'];
	public noIdentity = false;
	public causeOptionsArray = [
		{id: '0', description: 'Alta de emergencia'},
		{id: '1', description: 'Falta documento'},
		{id: '2', description: 'Recien nacido'},
		{id: '3', description: 'Otros'}];

	constructor(private formBuilder: FormBuilder,
				private router: Router,
				private patientService: PatientService) {
	}

	ngOnInit(): void {
		this.formSearch = this.formBuilder.group({
			identifType: [null, Validators.required],
			identifNumber: [null, Validators.required, Validators.maxLength(VALIDATIONS.MAX_LENGTH.dni)],
			gender: [null, Validators.required],
		});

		this.formAdd = this.formBuilder.group({
			causeOptions: [null, Validators.required],
			comments: [],
		});
	}

	search(): void {
		this.formSearchSubmitted = true;
		if (this.formSearch.valid) {
			this.patientService.quickGetPatient().subscribe(
				data => {
					if (data.length == 0) {
						this.router.navigate([ROUTE_SEARCH])
					}
				}
			);
		}
	}

	add(): void {
		if (this.formAdd.valid) {
			//TODO enviar datos de motivo y comentarios
			this.router.navigate([ROUTE_NEW]);
		}
	}

	noIdentityChange() {
		this.noIdentity = !this.noIdentity;
	}

}
