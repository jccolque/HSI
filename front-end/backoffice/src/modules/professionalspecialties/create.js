import React from 'react';
import {
    TextInput,
    ReferenceInput,
    SelectInput,
    Create,
    SimpleForm,
    required
} from 'react-admin';
import SaveCancelToolbar from "../../modules/components/save-cancel-toolbar";

const ProfessionalSpecialtyCreate = props => (
    <Create {...props}>
        <SimpleForm redirect="show" toolbar={<SaveCancelToolbar />} >
            <TextInput source="description" validate={[required()]} />
            <TextInput source="descriptionProfessionRef" validate={[required()]} />
            <TextInput source="sctidCode" validate={[required()]} />
            <ReferenceInput
                source="educationTypeId"
                reference="educationtypes"
                sort={{ field: 'description', order: 'ASC' }}
            >
                <SelectInput optionText="description" optionValue="id"/>
            </ReferenceInput>
        </SimpleForm>
    </Create>
);

export default ProfessionalSpecialtyCreate;
