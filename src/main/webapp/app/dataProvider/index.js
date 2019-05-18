import {
    CREATE,
    DELETE,
    DELETE_MANY,
    fetchUtils,
    GET_LIST,
    GET_MANY,
    GET_MANY_REFERENCE,
    GET_ONE,
    UPDATE,
    UPDATE_MANY
} from "react-admin";

/**
 * Maps react-admin queries to a REST API implemented using Java Spring Boot and Swagger
 *
 * @example
 * GET_LIST     => GET http://my.api.url/posts?pageNumber=0&pageSize=10
 * GET_ONE      => GET http://my.api.url/posts/123
 * GET_MANY     => GET http://my.api.url/posts?id=1234&id=5678
 * UPDATE       => PUT http://my.api.url/posts/123
 * CREATE       => POST http://my.api.url/posts
 * DELETE       => DELETE http://my.api.url/posts/123
 */
export default (apiUrl, httpClient = fetchUtils.fetchJson) => {
    /**
     * @param {String} type One of the constants appearing at the top if this file, e.g. 'UPDATE'
     * @param {String} resource Name of the resource to fetch, e.g. 'posts'
     * @param {Object} params The data request params, depending on the type
     * @returns {Object} { url, options } The HTTP request parameters
     */
    const convertDataRequestToHTTP = (type, resource, params) => {
        let url = "";
        const options = {};
        switch (type) {
            case GET_MANY_REFERENCE:
            case GET_LIST: {
                const {page, perPage} = params.pagination;
                const {field, order} = params.sort;
                const filterStr =
                    Object.keys(params.filter)
                        .map(k => "&" + encodeURIComponent(k) + "=" + encodeURIComponent(params.filter[k]))
                        .join("");

                let sortStr = params.hasOwnProperty("sort") ? `&sort=${field},${order}` : "";

                let manyReferenceFilter = params.hasOwnProperty("target") ? '&' + [params.target] + '=' + params.id : "";

                url = `${apiUrl}/${resource}?page=${page - 1}&size=${perPage}` + sortStr + filterStr + manyReferenceFilter;
                break;
            }
            case GET_ONE:
                url = `${apiUrl}/${resource}/${params.id}`;
                break;
            case GET_MANY: {
                const idStr = params.ids
                    .map(id => `id=${id}`)
                    .join("&");
                url = `${apiUrl}/${resource}?${idStr}`;
                break;
            }
            case UPDATE:
                url = `${apiUrl}/${resource}/${params.id}`;
                options.method = "PUT";
                options.body = JSON.stringify(params.data);
                break;
            case CREATE:
                url = `${apiUrl}/${resource}`;
                options.method = "POST";
                options.body = JSON.stringify(params.data);
                break;
            case DELETE:
                url = `${apiUrl}/${resource}/${params.id}`;
                options.method = "DELETE";
                break;
            default:
                throw new Error(`Unsupported fetch action type ${type}`);
        }
        return {url, options};
    };

    /**
     * @param {Object} response HTTP response from fetch()
     * @param {String} type One of the constants appearing at the top if this file, e.g. 'UPDATE'
     * @param {String} resource Name of the resource to fetch, e.g. 'posts'
     * @param {Object} params The data request params, depending on the type
     * @returns {Object} Data response
     */
    const convertHTTPResponse = (response, type, resource, params) => {
        const {json} = response;
        switch (type) {
            case GET_LIST:
            case GET_MANY:
            case GET_MANY_REFERENCE:
                if (!json.hasOwnProperty("totalElements")) {
                    throw new Error(
                        "The numberOfElements property must be must be present in the Json response"
                    );
                }
                return {
                    data: json.content,
                    total: parseInt(json.totalElements, 10)
                };
            case CREATE:
            case UPDATE:
            case GET_ONE:
                return {data: json};
            case DELETE:
                return {};
            default:
                throw new Error(`Response for this operation (${type}) is not implemented in dataProvider - check it, please.`);
        }
    };

    /**
     * @param {string} type Request type, e.g GET_LIST
     * @param {string} resource Resource name, e.g. "posts"
     * @param {Object} payload Request parameters. Depends on the request type
     * @returns {Promise} the Promise for a data response
     */
    return (type, resource, params) => {
        // simple-rest doesn't handle filters on UPDATE route, so we fallback to calling UPDATE n times instead
        if (type === UPDATE_MANY) {
            return Promise.all(
                params.ids.map(id =>
                    httpClient(`${apiUrl}/${resource}/${id}`, {
                        method: "PUT",
                        body: JSON.stringify(params.data)
                    })
                )
            ).then(responses => ({
                data: responses.map(response => response.json)
            }));
        }
        // simple-rest doesn't handle filters on DELETE route, so we fallback to calling DELETE n times instead
        if (type === DELETE_MANY) {
            return Promise.all(
                params.ids.map(id =>
                    httpClient(`${apiUrl}/${resource}/${id}`, {
                        method: "DELETE"
                    })
                )
            ).then(responses => ({
                data: responses.map(response => response.json)
            }));
        }

        const {url, options} = convertDataRequestToHTTP(type, resource, params);
        return httpClient(url, options).then(response =>
            convertHTTPResponse(response, type, resource, params)
        );
    };
};