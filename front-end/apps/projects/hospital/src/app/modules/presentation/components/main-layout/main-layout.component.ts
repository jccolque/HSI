import { Component, OnDestroy, ChangeDetectorRef, Input } from '@angular/core';
import { MediaMatcher } from '@angular/cdk/layout';

import { AuthenticationService } from '../../../auth/services/authentication.service';
import { MenuItem } from '../menu/menu.component';
import { OauthAuthenticationService } from "../../../auth/services/oauth-authentication.service";

@Component({
	selector: 'app-main-layout',
	templateUrl: './main-layout.component.html',
	styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent implements OnDestroy {
	opened = false;
	mobileQuery: MediaQueryList;
	private _mobileQueryListener: () => void;
	private _menuItems: MenuItem[];

	constructor(changeDetectorRef: ChangeDetectorRef, media: MediaMatcher,
				private authenticationService: AuthenticationService,
				private oauthAuthenticationService: OauthAuthenticationService,
	) {
		this.mobileQuery = media.matchMedia('(max-width: 600px)');
		this._mobileQueryListener = () => changeDetectorRef.detectChanges();
		this.mobileQuery.addEventListener('change', this._mobileQueryListener);
	}

	@Input() set menuItems(items: MenuItem[]) {
		this._menuItems = items;
		this.opened = (items && items.length) && !this.mobileQuery.matches;
	}

	get menuItems(): MenuItem[] {
		return this._menuItems;
	}

	ngOnDestroy(): void {
		this.mobileQuery.removeEventListener('change', this._mobileQueryListener);
	}

	logout(): void {
		this.authenticationService.logout();
		this.oauthAuthenticationService.logout();
	}
}
