import React from 'react';
import {ReferenceField, Show, SimpleShowLayout, TextField,} from 'react-admin';
import StarRatingField from "../../../core/field/StarRatingField";
import CompactListActionsToolbar from "../../../core/layout/CompactListActionsToolBar";

const CommentShow = props => (
    <Show {...props}>
        <SimpleShowLayout>
            <ReferenceField resource="comments" source="book.id" reference="books" label="resources.comments.fields.book.title">
                <TextField source="title" />
            </ReferenceField>
            <ReferenceField resource="comments" source="createdBy.id" reference="users" label="resources.comments.fields.createdBy.name">
                <TextField source="name" />
            </ReferenceField>
            <CompactListActionsToolbar source="rating" label="resources.comments.fields.rating">
                <StarRatingField/>
            </CompactListActionsToolbar>
            <TextField source="body" label="resources.comments.fields.body"/>
        </SimpleShowLayout>
    </Show>
);

export default CommentShow;
