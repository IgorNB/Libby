import React from 'react'
import {SelectField, SelectInput, translate} from "react-admin";
import {withStyles} from '@material-ui/core/styles';
import classnames from 'classnames';
import compose from 'recompose/compose';

const styles = {
    SUBMITTED: {borderRadius: 5, background: 'orange', color: "white !important", fontWeight: "500", paddingLeft: 10},
    ESCALATED: {borderRadius: 5, background: 'red', color: "white !important", fontWeight: "500", paddingLeft: 10},
    APPROVED: {borderRadius: 5, background: 'green', color: "white !important", fontWeight: "500", paddingLeft: 10},
    INIT: {borderRadius: 5, background: 'blue', color: "white !important", fontWeight: "500", paddingLeft: 10},
};

function getClassName(classes, props) {
    return classnames(
        {[classes.INIT]: props.record[props.source] === 'INIT'},
        {[classes.SUBMITTED]: props.record[props.source] === 'SUBMITTED'},
        {[classes.ESCALATED]: props.record[props.source] === 'ESCALATED'},
        {[classes.APPROVED]: props.record[props.source] === 'APPROVED'},
    );
}

const choices = (translate) => [
    {id: 'INIT', name: translate("resources.tasks.status.INIT")},
    {id: 'SUBMITTED', name: translate("resources.tasks.status.SUBMITTED")},
    {id: 'APPROVED', name: translate("resources.tasks.status.APPROVED")},
    {id: 'ESCALATED', name: translate("resources.tasks.status.ESCALATED")},
];

const TaskStatusColor = ({isInput = false, translate, classes, ...props}) => (
    isInput === true ?
        <SelectInput {...props}
                     InputProps={{className: getClassName(classes, props)}} //for input components
                     choices={choices(translate)}
        />
        :
        <SelectField {...props}
                     className={getClassName(classes, props)} //for fields components
                     choices={choices(translate)}
        />

);

const enhance = compose(
    translate,
    withStyles(styles)
);

export default enhance(TaskStatusColor);
