import Card from '@material-ui/core/Card';
import Typography from '@material-ui/core/Typography';
import {withStyles} from '@material-ui/core/styles';
import React from 'react';
import {
    AutocompleteInput,
    DisabledInput,
    EditActions,
    EditController,
    Link,
    LongTextInput,
    minLength,
    ReferenceInput,
    SimpleForm,
    Title,
    translate,
} from 'react-admin';
import StarRatingInput from "../../../core/input/StarRatingInput";

const LinkToRelatedBook = translate(({translate,record}) => (
    record.book != null ?
        <Link to={`/books/${record.book.id}/show`}>
            <Typography variant="caption" color="inherit" align="right">
                {translate("resources.comments.action.linkToRelatedBook")}
            </Typography>
        </Link>
        :
        <div></div>
));

const editStyles = {
    actions: {
        float: 'right',
    },
    card: {
        marginTop: '1em',
        maxWidth: '30em',
    },
};

const CommentEdit = withStyles(editStyles)(
    translate(({classes, translate, ...props}) => (
    <EditController {...props}>
        {({resource, record, redirect, save, basePath, version}) => (
            <div className="edit-page">
                <Title defaultTitle={`${translate("resources.comments.edit.title")}  ${record ? record.id : ''}`}/>
                <div className={classes.actions}>
                    <EditActions basePath={basePath} resource={resource} data={record} hasShow hasList />
                </div>
                <Card className={classes.card}>
                    {record && (
                        <SimpleForm basePath={basePath} redirect={redirect} resource={resource} record={record} save={save} version={version} >
                            <DisabledInput source="id" fullWidth label="resources.comments.fields.id" />
                            <ReferenceInput source="book.id" reference="books"
                                            perPage={15} sort={{field: 'title', order: 'ASC'}} fullWidth label="resources.comments.fields.book.title">
                                <AutocompleteInput optionText="title" options={{fullWidth: true}} />
                            </ReferenceInput>
                            <LinkToRelatedBook/>
                            <StarRatingInput source="rating" fullWidth label="resources.comments.fields.rating"/>
                            <LongTextInput source="body" validate={minLength(10)} fullWidth label="resources.comments.fields.body"/>
                        </SimpleForm>
                    )}
                </Card>
            </div>
        )}
    </EditController>
)));

export default CommentEdit;
