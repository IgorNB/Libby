import React, {Component} from 'react';
import {connect} from 'react-redux';
import {crudUpdate, SaveButton} from 'react-admin';
import PropTypes from 'prop-types';

// A custom action creator which modifies the values before calling the default crudCreate action creator
const saveWithNote = (values, basePath, redirectTo, command) =>
    crudUpdate('tasks', values["id"], {...values, command: command}, {}, basePath, redirectTo);

class SaveWithNoteButtonView extends Component {
    handleClickFactory = (command) => {
        return () => {
            const {basePath, handleSubmit, redirect, saveWithNote} = this.props;
            return handleSubmit(values => {
                saveWithNote(values, basePath, redirect, command);
            });
        };
    }


    //here we suppress javascript:S1854 "Dead stores should be removed" for handleSubmitWithRedirect,
    // which is used to pass all props expect this one to SaveButton
    render() {
        const {handleSubmitWithRedirect, command, ...props} = this.props;
        return (
            <SaveButton
                handleSubmitWithRedirect={this.handleClickFactory(command)}
                {...props}
            />
        );
    }
}

SaveWithNoteButtonView.propTypes = {
    command: PropTypes.string
};

export default connect(
    undefined,
    {saveWithNote}
)(SaveWithNoteButtonView);