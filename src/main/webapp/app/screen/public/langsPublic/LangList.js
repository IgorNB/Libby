import {withStyles} from '@material-ui/core/styles';
import React from 'react';
import {Datagrid, EditButton, List, NumberField, Responsive, ShowButton, SimpleList, TextField} from 'react-admin';
import CompactListActionsToolbar from "../../../core/layout/CompactListActionsToolBar"; // eslint-disable-line import/no-unresolved


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

const LangList = withStyles(styles)(({classes, ...props}) => (
    <List {...props} sort={{field: 'code', order: 'DESC'}}>
        <Responsive
            small={
                <SimpleList
                    primaryText={record => record.name}
                />
            }
            medium={
                <Datagrid>
                    <TextField source="id" label="resources.langs.fields.id"/>
                    <TextField source="code" cellClassName={classes.title} label="resources.langs.fields.code"/>
                    <NumberField source="version" label="resources.langs.fields.version"/>
                    <CompactListActionsToolbar>
                        <EditButton/>
                        <ShowButton/>
                    </CompactListActionsToolbar>
                </Datagrid>
            }
        />
    </List>
));

export default LangList;
