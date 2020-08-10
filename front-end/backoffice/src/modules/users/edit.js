import React from 'react';
import {
    Edit,
    SimpleForm,
    BooleanInput,
    TextInput,
    ArrayInput,
    DateField,
    SimpleFormIterator,
    ReferenceInput,
    AutocompleteInput,
    SelectInput,
    required,
} from 'react-admin';

import OnlySaveToolbar from '../components/only-save-toolbar';
import PeopleReferenceField from '../people/PeopleReferenceField';
import Aside from './Aside'
import authProvider from '../../providers/authProvider';

const validateInstitutionRequired = (values) => {
    const errors = {};
    values.roles.forEach(function (roleAndInstitution) {

        let isAdmin = roleAndInstitution && (authProvider.getRole(roleAndInstitution.roleId) === 'ROOT' ||
                               authProvider.getRole(roleAndInstitution.roleId) === 'ADMINISTRADOR');
        if(!isAdmin && roleAndInstitution && !roleAndInstitution.institutionId) {
            errors.institutionId = ['La institucion es requerida'];
        }
    });

    return errors;
}

const UserEdit = props => (
    <Edit {...props} 
        aside={<Aside />} 
    >
        <SimpleForm toolbar={<OnlySaveToolbar />} validate={validateInstitutionRequired}>
            <PeopleReferenceField source="personId" />
            <TextInput source="username" validate={[required()]}/>
            <BooleanInput source="enable" validate={[required()]}/>
            <DateField source="lastLogin" showTime locales="es-AR"/>
            <ArrayInput source="roles" >
                <SimpleFormIterator >
                    <ReferenceInput source="roleId" reference="roles" validate={[required()]}>
                        <SelectInput optionText="description" optionValue="id"/>
                    </ReferenceInput>
                    <ReferenceInput
                        source="institutionId"
                        reference="institutions"
                        sort={{ field: 'name', order: 'ASC' }}
                        filterToQuery={searchText => ({name: searchText})}
                    >
                        <AutocompleteInput optionText="name" optionValue="id"/>
                    </ReferenceInput>
                </SimpleFormIterator>
            </ArrayInput>
        </SimpleForm>
    </Edit>
);

export default UserEdit;
