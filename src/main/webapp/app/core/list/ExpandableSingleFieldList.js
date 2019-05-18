import React, {cloneElement, Component} from "react";
import PropTypes from "prop-types";
import classnames from "classnames";
import LinearProgress from "@material-ui/core/LinearProgress";
import {withStyles} from "@material-ui/core/styles";
import {linkToRecord} from "ra-core";

import {Link} from "ra-ui-materialui";
import ExpansionPanel from "@material-ui/core/ExpansionPanel";
import ExpansionPanelSummary from "@material-ui/core/ExpansionPanelSummary";
import ExpansionPanelDetails from "@material-ui/core/ExpansionPanelDetails";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";

const styles = {
    root: {display: "flex", flexWrap: "wrap"},
    expandableFieldListExpandPanelClass: {padding: "1px 1px"}
};

// useful to prevent click bubbling in a datagrid with rowClick
const stopPropagation = e => e.stopPropagation();

const sanitizeRestProps = ({
                               currentSort,
                               setSort,
                               isLoading,
                               loadedOnce,
                               ...props
                           }) => props;

/**
 * Iterator component to be used to display a list of entities, using a single field
 *
 * @example Display all the books by the current author
 * <ReferenceManyField reference="books" target="author_id">
 *     <ExpandableSingleFieldList>
 *         <ChipField source="title" />
 *     </ExpandableSingleFieldList>
 * </ReferenceManyField>
 *
 * By default, it includes a link to the <Edit> page of the related record
 * (`/books/:id` in the previous example).
 *
 * Set the linkType prop to "show" to link to the <Show> page instead.
 *
 * @example
 * <ReferenceManyField reference="books" target="author_id" linkType="show">
 *     <ExpandableSingleFieldList>
 *         <ChipField source="title" />
 *     </ExpandableSingleFieldList>
 * </ReferenceManyField>
 *
 * You can also prevent `<ExpandableSingleFieldList>` from adding link to children by setting
 * `linkType` to false.
 *
 * @example
 * <ReferenceManyField reference="books" target="author_id" linkType={false}>
 *     <ExpandableSingleFieldList>
 *         <ChipField source="title" />
 *     </ExpandableSingleFieldList>
 * </ReferenceManyField>
 */
export class ExpandableSingleFieldList extends Component {
    // Our handleClick does nothing as we wrap the children inside a Link but it is
    // required fo ChipField which uses a Chip from material-ui.
    // The material-ui Chip requires an onClick handler to behave like a clickable element
    handleClick = () => {
    };

    render() {
        const {
            classes = {},
            className,
            ids,
            data,
            loadedOnce,
            resource,
            basePath,
            children,
            linkType,
            ...rest
        } = this.props;

        const expandSize = 4;

        const contentArray = ids.map(id => {
            const resourceLinkPath = !linkType
                ? false
                : linkToRecord(basePath, id, linkType);

            if (resourceLinkPath) {
                return (
                    <Link
                        className={classnames(classes.link, className)}
                        key={id}
                        to={resourceLinkPath}
                        onClick={stopPropagation}
                    >
                        {cloneElement(children, {
                            record: data[id],
                            resource,
                            basePath,
                            // Workaround to force ChipField to be clickable
                            onClick: this.handleClick
                        })}
                    </Link>
                );
            }

            return cloneElement(children, {
                key: id,
                record: data[id],
                resource,
                basePath
            });
        });

        if (loadedOnce === false) {
            return <LinearProgress/>;
        }

        if (contentArray.length > expandSize) {
            return (
                <ExpansionPanel onClick={stopPropagation}>
                    <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}
                                           className="expandableFieldListExpandPanelClass">
                        <div
                            className={classnames(classes.root, className)}
                            {...sanitizeRestProps(rest)}
                        >
                            {contentArray.slice(0, expandSize)}
                        </div>
                    </ExpansionPanelSummary>
                    <ExpansionPanelDetails>
                        <div
                            className={classnames(classes.root, className)}
                            {...sanitizeRestProps(rest)}
                        >
                            {contentArray.slice(expandSize + 1, contentArray.length)}
                        </div>
                    </ExpansionPanelDetails>
                </ExpansionPanel>
            );

        } else {
            return (
                <div
                    className={classnames(classes.root, className)}
                    {...sanitizeRestProps(rest)}
                >
                    {contentArray}
                </div>
            );
        }
    }
}

ExpandableSingleFieldList.propTypes = {
    basePath: PropTypes.string,
    children: PropTypes.element.isRequired,
    classes: PropTypes.object,
    className: PropTypes.string,
    data: PropTypes.object,
    ids: PropTypes.array,
    linkType: PropTypes.oneOfType([PropTypes.string, PropTypes.bool])
        .isRequired,
    resource: PropTypes.string
};

ExpandableSingleFieldList.defaultProps = {
    classes: {},
    linkType: "edit"
};

export default withStyles(styles)(ExpandableSingleFieldList);
