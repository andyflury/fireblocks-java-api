package io.fireblocks.client.invoker.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.time.DateUtils;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2021-01-03T11:00:41.617883+01:00[Europe/Berlin]")
public class HttpBearerAuth implements Authentication {

    private Algorithm algorithm;
    private String apiKey;
    private MessageDigest digest;
    private ObjectMapper mapper;

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setPrivateKey(File privateKeyFile) throws Exception {
        PrivateKey privateKey = PemUtils.readPrivateKeyFromFile(privateKeyFile, "RSA");
        algorithm = Algorithm.RSA256(null, (RSAPrivateKey) privateKey);
        digest = MessageDigest.getInstance("SHA-256");
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
    }

    @Override
    public void applyToParams(String path, Object body, MultiValueMap<String, String> queryParams, HttpHeaders headerParams) {


        try {
            String bodyString = mapper.writeValueAsString(body);
            byte[] hash = digest.digest(bodyString.getBytes(StandardCharsets.UTF_8));
            String bodyHash = new String(Hex.encode(hash));

            Date now = new Date();
            String token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("uri", "/v1" + path)
                    .withClaim("nonce", now.getTime())
                    .withIssuedAt(now)
                    .withExpiresAt(DateUtils.addSeconds(now, 10))
                    .withSubject(apiKey)
                    .withClaim("bodyHash", bodyHash)
                    .sign(algorithm);

            headerParams.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
