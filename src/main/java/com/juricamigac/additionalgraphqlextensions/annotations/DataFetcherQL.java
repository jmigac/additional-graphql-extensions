package com.juricamigac.additionalgraphqlextensions.annotations;

import com.juricamigac.additionalgraphqlextensions.enums.DataFetcherOperationsEnum;
import org.apache.commons.lang3.StringUtils;
import org.atteo.classindex.IndexAnnotated;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DataFetcherQL {

    DataFetcherOperationsEnum operation() default DataFetcherOperationsEnum.QUERY;
    String operationName() default StringUtils.EMPTY;

}
