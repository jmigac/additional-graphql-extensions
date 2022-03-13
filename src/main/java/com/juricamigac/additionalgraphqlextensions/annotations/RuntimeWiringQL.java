package com.juricamigac.additionalgraphqlextensions.annotations;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to annotate RuntimeWiring fields
 */

@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RuntimeWiringQL {
}
