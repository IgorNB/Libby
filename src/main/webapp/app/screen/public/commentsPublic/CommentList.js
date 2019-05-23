import React from 'react';
import ChevronLeft from '@material-ui/icons/ChevronLeft';
import ChevronRight from '@material-ui/icons/ChevronRight';
import PersonIcon from '@material-ui/icons/Person';
import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import Card from '@material-ui/core/Card';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import CardHeader from '@material-ui/core/CardHeader';
import Grid from '@material-ui/core/Grid';
import Toolbar from '@material-ui/core/Toolbar';
import {withStyles} from '@material-ui/core/styles';
import {
    Filter,
    List,
    PaginationLimit,
    ReferenceField,
    Responsive,
    ShowButton,
    SimpleList,
    TextField,
    translate,
} from 'react-admin';
import StarRatingField from "../../../core/field/StarRatingField";
import CompactListActionsToolbar from "../../../core/layout/CompactListActionsToolBar"; // eslint-disable-line import/no-unresolved

const CommentFilter = props => (
    <Filter {...props}>

    </Filter>
);


const CommentPagination = translate(
    ({isLoading, ids, page, perPage, total, setPage, translate}) => {
        const nbPages = Math.ceil(total / perPage) || 1;
        if (!isLoading && (total === 0 || (ids && !ids.length))) {
            return <PaginationLimit total={total} page={page} ids={ids}/>;
        }

        return (
            nbPages > 1 && (
                <Toolbar>
                    {page > 1 && (
                        <Button
                            color="primary"
                            key="prev"
                            onClick={() => setPage(page - 1)}
                        >
                            <ChevronLeft/>
                            &nbsp;
                            {translate('ra.navigation.prev')}
                        </Button>
                    )}
                    {page !== nbPages && (
                        <Button
                            color="primary"
                            key="next"
                            onClick={() => setPage(page + 1)}
                        >
                            {translate('ra.navigation.next')}&nbsp;
                            <ChevronRight/>
                        </Button>
                    )}
                </Toolbar>
            )
        );
    }
);

const listStyles = theme => ({
    card: {
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
    },
    cardContent: theme.typography.body1,
    cardLink: {
        ...theme.typography.body1,
        flexGrow: 1,
    },
    cardLinkLink: {
        display: 'inline',
    },
    cardActions: {
        justifyContent: 'flex-end',
    },
});

const CommentGrid = withStyles(listStyles)(
    translate(({classes, ids, data, basePath, translate}) => (
        <Grid spacing={16} container style={{padding: '0 1em'}}>
            {ids.map(id => (
                <Grid item key={id} sm={12} md={6} lg={4}>
                    <Card className={classes.card}>
                        <CardHeader
                            className="comment"
                            title={<ReferenceField resource="commentsPublic" record={data[id]} source="book.id" reference="booksPublic" basePath={basePath} linkType="show">
                                        <TextField source="title" className={classes.cardLinkLink} />
                                    </ReferenceField> }
                            subheader={ <TextField record={data[id]} source="createdBy.name"  /> }
                            avatar={ <Avatar> <PersonIcon/> </Avatar> }
                        />
                        <CardContent className={classes.cardContent}>
                            <CompactListActionsToolbar record={data[id]} source="rating">
                                <StarRatingField />
                            </CompactListActionsToolbar>
                            <TextField record={data[id]} source="body"/>
                        </CardContent>
                        <CardActions className={classes.cardActions}>
                            <ShowButton resource="booksPublic" basePath={basePath} record={data[id]} />
                        </CardActions>
                    </Card>
                </Grid>
            ))}
        </Grid>
    ))
);

CommentGrid.defaultProps = {
    data: {},
    ids: [],
};

const CommentMobileList = props => (
    <SimpleList
        primaryText={record => record.book != null ? record.book.name : ''}
        secondaryText={record => record.body}
        /*tertiaryText={record => new Date(record.created_at).toLocaleDateString() }*/
        leftAvatar={() => <PersonIcon/>}
        {...props}
    />
);

const CommentList = props => (
    <List {...props}
        perPage={6}
        filters={<CommentFilter/>}
        pagination={<CommentPagination/>}
    >
        <Responsive small={<CommentMobileList/>} medium={<CommentGrid/>}/>
    </List>
);

export default CommentList;
