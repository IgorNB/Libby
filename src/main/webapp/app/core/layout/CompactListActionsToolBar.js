import {withStyles} from "@material-ui/core/styles";
import React, {Children, cloneElement} from "react";

export const CompactListActionsToolbar = withStyles({
    toolbar: {
        alignItems: 'center',
        display: 'flex',
    },
})(({classes, children, ...props}) => (
    <div className={classes.toolbar}>
        {Children.map(children, button => cloneElement(button, props))}
    </div>
));

export default CompactListActionsToolbar;