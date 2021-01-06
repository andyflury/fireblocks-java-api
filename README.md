# Fireblocks Java API

This is a Java API for the [Fireblocks API](https://docs.fireblocks.com/api/#introduction) 

# Build Process

To build the Fireblocks Java API using maven run the following command:

```
mvn clean install
```

# Usage

To use it add the following Maven dependency

```
<dependency>
  <groupId>io.fireblocks</groupId>
  <artifactId>fireblocks-java-api</artifactId>
  <version>1.0</version>	
</dependency>	 
```


Then use it as follows:

```
DefaultApi api = new DefaultApi();
ApiClient apiClient = api.getApiClient();
apiClient.setApiKey("xxxxx-xxxxx-xxxxx-xxxxx");
apiClient.initPrivateKey(new File("private.key"));

List<VaultAccount> vaultAccountsGet = api.vaultAccountsGet();

VaultAccountCreateRequest request = new VaultAccountCreateRequest();
request.setName("Test2");
VaultAccount vaultAccount = api.vaultAccountsPost(request);
```


# Implementation Details
The Fireblocks Java API uses [Swagger Codegen Maven Plugin](https://github.com/swagger-api/swagger-codegen/tree/3.0.0/modules/swagger-codegen-maven-plugin). An alternative would have been to use the [OpenAPI Generator](https://openapi-generator.tech/docs/plugins) instead which is a fork of the Swagger Codegen Maven Plugin. The code produced by the Swagger Codegen Maven Plugin however seemed cleaner.
Unfortunately the [Fireblocks Swagger UI](https://docs.fireblocks.com/api/swagger-ui/) does not directly expose the Swagger.json file. Luckily the Swagger.json can be retrieved from [swagger-ui-init.js](https://docs.fireblocks.com/api/swagger-ui/swagger-ui-init.js) by extracting the contents of the "options" variable:

```
var options = {
 "swaggerDoc": {
   "openapi": "3.0.0",
   .....
```


The Fireblocks Swagger.json definition uses inline schemas for POST and PUT requests. Because of that the Swagger Codegen Maven Plugin does not know how to name generated Java Pojos and instead uses class names like Block.class, Block1.class. To overcome this "title" attributes have been added to inline schemas, e.g.:

```
"application/json": {
  "schema": {
    "title": "VaultAccountCreateRequest",
    "properties": {
      "name": {
        "description": "Account Name",
        "type":
```

The Fireblocks Swagger.json uses "oneOf" constructs to allow amounts to be provided as eithers number or string. 

```
"amount": {
  "oneOf": [
    {
      "type": "number"
    },
    {
      "type": "string"
    }
  ]
}
```

The Swagger Codegen Maven Plugin unfortunately creates empty interfaces for those. These "oneOf" constracts have been replaced with simple "number" definitions, which will generate BigDecimals in the generated PoJo's.

```
"amount": {
  "type": "number"
}
```

## JWT Token

Fireblocks requires JWT to sign requests, see: https://docs.fireblocks.com/api/#signing-a-request
Unfortunately the Swagger Codgen Maven Plugin does not support this directly and instead the class "io.fireblocks.client.invoker.auth.HttpBearerAuth" had to be modified to sign requests
