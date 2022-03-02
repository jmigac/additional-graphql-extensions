package com.juricamigac.additionalgraphqlextensions.beans;

import com.juricamigac.additionalgraphqlextensions.annotations.GraphqlObject;
import graphql.schema.GraphQLSchema;

import java.util.Optional;

public interface GraphqlBean {

    GraphqlObject getAnnotation();
    Optional<GraphQLSchema> getSchema();

}
