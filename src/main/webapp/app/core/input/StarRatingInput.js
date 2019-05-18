import get from 'lodash/get';
import Rating from "material-ui-rating";
import React, {Fragment} from 'react'
import {change} from 'redux-form'
import {FormDataConsumer, REDUX_FORM_NAME} from "react-admin";

const StarRatingInput = ({source, formData, dispatch, ...rest}) => (
    <FormDataConsumer>
        {({formData, dispatch, ...rest}) => (
            <Fragment>

                <Rating
                    value={get(formData, source)}
                    onChange={value => dispatch(change(REDUX_FORM_NAME, source, value, false, false))}
                    max={5}
                    readOnly={false}
                    {...rest}
                />
            </Fragment>
        )}
    </FormDataConsumer>
);

export default StarRatingInput;
