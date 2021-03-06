import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

import { PermissionsService } from '@core/services/permissions.service';
import { ContextService } from '@core/services/context.service';
import { FeatureFlagService } from '@core/services/feature-flag.service';
import { AccountService } from '@api-rest/services/account.service';
import { RoleAssignmentDto } from '@api-rest/api-model';

import { MenuItem, defToMenuItem } from '@presentation/components/menu/menu.component';
import { UserInfo } from '@presentation/components/user-badge/user-badge.component';
import { mapToUserInfo } from '@api-presentation/mappers/user-person-dto.mapper';

import { MenuService } from '@extensions/services/menu.service';

import { LoggedUserService } from '../auth/services/logged-user.service';
import { NO_ROLES_USER_SIDEBAR_MENU, ROLES_USER_SIDEBAR_MENU } from './constants/menu';

import { HomeRoutes } from '../home/home-routing.module';
import { AppRoutes } from '../../app-routing.module';

@Component({
	selector: 'app-home',
	templateUrl: './home.component.html',
	styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
	userProfileLink = ['/', AppRoutes.Home, HomeRoutes.Profile];
	menuItems$: Observable<MenuItem[]>;
	userInfo: UserInfo;

	private readonly NO_INSTITUTION = -1;

	constructor(
		private contextService: ContextService,
		private extensionMenuService: MenuService,
		private permissionsService: PermissionsService,
		private accountService: AccountService,
		private featureFlagService: FeatureFlagService,
		private loggedUserService: LoggedUserService,
	) { }

	ngOnInit(): void {
		this.contextService.setInstitutionId(this.NO_INSTITUTION);

		this.loggedUserService.assignments$.subscribe(roleAssignment => {
			const menuItemDefs = this.userHasAnyRole(roleAssignment)? ROLES_USER_SIDEBAR_MENU : NO_ROLES_USER_SIDEBAR_MENU;
			this.menuItems$ = this.featureFlagService.filterItems$(menuItemDefs)
				.pipe(
					switchMap(menu => this.permissionsService.filterItems$(menu)),
					map(menu => menu.map(defToMenuItem)),
					switchMap(items => this.extensionMenuService.getSystemMenuItems().pipe(
						map(extesionItems => [...items, ...extesionItems]),
					)),
				);

		});

		this.accountService.getInfo()
			.subscribe(
				userInfo => this.userInfo = mapToUserInfo(userInfo.email, userInfo.personDto)
			);
	}

	private userHasAnyRole(roleAssignments: RoleAssignmentDto[]): boolean {
		return (roleAssignments.length > 0);
	}

}
