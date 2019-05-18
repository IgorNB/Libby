import React, {Component} from 'react';
import {ACCESS_TOKEN, TOKEN_ROLES_ATTRIBUTE} from "../constants";
import {Redirect} from 'react-router-dom'

class OAuth2RedirectHandler extends Component {
    static getUrlParameter(name, locationSearch) {
        name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
        const regex = new RegExp('[\\?&]' + name + '=([^&#]*)');

        const results = regex.exec(locationSearch);
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    };

    static getRolesFromToken(token) {
        return JSON.parse(atob(token.split(".")[1].replace(/-/g, '+').replace(/_/g, '/')))[TOKEN_ROLES_ATTRIBUTE].split(",");
    };

    static permissionsContainRole(permissions, role) {
        if (permissions) {
            return permissions.split(",").indexOf(role) > -1;
        } else {
            return false;
        }

    };

    render() {
        const token = OAuth2RedirectHandler.getUrlParameter('token', this.props.location.search);
        const error = OAuth2RedirectHandler.getUrlParameter('error', this.props.location.search);

        if (token) {
            localStorage.setItem(ACCESS_TOKEN, token);
            localStorage.setItem(TOKEN_ROLES_ATTRIBUTE, OAuth2RedirectHandler.getRolesFromToken(token))
            return <Redirect to={{
                pathname: "/",
                state: {from: this.props.location}
            }}/>;
        } else {
            return <Redirect to={{
                pathname: "/login",
                state: {
                    from: this.props.location,
                    error: error
                }
            }}/>;
        }
    }
}

export default OAuth2RedirectHandler;