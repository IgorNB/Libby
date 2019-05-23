import React from 'react';
import {ReferenceField, Show, SimpleShowLayout, TextField,} from 'react-admin';
import StarRatingField from "../../../core/field/StarRatingField";
import CompactListActionsToolbar from "../../../core/layout/CompactListActionsToolBar";

const CommentShow = props => (
    <Show {...props}>
        <SimpleShowLayout>
            <ReferenceField resource="commentsPublic" source="book.id" reference="booksPublic" label="resources.commentsPublic.fields.book.title" linkType="show">
                <TextField source="title" />
            </ReferenceField>
            <TextField source="createdBy.name" label="resources.commentsPublic.fields.createdBy.name"/>
            <CompactListActionsToolbar source="rating" label="resources.commentsPublic.fields.rating">
                <StarRatingField/>
            </CompactListActionsToolbar>
            <TextField source="body" label="resources.commentsPublic.fields.body"/>
        </SimpleShowLayout>
    </Show>
);

export default CommentShow;
