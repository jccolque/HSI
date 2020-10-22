import React from 'react';
import {
    ReferenceInput,
    AutocompleteInput,
    SelectInput,
    Create,
    SimpleForm,
    required
} from 'react-admin';
import SaveCancelToolbar from "../../modules/components/save-cancel-toolbar";

const searchToFilter = searchText => ({description: searchText ? searchText : -1});
const searchSpecialtyToFilter = searchText => ({name: searchText ? searchText : -1});
const renderSpecialty = (choice) => `${choice.description} - ${choice.descriptionProfessionRef}`;
const HealthcareProfessionalSpecialtyCreate = props => (
    <Create {...props}>
        <SimpleForm redirect="show" toolbar={<SaveCancelToolbar />}>
            <ReferenceInput
                source="healthcareProfessionalId"
                reference="healthcareprofessionals"
                label="resources.healthcareprofessionalspecialties.fields.healthcareProfessionalId"
                sort={{ field: 'licenseNumber', order: 'ASC' }}
            >
                <SelectInput optionText="licenseNumber" optionValue="id" validate={[required()]} options={{ disabled: true }} />
            </ReferenceInput>

            <ReferenceInput
                source="professionalSpecialtyId"
                reference="professionalspecialties"
                sort={{ field: 'description', order: 'ASC' }}
                filterToQuery={searchToFilter}
            >
                <AutocompleteInput optionText={renderSpecialty} optionValue="id" validate={[required()]} />
            </ReferenceInput>

            <ReferenceInput
                source="clinicalSpecialtyId"
                reference="clinicalspecialties"
                sort={{ field: 'name', order: 'ASC' }}
                filterToQuery={searchSpecialtyToFilter}
            >
                <AutocompleteInput optionText="name" optionValue="id" validate={[required()]} />
            </ReferenceInput>
        </SimpleForm>
    </Create>
);

export default HealthcareProfessionalSpecialtyCreate;
