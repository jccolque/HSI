import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { EmergencyCareTypes, Triages } from '../../constants/masterdata';
import { VitalSingCurrentPrevious } from '@presentation/components/signo-vital-current-previous/signo-vital-current-previous.component';
import { TriageCategory } from '../triage-chip/triage-chip.component';

@Component({
	selector: 'app-triage-details',
	templateUrl: './triage-details.component.html',
	styleUrls: ['./triage-details.component.scss']
})

export class TriageDetailsComponent implements OnInit, OnChanges {

	@Input() triage: Triage;
	@Input() emergencyCareType: EmergencyCareTypes;

	readonly triages = Triages;
	readonly emergencyCareTypes = EmergencyCareTypes;

	vitalSignsCurrent: VitalSingCurrentPrevious[];

	constructor() {
	}

	ngOnChanges() {
		this.vitalSignsCurrent = this.includesVitalSigns() ? this.mapToVitalSignCurrentPrevious(this.triage) : undefined;
	}

	ngOnInit(): void {
	}

	private includesVitalSigns(): boolean {
		return !!this.emergencyCareType && !!this.triage;
	}

	private mapToVitalSignCurrentPrevious(triage: Triage): VitalSingCurrentPrevious[] {
		const vitalSignsCurrent: VitalSingCurrentPrevious[] = [];
		const LABELS = this.emergencyCareType === EmergencyCareTypes.PEDIATRIA ?
			{
				respiratoryRate: 'Frecuencia respiratoria',
				bloodOxygenSaturation: 'Saturación de oxígeno',
				heartRate: 'Frecuencia cardíaca',
			} :
			{
				systolicBloodPressure: 'Tensión arterial sistólica',
				diastolicBloodPressure: 'Tensión arterial diastólica',
				heartRate: 'Frecuencia cardíaca',
				respiratoryRate: 'Frecuencia respiratoria',
				temperature: 'Temperatura',
				bloodOxygenSaturation: 'Saturación de oxígeno',
			};

		Object.keys(LABELS).forEach(key => {
				const vitalSign = getVitalSign(key);
				vitalSignsCurrent.push({
					description: LABELS[key],
					currentValue: {
						value: Number(vitalSign?.value) || undefined,
						effectiveTime: vitalSign?.effectiveTime || undefined
					}
				});
			}
		);

		return vitalSignsCurrent;

		function getVitalSign(key): { value: number, effectiveTime: Date } {
			if (triage.vitalSigns && triage.vitalSigns[key]) {
				return triage.vitalSigns[key];
			}
			if (triage.breathing && (key === 'respiratoryRate' || key === 'bloodOxygenSaturation')) {
				return triage.breathing[key];
			}
			if (triage.circulation && key === 'heartRate') {
				return triage.circulation[key];
			}
			return undefined;
		}
	}

}

export interface Triage {
	creationDate: Date;
	category: TriageCategory;
	professional: {
		firstName: string,
		lastName: string
	};
	doctorsOfficeDescription?: string;
	vitalSigns?: {
		bloodOxygenSaturation: {
			value: string,
			effectiveTime: Date
		},
		diastolicBloodPressure?: {
			value: string,
			effectiveTime: Date
		},
		heartRate: {
			value: string,
			effectiveTime: Date
		},
		respiratoryRate: {
			value: string,
			effectiveTime: Date
		},
		systolicBloodPressure?: {
			value: string,
			effectiveTime: Date
		},
		temperature?: {
			value: string,
			effectiveTime: Date
		}
	};
	appearance?: {
		bodyTemperatureDescription?: string,
		cryingExcessive?: boolean,
		muscleHypertoniaDescription?: string
	};
	breathing?: {
		respiratoryRetractionDescription: string,
		stridor: boolean,
		respiratoryRate: {
			value: string
			effectiveTime: Date,
		},
		bloodOxygenSaturation: {
			value: string
			effectiveTime: Date,
		}
	};
	circulation?: {
		perfusionDescription: string,
		heartRate: {
			value: string
			effectiveTime: Date,
		}
	};
	notes?: string;
}
