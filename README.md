# Additional Graphql Extensions

### Project information
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/jmigac/additional-graphql-extensions/Java%20CI%20with%20Maven?style=for-the-badge)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/jmigac/additional-graphql-extensions?style=for-the-badge)
[![GitHub license](https://img.shields.io/github/license/jmigac/additional-graphql-extensions?style=for-the-badge)](https://github.com/jmigac/additional-graphql-extensions/blob/main/LICENSE)
![Lines of code](https://img.shields.io/tokei/lines/github/jmigac/additional-graphql-extensions?style=for-the-badge)


Additional GraphQL Extension created through Java reflection to provide annotation-driven development by providing additional annotations for faster setup of GraphQL objects and easier way of managing DataFetcher objects inside RuntimeWiring object.

## How to add following project as dependency

```
<dependency>
    <groupId>com.juricamigac</groupId>
    <artifactId>additional-graphql-extensions</artifactId>
    <version>use the latest version</version>
</dependency>
```
### How to add github credentials for public packages
In your `.m2` find `settings.xml`, and add following code blocks to it.

```
<server>
    <id>github</id>
    <username>{GITHUB_USERNAME}</username>
    <password>{GITHUB_PERSONAL_ACCESS_TOKEN}</password>
</server>
```
For **personal access token** access, only "read packages" is enough so that maven can resolve the dependency.

Under repositories add the following code block for repository specification
```
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/jmigac/additional-graphql-extensions</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
    <releases>
        <enabled>true</enabled>
  </releases>
</repository>
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

# Perfomance testing

Time for fetching all of the datafetcher objects, setting up the runtime wiring and setting the graphql object.

| Number of data fetchers | Time to setup DataFetchers  | Time to setup Runtime Wiring | Time to setup GraphQL |
| --- | --- | --- | --- |
| 1 | 37ms | 38ms | 163ms |
| 2 | 53ms | 54ms | 203ms |
| 5 | 42ms | 44ms | 160ms |
| 10 | 42ms | 43ms | 162ms |
| 15 | 41ms | 42ms | 161ms |
| 20 | 39ms | 40ms | 155ms |
| 30 | 49ms | 50ms | 179ms |

## Summary of test results
Following test results show the that the fetching all of the beans with annotation `@DataFetcherQL` takes about in average 40ms, no matter what is the number of them. Runtime Wiring setup about 1ms to make that object with linking all of the queries and data fetchers to one object.
GraphQL object takes in average more time, about 160ms, because in case using the schema file, it's needed to load a schema file from resource loader and adapt runtime wiring with loaded file.