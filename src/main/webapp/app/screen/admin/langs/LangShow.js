import {ShowController} from 'ra-core';
import React from 'react';
import {ShowView, SimpleShowLayout, TextField,} from 'react-admin';

const LangShow = props => (
    <ShowController {...props}>
        {controllerProps => (
            <ShowView {...props} {...controllerProps}>
                <SimpleShowLayout>
                        <TextField source="id" label="resources.langs.fields.id"/>
                        <TextField source="code" label="resources.langs.fields.code"/>
                </SimpleShowLayout>
            </ShowView>
        )}
    </ShowController>
);

export default LangShow;
