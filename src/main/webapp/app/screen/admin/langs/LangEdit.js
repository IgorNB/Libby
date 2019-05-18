import React from 'react';
import {DisabledInput, Edit, required, SimpleForm, TextInput,} from 'react-admin'; // eslint-disable-line import/no-unresolved


const LangEdit = props => (
    <Edit {...props}>
        <SimpleForm>
                <DisabledInput source="id" label="resources.langs.fields.id"/>
                <TextInput source="code" validate={required()} resettable label="resources.langs.fields.code"/>
        </SimpleForm>
    </Edit>
);

export default LangEdit;
