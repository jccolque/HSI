import React from 'react';
import { Resource } from 'react-admin';
import SGXPermissions from "../libs/sgx/auth/SGXPermissions";

import cities from './cities';
import departments from './departments';
import institutions from './institutions';
import snvs from './snvs';
import addresses from './addresses';
import sectors from './sectors';
import clinicalspecialties from './clinicalspecialties';
import clinicalspecialtysectors from './clinicalspecialtysectors';
import rooms from './rooms';
import beds from './beds';
import healthcareprofessionals from './healthcareprofessionals';
import professionalSpecialties from './professionalspecialties';
import healthcareprofessionalspecialties from './healthcareprofessionalspecialties';
import doctorsoffices from './doctorsoffices';

import person from './person';
import admin from './admin';
import users from './users';
import passwordReset from './password-reset';
import careLines from './carelines';
import clinicalspecialtycarelines from './clinicalspecialtycarelines';
import documentTypes from './documenttypes';
import documentFiles from './documentfiles';
import properties from './properties';
import restClientMeasures from './rest-client-measures';
import medicalCoverage from './medicalcoverage';
import snomedgroups from './snomedgroups';


import { ROOT, ADMINISTRADOR } from './roles';
import snomedconcepts from './snomedconcepts';
import snomedrelatedgroups from './snomedrelatedgroups';
import medicalcoverageplans from "./medicalcoverageplans";

// Ampliación
//

const resourcesAdminInstitucional = [
    <Resource name="healthcareprofessionals" />,
    <Resource name="users" />,
];

const resourcesAdminRoot = (permissions: SGXPermissions) => [
    <Resource name="healthcareprofessionals" {...healthcareprofessionals} />,
    <Resource name="healthcareprofessionalspecialties" {...healthcareprofessionalspecialties} />,
    <Resource name="password-reset" {...passwordReset} />,
    <Resource name="roles" />,
    <Resource name="addresses" {...addresses} />,
    
    <Resource name="users" {...users} />,
    <Resource name="medicalcoveragetypes" />,
    <Resource name="medicalcoverageplans" {...medicalcoverageplans} />,
    <Resource name="medicalcoveragesmerge" />,
    // Ampliación
    // 
];

const resourcesFor = (permissions: SGXPermissions) =>
    permissions.hasAnyAssignment(
        ROOT, ADMINISTRADOR
    ) ? resourcesAdminRoot(permissions): resourcesAdminInstitucional;

const resources = (permissions: SGXPermissions) => [
    // staff
    <Resource name="person" {...person(permissions)} />,
    <Resource name="admin" {...admin(permissions)}/>,
    ...resourcesFor(permissions),
    // facilities
    <Resource name="institutions" {...institutions(permissions)} />,
    <Resource name="sectors" {...sectors} />,
    <Resource name="clinicalspecialties" {...clinicalspecialties(permissions)} />,
    <Resource name="clinicalspecialtysectors" {...clinicalspecialtysectors} />,
    <Resource name="doctorsoffices" {...doctorsoffices} />,
    <Resource name="rooms" {...rooms} />,
    <Resource name="beds" {...beds} />,
    <Resource name="clinicalspecialtycarelines" {...clinicalspecialtycarelines} />,
    <Resource name="carelines" {...careLines(permissions)} />,
    // debug
    <Resource name="snvs"  {...snvs} />,
    <Resource name="documentfiles" {...documentFiles(permissions)} />,
    <Resource name="rest-client-measures" {...restClientMeasures(permissions)} />,
    <Resource name="properties" {...properties(permissions)} />,
    // masterData
    <Resource name="cities" {...cities(permissions)} />,
    <Resource name="departments" {...departments(permissions)} />,
    <Resource name="addresses" {...addresses} />,
    <Resource name="documenttypes" {...documentTypes(permissions)} />,
    <Resource name="snomedgroups"   {...snomedgroups} />,
    <Resource name="medicalcoverages" {...medicalCoverage(permissions)} />,
    <Resource name="professionalspecialties" {...professionalSpecialties(permissions)} />,
    // more
    <Resource name="identificationTypes" />,
    <Resource name="dependencies" />,
    <Resource name="personextended" />,
    <Resource name="genders" />,
    <Resource name="sectortypes" />,
    <Resource name="agegroups" />,
    <Resource name="caretypes" />,
    <Resource name="sectororganizations" />,
    <Resource name="hospitalizationtypes" />,
    <Resource name="provinces" />,
    <Resource name="bedcategories" />,
    <Resource name="educationtypes" />,
    
    <Resource name="snomedgroupconcepts" />,
    <Resource name="snomedrelatedgroups"  {...snomedrelatedgroups} />,
    <Resource name="snomedconcepts" {...snomedconcepts} />,
    <Resource name="internmentepisodes" />,
];

export default resources;