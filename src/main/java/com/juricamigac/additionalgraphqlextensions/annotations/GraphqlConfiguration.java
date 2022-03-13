package com.juricamigac.additionalgraphqlextensions.annotations;

import org.atteo.classindex.IndexAnnotated;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to annotated configuration class for GraphQL to enable component scanning of the injected dependency
 */

@Configuration
@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ComponentScan(basePackages = "com.juricamigac.additionalgraphqlextensions.postprocessor")
public @interface GraphqlConfiguration {}
