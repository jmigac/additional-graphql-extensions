package com.juricamigac.additionalgraphqlextensions.beans.impl;

import com.juricamigac.additionalgraphqlextensions.annotations.GraphqlObject;
import com.juricamigac.additionalgraphqlextensions.beans.GraphqlBean;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.Optional;

@Data
@Builder
public class GraphqlClasspathBeanImpl implements GraphqlBean{

    private GraphqlObject annotation;
    private Optional<File> schemaFile;
    private RuntimeWiring runtimeWiring;

    @Override
    public GraphqlObject getAnnotation() {
        return annotation;
    }

    @Override
    public Optional<GraphQLSchema> getSchema() {
        if (schemaFile.isPresent()){
            final TypeDefinitionRegistry registar = new SchemaParser().parse(this.schemaFile.get());
            return Optional.of(new SchemaGenerator().makeExecutableSchema(registar, runtimeWiring));
        }
        return Optional.empty();
    }
}
