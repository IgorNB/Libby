import React from 'react';
import {Route} from 'react-router-dom';
import OAuth2RedirectHandler from './OAuth2RedirectHandler';

export default [
    <Route exact path="/oauth2/redirect" component={OAuth2RedirectHandler}/>,
];
