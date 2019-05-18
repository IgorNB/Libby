import {AUTH_CHECK, AUTH_ERROR, AUTH_GET_PERMISSIONS, AUTH_LOGIN, AUTH_LOGOUT,} from 'react-admin'; // eslint-disable-line import/no-unresolved
// eslint-disable-line import/no-unresolved
import {ACCESS_TOKEN, FORM_AUTH_URI, TOKEN_ROLES_ATTRIBUTE} from '../constants';
import OAuth2RedirectHandler from "./OAuth2RedirectHandler";
// Authenticatd by default
export default (type, params) => {
    if (type === AUTH_LOGIN) {
        const {username, password} = params;
        const email = username;
        const request = new Request(FORM_AUTH_URI, {
            method: 'POST',
            body: JSON.stringify({email, password}),
            headers: new Headers({'Content-Type': 'application/json'}),
        })
        return fetch(request)
            .then(response => {
                if (response.status < 200 || response.status >= 300) {
                    throw new Error(response.statusText);
                }
                return response.json();
            })
            .then(res => {
                const token = res[ACCESS_TOKEN];
                localStorage.setItem(ACCESS_TOKEN, token);
                localStorage.setItem(TOKEN_ROLES_ATTRIBUTE, OAuth2RedirectHandler.getRolesFromToken(token))
            });
    }
    if (type === AUTH_LOGOUT) {
        localStorage.setItem(ACCESS_TOKEN, null);
        localStorage.removeItem(TOKEN_ROLES_ATTRIBUTE);
        return Promise.resolve();
    }
    if (type === AUTH_ERROR) {
        const {status} = params;
        return status === 401 || status === 403
            ? Promise.reject()
            : Promise.resolve();
    }
    if (type === AUTH_CHECK) {
        return localStorage.getItem(ACCESS_TOKEN)
            ? Promise.resolve()
            : Promise.reject();
    }
    if (type === AUTH_GET_PERMISSIONS) {
        const roles = localStorage.getItem(TOKEN_ROLES_ATTRIBUTE);
        return roles ? Promise.resolve(roles) : Promise.resolve();
    }

    return Promise.reject('Unknown method');
};
