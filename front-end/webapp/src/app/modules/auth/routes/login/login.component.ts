import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthenticationService } from '../../services/authentication.service';
import { catchError } from 'rxjs/operators';
import { ApiErrorMessage } from '@api-rest/api-model';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
	public apiError: ApiErrorMessage = null;
	public form: FormGroup;
	constructor(
		private formBuilder: FormBuilder,
		private authenticationService: AuthenticationService,
	) { }

	ngOnInit(): void {
		this.form = this.formBuilder.group({
			username: [null, Validators.required],
			password: [null, Validators.required],
		});
	}

	hasError(type: string, control: string): boolean {
		return this.form.get(control).hasError(type);
	}


	submit() {
		if (this.form.valid) {
			this.form.disable();

			this.authenticationService.login(
				this.form.value.username,
				this.form.value.password,
			).pipe(
				catchError((err: ApiErrorMessage) => {
					this.apiError = err;
					this.form.enable();
					throw err;
				}),
			).subscribe(
				() => this.authenticationService.goHome()
			);
		}
	}
}
