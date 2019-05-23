import {withStyles} from '@material-ui/core/styles';
import React from 'react';
import CompactListActionsToolbar from "../../../core/layout/CompactListActionsToolBar";
import {
    Datagrid,
    Filter,
    ImageField,
    List,
    NumberField,
    NumberInput,
    ReferenceField,
    Responsive,
    SearchInput,
    SimpleList,
    TextField,
    TextInput
} from 'react-admin';
import StarRatingField from "../../../core/field/StarRatingField";

const BookFilter = props => (
    <Filter {...props}>
        <SearchInput source="q" alwaysOn label="resources.books.fields.q"/>
        <TextInput source="title" alwaysOn label="resources.books.fields.title"/>
        <TextInput source="authors" alwaysOn label="resources.books.fields.authors"/>
        <NumberInput source="ratingsCount" label="resources.books.fields.ratingsCount"/>
        <NumberInput source="averageRating" label="resources.books.fields.averageRating"/>
        <TextInput source="id" label="resources.books.fields.id"/>
    </Filter>
);

const styles = theme => ({
    title: {
        maxWidth: "20em",
        overflow: "hidden",
        textOverflow: "ellipsis",
        whiteSpace: "nowrap"
    },
    authors: {
        maxWidth: "20em",
        overflow: "hidden",
        textOverflow: "ellipsis",
        whiteSpace: "nowrap"
    },
    hiddenOnSmallScreens: {
        [theme.breakpoints.down("md")]: {
            display: "none"
        }
    }
});

const rowClick = () => {
    return "show";
};

const BookList = withStyles(styles)(({classes, ...props}) => (
    <List
        {...props}
        filters={<BookFilter/>}
        //sort={{field: "ratingsCount", order: "DESC"}}
    >
        <Responsive
            small={
                <SimpleList
                    primaryText={record => record.title}
                />
            }
            medium={
                <Datagrid rowClick={rowClick}>
                    <ImageField source="smallImageUrl" reference="users" label="resources.books.fields.smallImageUrl"/>
                    <TextField source="title" label="resources.books.fields.title"/>
                    <TextField source="authors" label="resources.books.fields.authors"/>
                    <NumberField source="originalPublicationYear"
                                 label="resources.books.fields.originalPublicationYear"/>
                    <NumberField source="averageRating" label="resources.books.fields.averageRating"/>
                    <CompactListActionsToolbar cellClassName={classes.title} source="averageRating" label="">
                        <StarRatingField className={classes.toolbar}/>
                    </CompactListActionsToolbar>

                    <NumberField source="ratingsCount" label="resources.books.fields.ratingsCount"/>
                    <TextField source="isbn" cellClassName={classes.title} label="resources.books.fields.isbn"/>
                    <TextField source="isbn13" cellClassName={classes.title} label="resources.books.fields.isbn13"/>
                    <ReferenceField label="Lang" source="lang.id" reference="langs" allowEmpty
                                    label="resources.books.fields.lang.code">
                        <TextField source="code"/>
                    </ReferenceField>
                    <TextField source="id" label="resources.books.fields.id"/>
                </Datagrid>
            }
        />
    </List>
));

export default BookList;
