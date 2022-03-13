package com.juricamigac.additionalgraphqlextensions.annotations;

import com.juricamigac.additionalgraphqlextensions.enums.DataFetcherOperationsEnum;
import org.apache.commons.lang3.StringUtils;
import org.atteo.classindex.IndexAnnotated;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation meant to be used on the classes / beans which will be implementing DataFetcher interface.
 */

@Component
@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DataFetcherQL {

    /**
     * Element which will represent if the DataFetcher will be Query or Mutation
     *
     * @return Type of GraphQL operation from which will be RuntimeWiring created
     */
    DataFetcherOperationsEnum operation() default DataFetcherOperationsEnum.QUERY;

    /**
     * Name of the GraphQL operation
     *
     * @return Name of the GraphQL operation
     */
    String operationName() default StringUtils.EMPTY;

}
