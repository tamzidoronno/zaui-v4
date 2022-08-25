# Repository Layer of GetShop

## Technology Used
- Java 8
- Gradle
- Mongo Java Driver
- Morphia ODM

## Tests
Many of the tests are dependent on the actual Mongo Database. Followings are docker instructions
to instantiate a MongoDB server for the purpose of executing tests.

*Note:* 

- Recommended mongo version is `3.6.3`. This is the same version used in production.  
- By default, the tests will connect to mongodb port `27019`. This can be changed from test.properties file.  

```
    docker pull mongo:3.6.3 
    docker container run --name mongo-test --publish 27019:27017 mongo:3.6.3
```

- Run command for the repository's test suits

```
    ./gradlew :repository:check
```