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
import AvatarField from "../../../core/field/AvatarField";
import TaskStatusColor from "../../../core/input/TaskStatusColor";

const TaskFilter = props => (
    <Filter {...props}>
        <SearchInput source="q" alwaysOn label="resources.tasks.fields.q"/>
        <TaskStatusColor source="workflowStep" isInput = {true} alwaysOn label="resources.tasks.fields.workflowStep"/>
        <TextInput source="bookTitle" alwaysOn label="resources.tasks.fields.bookTitle"/>
        <TextInput source="bookAuthors" alwaysOn label="resources.tasks.fields.bookAuthors"/>
        <TextInput source="id" label="resources.tasks.fields.id"/>
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
                    <ReferenceField source="assignee.id" reference="users" label="resources.tasks.fields.assignee.name" allowEmpty>
                        <TextField source="name" />
                    </ReferenceField>
                    <ReferenceField source="createdBy.id" reference="users" label="resources.tasks.fields.createdBy.name" allowEmpty>
                        <TextField source="name" />
                    </ReferenceField>
                    <TaskStatusColor source="workflowStep" label="resources.tasks.fields.workflowStep"/>
                    <ReferenceField source="book.id" reference="books" label="resources.tasks.fields.book.title" allowEmpty>
                        <TextField source="title" />
                    </ReferenceField>
                    <AvatarField size={40} source="bookSmallImageUrl" label="resources.tasks.fields.bookSmallImageUrl"/>
                    <TextField source="bookTitle" label="resources.tasks.fields.bookTitle"/>
                    <TextField source="bookAuthors" label="resources.tasks.fields.bookAuthors"/>
                    <NumberField source="bookOriginalPublicationYear" label="resources.tasks.fields.bookOriginalPublicationYear"/>
                    <TextField source="bookIsbn" label="resources.tasks.fields.bookIsbn" cellClassName={classes.title}/>
                    <TextField source="bookIsbn13" label="resources.tasks.fields.bookIsbn13" cellClassName={classes.title}/>
                    <TextField source="bookOriginalTitle" label="resources.tasks.fields.bookOriginalTitle" />
                    <ReferenceField label="Lang" label="resources.tasks.fields.bookLang.code" source="bookLang.id" reference="langs" allowEmpty>
                        <TextField source="code" />
                    </ReferenceField>
                    <TextField source="id" label="resources.tasks.fields.id"/>
                </Datagrid>
            }
        />
    </List>
));

export default TaskList;
