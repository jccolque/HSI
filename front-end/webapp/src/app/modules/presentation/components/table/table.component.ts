import { Component, OnInit, Input, ViewChild, Injectable } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { TableService } from '@core/services/table.service';
import { MatPaginator, MatPaginatorIntl } from '@angular/material/paginator';
import { ThemePalette } from '@angular/material/core';

@Component({
	selector: 'app-table',
	templateUrl: './table.component.html',
	styleUrls: ['./table.component.scss']
})
export class TableComponent implements OnInit {

	ActionDisplays = ActionDisplays;
	TableStyles = TableStyles;

	@Input() model: TableModel<any>;
	@Input() mainStyle: TableStyle = TableStyles.DEFAULT;

	@ViewChild(MatPaginator) set matPaginator(paginator: MatPaginator) {
		this.dataSource.paginator = paginator;
	 }


	dataSource = new MatTableDataSource<any>([]);

	columns: ColumnModel<any>[] = [];
	displayedColumns: string[] = [];
	filterEnabled: boolean = false;
	paginationEnabled: boolean = false;

	constructor(
		private tableService: TableService
	) { }

	ngOnInit(): void {
	}

	ngOnChanges() {
		this.setUpTable();
	}

	private setUpTable() {
		if (this.model) {
			this.columns = this.model.columns;
			this.dataSource.data = this.model.data;
			this.displayedColumns = this.columns?.map(c => c.columnDef);

			//filtering
			this.filterEnabled = this.model.enableFilter;
			this.dataSource.filterPredicate = this.tableService.predicateFilter;

			//pagination
			this.paginationEnabled = this.model.enablePagination;
			}
	}


	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.dataSource.filter = filterValue.trim().toLowerCase();
	}

}

export enum TableStyles {
	PRIMARY = 'primary',
	SECONDARY = 'secondary',
	DEFAULT = 'default',
}

export enum ActionDisplays {
	ICON = 'icon',
	BUTTON = 'button',
}


export type TableStyle = TableStyles.PRIMARY | TableStyles.SECONDARY | TableStyles.DEFAULT;
export type ActionDisplay = ActionDisplays.BUTTON | ActionDisplays.ICON;

export interface ColumnModel<T> {
	columnDef: string;
	header?: string;
	text?: (row: T) => string | number;
	action?: {
		displayType: ActionDisplay,
		display: string,
		matColor?: ThemePalette,
		do: (row: T) => void,
		hide?: (row: T) => boolean
	}
}

export interface TableModel<T> {
	columns: ColumnModel<T>[];
	data: T[];
	enableFilter?: boolean;
	enablePagination?: boolean;
}

@Injectable()
export class MatPaginatorIntlAR extends MatPaginatorIntl {
	itemsPerPageLabel = 'Items por página';
	nextPageLabel = 'Siguiente';
	previousPageLabel = 'Anterior';
	lastPageLabel = 'Última página';
	firstPageLabel = 'Primera página';

	getRangeLabel = function (page, pageSize, length) {
		if (length === 0 || pageSize === 0) {
			return '0 de ' + length;
		}
		length = Math.max(length, 0);
		const startIndex = page * pageSize;
		const endIndex = startIndex < length ?
			Math.min(startIndex + pageSize, length) :
			startIndex + pageSize;
		return startIndex + 1 + ' - ' + endIndex + ' de ' + length;
	};

}
