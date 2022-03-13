package com.juricamigac.additionalgraphqlextensions.annotations;

import org.atteo.classindex.IndexAnnotated;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to annotate classes which will be used as a service and to be injected into other classes.
 * Following classes will contain GraphQL object and / or RuntimeWiring
 */

@Component
@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GQL {
}
