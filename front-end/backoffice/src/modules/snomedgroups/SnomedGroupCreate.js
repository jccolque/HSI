import React from 'react';
import {
    TextInput,
    Create,
    SimpleForm,
    required,
    maxLength,
    ReferenceInput,
    AutocompleteInput,
    SelectInput,
    FormDataConsumer,
    BooleanInput,
    usePermissions,
} from 'react-admin';
import CustomToolbar from "../components/CustomToolbar";
import { ADMINISTRADOR } from "../roles";
import UserReferenceInput from "../users/UserReferenceInput";

const InstitutionSelect = ({ formData, ...rest }) => {
    const { permissions } = usePermissions();
    const userIsAdmin = permissions?.roleAssignments?.filter(roleAssignment => (roleAssignment.role === ADMINISTRADOR.role)).length > 0;
    return (
        <ReferenceInput
            {...rest}
            reference="institutions"
            sort={{ field: 'name', order: 'ASC' }}
            filterToQuery={searchText => ({name: searchText})}
            filter={{ institutionId: formData.institutionId }}
            validate={!userIsAdmin ? [required()] : []}
        >
            <AutocompleteInput optionText="name" optionValue="id" resettable />
        </ReferenceInput>);
};

const SnomedGroupSelect = ({ formData, ...rest }) => {
    return (
        <ReferenceInput
            {...rest}
            reference="snomedgroups"
            sort={{ field: 'description', order: 'ASC' }}
            isRequired={true}
            validate={required()}
        >
            <SelectInput optionText="description" optionValue="id" />
        </ReferenceInput>);
};

const SnomedGroupCreate = props => (
    <Create {...props}>
        <SimpleForm redirect="show" toolbar={<CustomToolbar />}>

            {/* Description */}
            <TextInput source="description" validate={[
                required(),
                maxLength(100)]}/>

            {/* Custom id */}
            <TextInput source="customId" validate={[
                required(),
                maxLength(50)]}
            />

            {/* Parent Snomed Group */}
            <FormDataConsumer>
                {formDataProps => ( <SnomedGroupSelect {...formDataProps} source="groupId" />)}
            </FormDataConsumer>

            {/* Is template */}
            <BooleanInput source="template" disabled={false} initialValue={false}/>

            {/* Institution */}
            <FormDataConsumer>
                {formDataProps => ( <InstitutionSelect {...formDataProps} source="institutionId" />)}
            </FormDataConsumer>

            {/* User */}
            <UserReferenceInput source="userId" />

        </SimpleForm>
    </Create>
);

export default SnomedGroupCreate;
