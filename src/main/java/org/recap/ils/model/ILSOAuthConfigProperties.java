
package org.recap.ils.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ILSOAuthConfigProperties extends ILSConfigProperties {
    @SerializedName("ils.rest.data.api")
    @Expose
    public String ilsRestDataApi;
    @SerializedName("ils.rest.polling.max.timeout")
    @Expose
    public String ilsRestPollingMaxTimeout;
    @SerializedName("oauth2.client.clientid")
    @Expose
    public String oauth2ClientClientid;
    @SerializedName("oauth2.client.clientSecret")
    @Expose
    public String oauth2ClientClientSecret;
    @SerializedName("oauth2.client.registered-redirect-uri")
    @Expose
    public String oauth2ClientRegisteredRedirectUri;
    @SerializedName("oauth2.resource.jwt.key-value")
    @Expose
    public String oauth2ResourceJwtKeyValue;
    @SerializedName("oauth2.client.scope")
    @Expose
    public String oauth2ClientScope;
    @SerializedName("oauth2.client.pre-established-redirect-uri")
    @Expose
    public String oauth2ClientPreEstablishedRedirectUri;
    @SerializedName("oauth.resource.jwt.key-value")
    @Expose
    public String oauthResourceJwtKeyValue;
    @SerializedName("oauth.token.api.url")
    @Expose
    public String oauthTokenApiUrl;
}