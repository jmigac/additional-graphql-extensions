package com.juricamigac.additionalgraphqlextensions.annotations;

import com.juricamigac.additionalgraphqlextensions.enums.GraphqlObjectEnum;
import org.apache.commons.lang3.StringUtils;
import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Field annotation for annotating GraphqlObject's
 */

@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GraphqlObject {

    /**
     * Annotation argument for defining if the GraphQL schema will be loaded from the classpath or as a RAW string
     *
     * @return Schema type
     */
    GraphqlObjectEnum schemaType() default GraphqlObjectEnum.CLASSPATH;

    /**
     * Annotation argument which represents value of the schema file
     *
     * @return Schema
     */
    String schemaValue() default StringUtils.EMPTY;

}
