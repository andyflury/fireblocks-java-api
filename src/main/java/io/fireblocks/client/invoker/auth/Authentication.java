package io.fireblocks.client.invoker.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

public interface Authentication {

    public void applyToParams(String path, Object body, MultiValueMap<String, String> queryParams, HttpHeaders headerParams);

}
