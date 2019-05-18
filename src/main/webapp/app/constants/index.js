export const API_PROXY_PREFIX = '/libby/api';
export const FRONTEND_BASE_URL = 'http://localhost:8080';
export const ACCESS_TOKEN = 'accessToken';
export const TOKEN_ROLES_ATTRIBUTE = 'auth';
export const ROLE_ADMIN = 'ROLE_ADMIN';
export const ROLE_USER = 'ROLE_USER';

export const API_PROXY_URL = FRONTEND_BASE_URL + API_PROXY_PREFIX;
export const OAUTH2_REDIRECT_URI = encodeURIComponent(FRONTEND_BASE_URL + '/#/oauth2/redirect');
export const FORM_AUTH_URI = API_PROXY_URL + '/auth/login';
export const GOOGLE_AUTH_URL = API_PROXY_URL + '/oauth2/authorize/google?redirect_uri=' + OAUTH2_REDIRECT_URI;