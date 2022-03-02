package com.juricamigac.additionalgraphqlextensions.annotations;

import com.juricamigac.additionalgraphqlextensions.enums.GraphqlObjectEnum;
import org.apache.commons.lang3.StringUtils;
import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GraphqlObject {

    GraphqlObjectEnum schemaType() default GraphqlObjectEnum.CLASSPATH;
    String schemaValue() default StringUtils.EMPTY;

}
