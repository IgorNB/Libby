import React from 'react'
import {AutocompleteInput, Create, LongTextInput, ReferenceInput, SimpleForm} from "react-admin"; // eslint-disable-line import/no-unresolved
import StarRatingInput from "../../../core/input/StarRatingInput";

const redirect = (basePath, id, data) => `/books/${data.book.id}/show`;

const defaultValue = {rating: 5};
const CommentCreate = props => (
    <Create {...props}>
        <SimpleForm defaultValue={defaultValue} redirect={redirect}>
            <ReferenceInput label="Book" source="book.id" reference="books">
                <AutocompleteInput optionText="title"/>
            </ReferenceInput>
            <StarRatingInput source="rating"/>
            <LongTextInput source="body"/>
        </SimpleForm>
    </Create>
);

export default CommentCreate;
