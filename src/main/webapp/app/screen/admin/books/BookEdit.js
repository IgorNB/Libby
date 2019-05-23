import React from "react";
import {
    AutocompleteInput,
    DisabledInput,
    Edit,
    FormDataConsumer,
    NumberInput,
    ReferenceInput,
    required,
    SaveButton,
    SimpleForm,
    TextInput,
    Toolbar,
    translate
} from 'react-admin';
import BookTitle from "./BookTitle";
import Grid from "@material-ui/core/Grid";
import RichTextInput from "ra-input-rich-text";
import validator from "validator";
import AvatarField from "../../../core/field/AvatarField";
import Typography from '@material-ui/core/Typography';

const CustomToolbar = translate(({translate, ...props}) => (
    <Toolbar {...props}>
        <SaveButton/>
    </Toolbar>
));

const isUrlValidation = (value, allValues, props) => {
    if (value && !validator.isURL(value + "", {protocols: ["http", "https"]})) {
        return props.translate("resources.books.validation.urlFormat");
    }
    return undefined;
};

const isYearValidation = (value, allValues, props) => {
    if (value && !validator.isInt(value + "", {min: 0, max: 3000, allow_leading_zeroes: false})) {
        return props.translate("resources.books.validation.yearFormat");
    }
    return undefined;
};

const isISBN13Validation = (value, allValues, props) => {
    if (value && !validator.isISBN(value + "", "13")) {
        return props.translate("resources.books.validation.ISBN13Format");
    }
    return undefined;
};

const validateISBN13 = [isISBN13Validation];
const validateYear = [isYearValidation];
const validateUrl = [isUrlValidation];


const BookEdit =({ translate, classes, ...props }) =>  (
    <Edit undoable={false}  title={<BookTitle/>} {...props}>
        <SimpleForm toolbar={<CustomToolbar/>} defaultValue={{average_note: 0}}>
            <Grid container direction="row" spacing={32} >
                <Grid item>
                    <DisabledInput source="id" style={{width: "400px"}} label="resources.books.fields.id"/>
                </Grid>
                <Grid item>
                    <DisabledInput source="version" label="resources.books.fields.version"/>
                </Grid>
                <Grid item>
                    <DisabledInput source="createdBy.name" label="resources.books.fields.createdBy.name"/>
                </Grid>
            </Grid>



            <Grid container direction="row" spacing={32} >
                <Grid item xs={12}>
                </Grid>
                <Grid item xs={12}>
                </Grid>
            </Grid>
            <Typography component="h2" variant="h1" color="inherit" align="left">
                {translate("resources.books.edit.bookFieldGroup")}
            </Typography>
            <Grid container direction="row" spacing={32} >
                <Grid item>
                    <TextInput  autoFocus source="title" validate={required()} resettable style={{width: "400px"}} label="resources.books.fields.title"/>
                </Grid>

                <Grid item>
                    <TextInput source="authors" validate={required()} style={{width: "400px"}} label="resources.books.fields.authors"/>
                </Grid>
            </Grid>

            <Grid container direction="row" spacing={32}>

                <Grid item>
                    <FormDataConsumer>
                        {({ formData, ...rest }) =>
                            <AvatarField size={130} source="smallImageUrl" record={formData} label="resources.books.fields.smallImageUrl"/>
                        }
                    </FormDataConsumer>
                </Grid>
                <Grid item>
                    <TextInput source="smallImageUrl" style={{width: "240px"}} validate={validateUrl} label="resources.books.fields.smallImageUrl"/>
                </Grid>
                <Grid item>
                    <RichTextInput source="name" label="resources.books.fields.Name"/>
                </Grid>
            </Grid>

            <Grid container direction="row" spacing={32}>
                <Grid item>
                    <NumberInput source="originalPublicationYear" validate={validateYear} label="resources.books.fields.originalPublicationYear"/>
                </Grid>
                <Grid item>
                    <ReferenceInput label="Lang" source="lang.id" reference="langs" label="resources.books.fields.lang.code">
                        <AutocompleteInput optionText="code" />
                    </ReferenceInput>
                </Grid>
                <Grid item>
                    <TextInput source="isbn" label="resources.books.fields.isbn"/>
                </Grid>
                <Grid item>
                    <TextInput source="isbn13" label="resources.books.fields.isbn13" validate={validateISBN13}/>
                </Grid>
            </Grid>

        </SimpleForm>
    </Edit>
);

export default translate(BookEdit);
