import React from 'react';
import Avatar from '@material-ui/core/Avatar';
import get from 'lodash/get';

const AvatarField = ({record, source, size}) => (
    <Avatar
        src={get(record, source)}
        size={size}
        style={{width: size, height: size}}
    />
);

AvatarField.defaultProps = {
    size: 25,
};

export default AvatarField;
