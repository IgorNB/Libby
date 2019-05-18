import React from 'react';
import {translate} from 'react-admin';

export default translate(({record, translate}) => (
    <span>
        {record ? translate('resources.tasksPublic.edit.title') + ' "' + record.bookTitle + '" ' : ''}
    </span>
));
