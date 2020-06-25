import React from 'react';
import {
    List,
    Datagrid,
    TextField,
    ReferenceField,
    DeleteButton, Filter, ReferenceInput, SelectInput,
} from 'react-admin';
import SubReference from '../components/subreference';

const ClinicalSpecialtySectorFilter = props =>(
    <Filter {...props}>
        <ReferenceInput source="sectorId" reference="sectors" alwaysOn allowEmpty={false}>
            <SelectInput optionText="description" />
        </ReferenceInput>
    </Filter>
);

const ClinicalSpecialtySectorList = props => (
    <List {...props} hasCreate={false} filters={<ClinicalSpecialtySectorFilter />}>
        <Datagrid rowClick="show">
            <TextField source="description"/>
            <ReferenceField source="sectorId" reference="sectors">
                <TextField source="description" />
            </ReferenceField>
            {/*Referencia a la institución del sector*/}
            <ReferenceField source="sectorId" reference="sectors" link={false} label="resources.sectors.fields.institutionId">
                <SubReference source="institutionId" reference="institutions" link={false}>
                    <TextField source="name"/>
                </SubReference>
            </ReferenceField>
            <ReferenceField source="clinicalSpecialtyId" reference="clinicalspecialties">
                <TextField source="name" />
            </ReferenceField>
            <DeleteButton />
        </Datagrid>
    </List>
);

export default ClinicalSpecialtySectorList;

