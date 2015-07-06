/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.aerogear.verysimplememeclient.auth;

import android.app.Activity;
import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.authorization.oauth2.OAuthWebViewDialog;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.rest.gson.GsonResponseParser;
import org.jboss.aerogear.android.pipe.rest.multipart.MultipartRequestBuilder;
import org.jboss.aerogear.verysimplememeclient.vo.Meme;
import org.jboss.aerogear.verysimplememeclient.vo.Post;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KeycloakHelper {

    private static final String SHOOT_SERVER_URL = "http://auth.com:9090";
    private static final String AUTHZ_URL = SHOOT_SERVER_URL + "/auth";
    private static final String AUTHZ_ENDPOINT = "/realms/memeolist/protocol/openid-connect/auth";
    private static final String ACCESS_TOKEN_ENDPOINT = "/realms/memeolist/protocol/openid-connect/token";
    private static final String REFRESH_TOKEN_ENDPOINT = "/realms/memeolist/protocol/openid-connect/token";
    private static final String AUTHZ_ACCOOUNT_ID = "keycloak-token";
    private static final String AUTHZ_CLIENT_ID = "memolist-android";
    private static final String AUTHZ_REDIRECT_URL = "http://oauth2callback";
    private static final String MODULE_NAME = "KeyCloakAuthz";

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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

            PipeManager.config("kc-post", RestfulPipeConfiguration.class).module(AuthorizationManager.getModule(MODULE_NAME))
                    .withUrl(new URL("http://10.0.2.2:8080" + "/memeolist/v1/api/post")).responseParser(new GsonResponseParser(new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    try {
                        return FORMAT.parse(json.getAsString());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                @Override
                public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                        context) {
                    return src == null ? null : new JsonPrimitive(src.getTime());
                }
            }).create())).forClass(Post.class);

            PipeManager.config("kc-upload", RestfulPipeConfiguration.class).module(AuthorizationManager.getModule(MODULE_NAME))
                    .withUrl(new URL("http://10.0.2.2:8080" + "/memeolist/v1/api/meme"))
                    .requestBuilder(new MultipartRequestBuilder())
                    .responseParser(new GsonResponseParser(new GsonBuilder().registerTypeAdapter(byte[].class, new TypeAdapter() {
                        @Override
                        public void write(JsonWriter out, Object value) throws IOException {
                            byte[] valueArray = (byte[]) value;
                            out.beginArray();
                            for (byte b : valueArray) {
                                out.value(b);
                            }
                            out.endArray();
                        }

                        @Override
                        public Object read(JsonReader in) throws IOException {

                            return Base64.decode(in.nextString().toString(), Base64.CRLF);
                        }
                    }).registerTypeAdapter(Date.class, new JsonDeserializer() {
                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            return new Date(json.getAsJsonPrimitive().getAsLong());
                        }
                    }).registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                        @Override
                        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                                context) {
                            return src == null ? null : new JsonPrimitive(src.getTime());
                        }
                    }).create()))
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
