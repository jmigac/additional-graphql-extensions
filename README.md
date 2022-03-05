# Additional Graphql Extensions
Additional GraphQL Extension created through Java reflection to provide annotation-driven development by providing additional annotations for faster setup of GraphQL objects and easier way of managing DataFetcher objects inside RuntimeWiring object.

## How to add following project as dependency

```
<dependency>
    <groupId>com.juricamigac</groupId>
    <artifactId>additional-graphql-extensions</artifactId>
    <version>use the latest version</version>
</dependency>
```
### Additional dependencies which are required to use the following the extension
```
<dependency>
    <groupId>com.graphql-java</groupId>
    <artifactId>graphql-java</artifactId>
    <version>17.3</version>
</dependency>
<dependency>
    <groupId>net.oneandone.reflections8</groupId>
    <artifactId>reflections8</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```
## How to use the GraphQL Extension
1. Create Configuration class for scanning Graphql packages
```
@GraphqlConfiguration
public class AutoGraphqlConfiguration {}
```
2. Create service for enabling GraphQL objects
```
@GQL
public class GraphqlService {

    @Getter
    @GraphqlObject(schemaType = GraphqlObjectEnum.CLASSPATH, schemaValue = "graphql/shema.graphqls")
    private GraphQL graphQL;

    @Getter
    @RuntimeWiringQL
    private RuntimeWiring runtimeWiringQL;

}
```
3. Now since you have linked the GraphQL schema file to the creation of the GraphQL object, you simply need to define DataFetchers for each of the operations in the schema file.
Procedure to-do the following is:
   
```
@DataFetcherQL(operationName = "{NAME_OF_THE_GRAPHQL_OPERATION}")
public class AllEarthquakesDataFetcher implements DataFetcher<{RETURN_TYPE}> {

    @Override
    public {RETURN_TYPE} get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        
    }
}
```
4. Optional step: If your project is logging vast number of ReflectionExceptions, it's because the dependency have reported bug, and to disable this, just disable logging for following package
Inside the `application.properties` add the following line `logging.level.org.reflections8.*=ERROR` 