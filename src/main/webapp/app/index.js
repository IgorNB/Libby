/* eslint react/jsx-key: off */
import React from 'react';
import {Admin, fetchUtils, Resource} from 'react-admin'; // eslint-disable-line import/no-unresolved
import {render} from 'react-dom';
import {reducer as tree} from 'ra-tree-ui-materialui';

import authProvider from './authProvider/authProvider';
import {Layout, Login} from './layout';

import springRestProvider from './dataProvider';
import i18nProvider from '../i18n/i18nProvider';
import books from './screen/admin/books';
import tasks from './screen/admin/tasks';
import tasksPublic from './screen/public/tasksPublic';
import booksPublic from './screen/public/booksPublic';
import commentsPublic from './screen/public/commentsPublic';
import langs from './screen/admin/langs';
import users from './screen/admin/users';
import langsPublic from './screen/public/langsPublic';
import comments from './screen/admin/comments';
import customRoutes from './authProvider/customRoutes';
import {ACCESS_TOKEN, API_PROXY_URL, ROLE_ADMIN} from "./constants";
import OAuth2RedirectHandler from "./authProvider/OAuth2RedirectHandler";

const httpClient = (url, options = {}) => {
    if (localStorage.getItem(ACCESS_TOKEN)) {
        if (!options.headers) {
            options.headers = new Headers({Accept: 'application/json'});
        }
        // add your own headers here
        options.headers.set('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN));
    }
    return fetchUtils.fetchJson(url, options);
}

const springDataProvider = springRestProvider(API_PROXY_URL, httpClient);

render(
    <Admin
        authProvider={authProvider}
        dataProvider={springDataProvider}
        i18nProvider={i18nProvider}
        title="Example Admin"
        locale="ru"
        appLayout={Layout}
        loginPage={Login}
        customReducers={{tree}}
        customRoutes={customRoutes}
    >
        {permissions => [
            //пользовательский экран
            !OAuth2RedirectHandler.permissionsContainRole(permissions, ROLE_ADMIN) ?
                <Resource name="booksPublic" {...booksPublic} /> : <Resource name="booksPublic"/>,
            !OAuth2RedirectHandler.permissionsContainRole(permissions, ROLE_ADMIN) ?
                <Resource name="tasksPublic" {...tasksPublic} /> : <Resource name="tasksPublic"/>,
            !OAuth2RedirectHandler.permissionsContainRole(permissions, ROLE_ADMIN) ?
                <Resource name="commentsPublic" {...commentsPublic} /> : <Resource name="commentsPublic"/>,
            false ? <Resource name="langsPublic" {...langsPublic} /> : <Resource name="langsPublic"/>,

            //административный экран
            OAuth2RedirectHandler.permissionsContainRole(permissions, ROLE_ADMIN) ?
                <Resource name="books" {...books} /> : <Resource name="books"/>,
            OAuth2RedirectHandler.permissionsContainRole(permissions, ROLE_ADMIN) ?
                <Resource name="comments" {...comments} /> : <Resource name="comments"/>,
            OAuth2RedirectHandler.permissionsContainRole(permissions, ROLE_ADMIN) ?
                <Resource name="tasks" {...tasks} /> : <Resource name="tasks"/>,
            OAuth2RedirectHandler.permissionsContainRole(permissions, ROLE_ADMIN) ?
                <Resource name="langs" {...langs} /> : <Resource name="langs"/>,
            OAuth2RedirectHandler.permissionsContainRole(permissions, ROLE_ADMIN) ?
                <Resource name="users" {...users} /> : <Resource name="users"/>,


        ]}
    </Admin>,
    document.getElementById('root')
);
