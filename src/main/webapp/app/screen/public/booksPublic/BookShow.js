import {ShowController} from 'ra-core';
import React from 'react';
import AvatarField from '../../../core/field/AvatarField';
import {
    Datagrid,
    FunctionField,
    ImageField,
    Pagination,
    ReferenceManyField,
    ShowButton,
    ShowView,
    SimpleShowLayout,
    TextField,
    translate
} from 'react-admin'; // eslint-disable-line import/no-unresolved
// eslint-disable-line import/no-unresolved
// eslint-disable-line import/no-unresolved
import {Link} from 'react-router-dom';
import Button from '@material-ui/core/Button';
import BookTitle from './BookTitle';
import CompactListActionsToolbar from "../../../core/layout/CompactListActionsToolBar";
import StarRatingField from "../../../core/field/StarRatingField";
// eslint-disable-line import/no-unresolved
import {Link} from 'react-router-dom';
import Button from '@material-ui/core/Button';
import BookTitle from './BookTitle';
import CompactListActionsToolbar from "../../../core/layout/CompactListActionsToolBar";
import StarRatingField from "../../../core/field/StarRatingField";

const CreateRelatedComment = translate(({translate, record}) => (
    <Button
        component={Link}
        to={{
            pathname: '/commentsPublic/create',
            state: {record: {book: {id: record.id}}},
        }}
    >
        {translate('resources.booksPublic.action.COMMENT')}
    </Button>
));

const BookShow = props => (
    <ShowController title={<BookTitle/>} {...props}>
        {controllerProps => (
            <ShowView {...props} {...controllerProps}>
                <SimpleShowLayout>
                    <FunctionField style={{fontSize: 30}} addLabel={false} render={record =>
                        `${record.title} 
                        ( ${record.authors}
                        , ${record.originalPublicationYear} 
                        ${record.lang != null ? ',' + record.lang.code : ''}
                        )`
                    }/>

                    <StarRatingField addLabel={false} source="averageRating" label="book.averageRating"/>
                    <ImageField addLabel={false} source="smallImageUrl"
                                label="resources.booksPublic.fields.smallImageUrl"/>
                    {controllerProps.record && (controllerProps.record['isbn'] != null || controllerProps.record['isbn13'] != null) &&
                    <FunctionField label="isbn/isbn13" render={record => `${record.isbn} ${record.isbn13}`}/>
                    }
                    <ReferenceManyField
                        pagination={<Pagination/>}
                        perPage={5}
                        addLabel={false}
                        reference="commentsPublic"
                        target="book.id"
                        sort={{field: 'createdDate', order: 'DESC'}}
                    >
                        <Datagrid>
                            <CompactListActionsToolbar source="rating"
                                                       label="resources.booksPublic.referenceManyFields.comment.rating">
                                <StarRatingField/>
                            </CompactListActionsToolbar>
                            <AvatarField size={40} source="createdBy.imageUrl"
                                         label="resources.booksPublic.referenceManyFields.comment.createdBy.imageUrl"/>
                            <TextField source="createdBy.name"
                                       label="resources.booksPublic.referenceManyFields.comment.createdBy.name"/>
                            <TextField source="body" label="resources.booksPublic.referenceManyFields.comment.body"/>
                            <ShowButton/>
                        </Datagrid>
                    </ReferenceManyField>
                    <CreateRelatedComment/>
                </SimpleShowLayout>
            </ShowView>
        )}
    </ShowController>
);

export default BookShow;
