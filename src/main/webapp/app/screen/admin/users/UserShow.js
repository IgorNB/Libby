import {ShowController} from 'ra-core';
import React from 'react';
import {BooleanField, NumberField, ShowView, SimpleShowLayout, TextField,} from 'react-admin';
import AvatarField from "../../../core/field/AvatarField"; // eslint-disable-line import/no-unresolved

const UserShow = props => (
    <ShowController {...props}>
        {controllerProps => (
            <ShowView {...props} {...controllerProps}>
                <SimpleShowLayout>
                    <TextField source="id" label="resources.users.fields.id"/>
                    <TextField source="name" label="resources.users.fields.name"/>
                    <NumberField source="version" label="resources.users.fields.version"/>
                    <AvatarField size={40} source="imageUrl" label="resources.users.fields.imageUrl"/>
                    <TextField source="email" label="resources.users.fields.email"/>
                    <BooleanField source="emailVerified" label="resources.users.fields.emailVerified"/>
                    <TextField source="provider" label="resources.users.fields.provider"/>
                    <TextField source="providerId" label="resources.users.fields.providerId"/>
                </SimpleShowLayout>
            </ShowView>
        )}
    </ShowController>
);

export default UserShow;
