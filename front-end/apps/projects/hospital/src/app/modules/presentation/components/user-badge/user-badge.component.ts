import { Component, Input } from '@angular/core';

@Component({
	selector: 'app-user-badge',
	templateUrl: './user-badge.component.html',
	styleUrls: ['./user-badge.component.scss']
})
export class UserBadgeComponent {
	@Input() userInfo: UserInfo;

	constructor() { }

	get avatar(): string {
		return this.userInfo?.avatar ?? 'assets/images/empty-profile.png';
	}

}

export class UserInfo {
	userName: string;
	fullName?: string;
	avatar?: string;
}
