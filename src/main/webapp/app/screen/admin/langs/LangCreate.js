import React from 'react';

import {Create, required, SimpleForm, TextInput} from "react-admin"; // eslint-disable-line import/no-unresolved


const LangCreate = ({permissions, ...props}) => (
    <Create {...props}>
        <SimpleForm>
            <TextInput source="code" validate={required()} resettable label="resources.langs.fields.code"/>
        </SimpleForm>
    </Create>
);

export default LangCreate;
