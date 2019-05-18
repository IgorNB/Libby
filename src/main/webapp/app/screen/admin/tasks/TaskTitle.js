import React from 'react';
import {translate} from 'react-admin';

export default translate(({record, translate}) => (
    <span>
        {record ? translate('resources.tasks.edit.title', {title: record.title}) : ''}
    </span>
));
