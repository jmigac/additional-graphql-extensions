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

import java.util.Optional;

@Data
@Builder
public class GraphqlRawStringBeanImpl implements GraphqlBean {

    private GraphqlObject annotation;
    private String rawSchema;
    private RuntimeWiring runtimeWiring;

    @Override
    public GraphqlObject getAnnotation() {
        return this.annotation;
    }

    @Override
    public Optional<GraphQLSchema> getSchema() {
        final TypeDefinitionRegistry registar = new SchemaParser().parse(rawSchema);
        return Optional.of(new SchemaGenerator().makeExecutableSchema(registar, runtimeWiring));
    }
}
