# WMockServer

#### MockServer for automation testing (Android/iOS/Web)

- **build** `./gradlew test shadowJar`
- **run** `java -jar build/libs/wmockserver-*-all.jar`
- **check** http://0.0.0.0:8080/healthCheck
- **enjoy**

#### How to add base mock?

1. Open `src/main/resources/mocks/__files/`
2. Create directory for your base mock. For example `v1/welcome.json` it must match with your endpoint
3. Create your mock. File name should contain main logic of your mock
4. Open `src/main/resources/mocks/mappings`
5. Create directory for matching your base mock. For example `v1_welcome.json` it must match with your endpoint
6. Create your matching. File name should contain main logic of your mock. File must contain: request and response. In
   response you must add path to your mock. For example:

```json
{
   "request": {
      "urlPathPattern": "/v1/welcome",
      "method": "GET"
   },
   "response": {
      "status": 200,
      "bodyFileName": "v1/welcome.json",
      "headers": {
      }
   }
}
```
