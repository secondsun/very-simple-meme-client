/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.aerogear.verysimplememeclient.auth;

import android.app.Activity;

import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.authorization.oauth2.OAuthWebViewDialog;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.rest.multipart.MultipartRequestBuilder;
import org.jboss.aerogear.verysimplememeclient.vo.Meme;

import java.net.URL;

public class KeycloakHelper {

    private static final String SHOOT_SERVER_URL = "https://auth-coffeeregister.rhcloud.com";
    private static final String AUTHZ_URL = SHOOT_SERVER_URL +"/auth";
    private static final String AUTHZ_ENDPOINT = "/realms/memeolist/protocol/openid-connect/auth";
    private static final String ACCESS_TOKEN_ENDPOINT = "/realms/memeolist/protocol/openid-connect/token";
    private static final String REFRESH_TOKEN_ENDPOINT = "/realms/memeolist/protocol/openid-connect/token";
    private static final String AUTHZ_ACCOOUNT_ID = "keycloak-token";
    private static final String AUTHZ_CLIENT_ID = "memolist-android";
    private static final String AUTHZ_REDIRECT_URL = "http://oauth2callback";
    private static final String MODULE_NAME = "KeyCloakAuthz";

//    private static final String SHOOT_SERVER_URL = "https://auth-coffeeregister.rhcloud.com";
//    private static final String AUTHZ_URL = SHOOT_SERVER_URL +"/auth";
//    private static final String AUTHZ_ENDPOINT = "/realms/memeolist/tokens/login";
//    private static final String ACCESS_TOKEN_ENDPOINT = "/realms/memeolist/tokens/access/codes";
//    private static final String REFRESH_TOKEN_ENDPOINT = "/realms/memeolist/tokens/refresh";
//    private static final String AUTHZ_ACCOOUNT_ID = "keycloak-token";
//    private static final String AUTHZ_CLIENT_ID = "memolist-android";
//    private static final String AUTHZ_REDIRECT_URL = "http://oauth2callback";
//    private static final String MODULE_NAME = "KeyCloakAuthz";

    static {
        try {
            AuthorizationManager.config(MODULE_NAME, OAuth2AuthorizationConfiguration.class)
                    .setBaseURL(new URL(AUTHZ_URL))
                    .setAuthzEndpoint(AUTHZ_ENDPOINT)
                    .setAccessTokenEndpoint(ACCESS_TOKEN_ENDPOINT)
                    .setRefreshEndpoint(REFRESH_TOKEN_ENDPOINT)
                    .setAccountId(AUTHZ_ACCOOUNT_ID)
                    .setClientId(AUTHZ_CLIENT_ID)
                    .setRedirectURL(AUTHZ_REDIRECT_URL)
                    .asModule();

            PipeManager.config("kc-upload", RestfulPipeConfiguration.class).module(AuthorizationManager.getModule(MODULE_NAME))
                    .withUrl(new URL("http://192.168.11.160" + "/v1/api/meme"))
                    .requestBuilder(new MultipartRequestBuilder())
                    .forClass(Meme.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void connect(final Activity activity, final Callback callback) {
        try {
            final AuthzModule authzModule = AuthorizationManager.getModule(MODULE_NAME);

            authzModule.requestAccess(activity, new Callback<String>() {
                @Override
                public void onSuccess(String s) {
                    callback.onSuccess(s);
                }

                @Override
                public void onFailure(Exception e) {
                    if (!e.getMessage().matches(OAuthWebViewDialog.OAuthReceiver.DISMISS_ERROR)) {
                        authzModule.deleteAccount();
                    }
                    callback.onFailure(e);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(e);
        }
    }

    public static boolean isConnected() {
        return AuthorizationManager.getModule(MODULE_NAME).isAuthorized();
    }

}
