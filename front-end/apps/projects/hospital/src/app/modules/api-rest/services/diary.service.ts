import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ContextService } from '@core/services/context.service';
import { DiaryADto, CompleteDiaryDto, DiaryDto } from '@api-rest/api-model';
import { environment } from '@environments/environment';
import { Observable } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class DiaryService {

	constructor(
		private readonly http: HttpClient,
		private readonly contextService: ContextService
	) { }


	addDiary(agenda: DiaryADto): Observable<number> {
		const url = `${environment.apiBase}/institutions/${this.contextService.institutionId}/medicalConsultations/diary`;
		return this.http.post<number>(url, agenda);
	}

	updateDiary(agenda: DiaryDto): Observable<number> {
		const url = `${environment.apiBase}/institutions/${this.contextService.institutionId}/medicalConsultations/diary/${agenda.id}`;
		return this.http.put<number>(url, agenda);
	}

	get(diaryId: number): Observable<CompleteDiaryDto> {
		const url = `${environment.apiBase}/institutions/${this.contextService.institutionId}/medicalConsultations/diary/${diaryId}`;
		return this.http.get<CompleteDiaryDto>(url);
	}

	delete(diaryId: number): Observable<boolean> {
		const url = `${environment.apiBase}/institutions/${this.contextService.institutionId}/medicalConsultations/diary/${diaryId}`;
		return this.http.delete<boolean>(url);
	}
}
