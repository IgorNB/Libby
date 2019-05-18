package com.lig.libby.security.oauth2;

import com.lig.libby.security.config.AppPropertiesConfig;
import com.lig.libby.security.exception.BadRequestException;
import com.lig.libby.security.jwt.TokenProviderService;
import com.lig.libby.security.util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private TokenProviderService tokenProviderService;

    private AppPropertiesConfig appPropertiesConfig;

    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;


    @Autowired
    OAuth2AuthenticationSuccessHandler(TokenProviderService tokenProviderService, AppPropertiesConfig appPropertiesConfig,
                                       HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.tokenProviderService = tokenProviderService;
        this.appPropertiesConfig = appPropertiesConfig;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @SuppressWarnings("squid:S1172")
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        boolean rememberMe = false;
        String token = tokenProviderService.createToken(authentication, rememberMe);


        UriComponents uriComponents = UriComponentsBuilder.fromUriString(targetUrl).build();

        //react-admin compatible putting uri fragment before query params
        if (uriComponents.getFragment() != null) {
            String fragmentWithQueryParams = UriComponentsBuilder
                    .fromPath(uriComponents.getFragment())
                    .queryParams(uriComponents.getQueryParams())
                    .queryParam("token", token)
                    .toUriString();

            return UriComponentsBuilder
                    .fromUri(uriComponents.toUri())
                    .replaceQueryParams(null)
                    .fragment(fragmentWithQueryParams)
                    .build().toUriString();
        }

        //default behaviour
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        if (appPropertiesConfig.getOauth2().getAuthorizedRedirectUris() == null || appPropertiesConfig.getOauth2().getAuthorizedRedirectUris().isEmpty()) {
            return true;
        } else {
            return appPropertiesConfig.getOauth2().getAuthorizedRedirectUris()
                    .stream()
                    .anyMatch(authorizedRedirectUri -> {
                        // Only validate host and port. Let the clients use different paths if they want to
                        URI authorizedURI = URI.create(authorizedRedirectUri);
                        return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                                && authorizedURI.getPort() == clientRedirectUri.getPort();
                    });
        }
    }
}
