import get from 'lodash/get';
import Rating from "material-ui-rating";
import React from 'react'


const StarRatingField = ({record, source}) => (
    <Rating
        value={get(record, source)}
        max={5}
        onChange={() => {
        }}
        readOnly={true}
    />
);

export default StarRatingField;
