import {withStyles} from '@material-ui/core/styles';
import React from 'react';
import {
    Datagrid,
    Filter,
    List,
    NumberField,
    ReferenceField,
    Responsive,
    SearchInput,
    SimpleList,
    TextField,
    TextInput
} from 'react-admin';
import TaskStatusColor from "../../../core/input/TaskStatusColor";
import AvatarField from "../../../core/field/AvatarField";


const TaskFilter = props => (
        <Filter {...props}>
            <SearchInput source="q" alwaysOn label="resources.tasksPublic.fields.q"/>
            <TaskStatusColor source="workflowStep" isInput = {true} alwaysOn label="resources.tasksPublic.fields.workflowStep"/>
            <TextInput source="bookTitle" alwaysOn label="resources.tasksPublic.fields.bookTitle"/>
            <TextInput source="bookAuthors" alwaysOn label="resources.tasksPublic.fields.bookAuthors"/>
            <TextInput source="id" label="resources.tasksPublic.fields.id"/>
        </Filter>
);

const styles = theme => ({
    title: {
        maxWidth: "20em",
        overflow: "hidden",
        textOverflow: "ellipsis",
        whiteSpace: "nowrap"
    },
    hiddenOnSmallScreens: {
        [theme.breakpoints.down("md")]: {
            display: "none"
        }
    },
    publishedAt: {fontStyle: "italic"}
});

const rowClick = () => {
    return "edit";
};

const TaskList = withStyles(styles)(({classes, ...props}) => (
    <List
        {...props}
        filters={<TaskFilter/>}
        sort={{field: "workflowStep", order: "DESC"}}
    >
        <Responsive
            small={
                <SimpleList
                    primaryText={record => record.title}
                />
            }
            medium={
                <Datagrid rowClick={rowClick}>
                    <TextField source="assignee.name" label="resources.tasksPublic.fields.assignee.name"/>
                    <TaskStatusColor source="workflowStep" label="resources.tasksPublic.fields.workflowStep"/>
                    <AvatarField size={40} source="bookSmallImageUrl" label="resources.tasksPublic.fields.bookSmallImageUrl"/>
                    <TextField source="bookTitle" label="resources.tasksPublic.fields.bookTitle"/>
                    <TextField source="bookAuthors" label="resources.tasksPublic.fields.bookAuthors"/>
                    <NumberField source="bookOriginalPublicationYear" label="resources.tasksPublic.fields.bookOriginalPublicationYear"/>
                    <TextField source="bookIsbn" label="resources.tasksPublic.fields.bookIsbn" cellClassName={classes.title}/>
                    <TextField source="bookIsbn13" label="resources.tasksPublic.fields.bookIsbn13" cellClassName={classes.title}/>
                    <TextField source="bookOriginalTitle" label="resources.tasksPublic.fields.bookOriginalTitle" />
                    <ReferenceField label="Lang" label="resources.tasksPublic.fields.bookLang.code" source="bookLang.id" reference="langsPublic" allowEmpty>
                        <TextField source="code" />
                    </ReferenceField>
                    <TextField source="id" label="resources.tasksPublic.fields.id"/>
                </Datagrid>
            }
        />
    </List>
));

export default TaskList;
