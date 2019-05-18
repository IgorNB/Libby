import {withStyles} from '@material-ui/core/styles';
import React from 'react';
import {
    BooleanField,
    Datagrid,
    EditButton,
    List,
    NumberField,
    Responsive,
    ShowButton,
    SimpleList,
    TextField
} from 'react-admin';
import CompactListActionsToolbar from "../../../core/layout/CompactListActionsToolBar";
import AvatarField from "../../../core/field/AvatarField"; // eslint-disable-line import/no-unresolved


const styles = theme => ({
    title: {
        maxWidth: '20em',
        overflow: 'hidden',
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
    },
    hiddenOnSmallScreens: {
        [theme.breakpoints.down('md')]: {
            display: 'none',
        },
    },
    publishedAt: {fontStyle: 'italic'},
});

const UserList = withStyles(styles)(({classes, ...props}) => (
    <List {...props} sort={{field: 'id', order: 'DESC'}}>
        <Responsive
            small={
                <SimpleList
                    primaryText={record => record.name}
                />
            }
            medium={
                <Datagrid>
                    <TextField source="id" label="resources.users.fields.id"/>
                    <TextField source="name" cellClassName={classes.title} label="resources.users.fields.name"/>
                    <NumberField source="version" label="resources.users.fields.version"/>
                    <AvatarField size={40} source="imageUrl" label="resources.users.fields.imageUrl"/>
                    <TextField source="email" label="resources.users.fields.email"/>
                    <BooleanField source="emailVerified" label="resources.users.fields.emailVerified"/>
                    <TextField source="provider" label="resources.users.fields.provider"/>
                    <TextField source="providerId" label="resources.users.fields.providerId"/>
                    <CompactListActionsToolbar>
                        <EditButton/>
                        <ShowButton/>
                    </CompactListActionsToolbar>
                </Datagrid>
            }
        />
    </List>
));

export default UserList;
