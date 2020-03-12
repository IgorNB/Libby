import React from "react";
import {
    AutocompleteInput,
    DisabledInput,
    Edit,
    FormDataConsumer,
    Link,
    NumberInput,
    ReferenceInput,
    required,
    SaveButton,
    SimpleForm,
    TextInput,
    Toolbar,
    translate
} from 'react-admin'; // eslint-disable-line import/no-unresolved
// eslint-disable-line import/no-unresolved
// eslint-disable-line import/no-unresolved
// eslint-disable-line import/no-unresolved
// eslint-disable-line import/no-unresolved
import TaskTitle from "./TaskTitle";
import SaveWithCommandButton from "./TaskCommandButton";
import Grid from "@material-ui/core/Grid";
import RichTextInput from "ra-input-rich-text";
import validator from "validator";
import AvatarField from "../../../core/field/AvatarField";
import Typography from '@material-ui/core/Typography';
import TaskStatusColor from "../../../core/input/TaskStatusColor";


const CustomToolbar = translate(({translate, ...props}) => (
    <Toolbar {...props}>
        <SaveButton/>
        {
            props.record["availableCommands"].map(
                availableCommand =>
                    <SaveWithCommandButton
                        label={translate("resources.tasks.action." + availableCommand)}
                        redirect="edit"
                        submitOnEnter={false}
                        variant="flat"
                        command={availableCommand}
                    />
            )
        }
    </Toolbar>
));

const isUrlValidation = (value, allValues, props) => {
    if (value && !validator.isURL(value + "", {protocols: ["http", "https"]})) {
        return props.translate("resources.tasks.validation.urlFormat");
    }
    return undefined;
};

const isYearValidation = (value, allValues, props) => {
    if (value && !validator.isInt(value + "", {min: 0, max: 3000, allow_leading_zeroes: false})) {
        return props.translate("resources.tasks.validation.yearFormat");
    }
    return undefined;
};

const isISBN13Validation = (value, allValues, props) => {
    if (value && !validator.isISBN(value + "", "13")) {
        return props.translate("resources.tasks.validation.ISBN13Format");
    }
    return undefined;
};

const validateISBN13 = [isISBN13Validation];
const validateYear = [isYearValidation];
const validateUrl = [isUrlValidation];

const LinkToRelatedBook = translate(({translate,record}) => (
    record.book != null ?
    <Link to={record.book != null ? `/books/${record.book.id}/show` : '' } >
        <Typography variant="caption" color="inherit" align="left">
            {translate("resources.tasks.action.bookLink")}
        </Typography>
    </Link>
   : <div></div>
));

const TaskEdit =({ translate, classes, ...props }) =>  (
    <Edit undoable={false}  title={<TaskTitle/>} {...props}>
        <SimpleForm toolbar={<CustomToolbar/>} defaultValue={{average_note: 0}}>
            <Grid container direction="row" spacing={32} >
                <Grid item>
                    <DisabledInput source="id" style={{width: "400px"}} label="resources.tasks.fields.id"/>
                </Grid>
                <Grid item>
                    <DisabledInput source="version" label="resources.tasks.fields.version"/>
                </Grid>
                <Grid item>

                </Grid>
            </Grid>

            <Grid container direction="row" spacing={32} >
                <Grid item xs={12}>
                </Grid>
                <Grid item xs={12}>
                </Grid>
            </Grid>

            <Typography component="h2" variant="h1" color="inherit" align="left">
                {translate("resources.tasks.edit.taskFieldGroup")}
            </Typography>
            <TaskStatusColor disabled={true} isInput={true} {...props} source="workflowStep" label="resources.tasks.fields.workflowStep"/>
            <LinkToRelatedBook/>
            <ReferenceInput source="assignee.id" reference="users" label="resources.tasks.fields.assignee.name" >
                <AutocompleteInput optionText="name" />
            </ReferenceInput>
            <DisabledInput source="createdBy.name" label="resources.tasks.fields.createdBy.name"/>
            <DisabledInput source="points" label="resources.tasks.fields.points"/>

            <Grid container direction="row" spacing={32} >
                <Grid item xs={12}>
                </Grid>
                <Grid item xs={12}>
                </Grid>
            </Grid>
            <Typography component="h2" variant="h1" color="inherit" align="left">
                {translate("resources.tasks.edit.bookFieldGroup")}
            </Typography>
            <Grid container direction="row" spacing={32} >
                <Grid item>
                    <TextInput  autoFocus source="bookTitle" validate={required()} resettable style={{width: "400px"}} label="resources.tasks.fields.bookTitle"/>
                </Grid>

                <Grid item>
                    <TextInput source="bookAuthors" validate={required()} style={{width: "400px"}} label="resources.tasks.fields.bookAuthors"/>
                </Grid>
            </Grid>

            <Grid container direction="row" spacing={32}>

                <Grid item>
                    <FormDataConsumer>
                        {({ formData, ...rest }) =>
                            <AvatarField size={130} source="bookSmallImageUrl" record={formData} label="resources.tasks.fields.bookSmallImageUrl"/>
                        }
                    </FormDataConsumer>
                </Grid>
                <Grid item>
                    <TextInput source="bookSmallImageUrl" style={{width: "240px"}} validate={validateUrl} label="resources.tasks.fields.bookSmallImageUrl"/>
                </Grid>
                <Grid item>
                    <RichTextInput source="bookName" label="resources.tasks.fields.bookName"/>
                </Grid>
            </Grid>

            <Grid container direction="row" spacing={32}>
                <Grid item>
                    <NumberInput source="bookOriginalPublicationYear" validate={validateYear} label="resources.tasks.fields.bookOriginalPublicationYear"/>
                </Grid>
                <Grid item>
                    <ReferenceInput label="Lang" source="bookLang.id" reference="langs" label="resources.tasks.fields.bookLang.code">
                        <AutocompleteInput optionText="code" />
                    </ReferenceInput>
                </Grid>
                <Grid item>
                    <TextInput source="bookIsbn" label="resources.tasks.fields.bookIsbn"/>
                </Grid>
                <Grid item>
                    <TextInput source="bookIsbn13" label="resources.tasks.fields.bookIsbn13" validate={validateISBN13}/>
                </Grid>
            </Grid>

        </SimpleForm>
    </Edit>
);

export default translate(TaskEdit);
