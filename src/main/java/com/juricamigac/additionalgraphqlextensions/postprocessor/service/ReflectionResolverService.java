package com.juricamigac.additionalgraphqlextensions.postprocessor.service;

import com.juricamigac.additionalgraphqlextensions.annotations.DataFetcherQL;
import com.juricamigac.additionalgraphqlextensions.annotations.GraphqlObject;
import com.juricamigac.additionalgraphqlextensions.annotations.RuntimeWiringQL;
import com.juricamigac.additionalgraphqlextensions.beans.DataFetcherBean;
import com.juricamigac.additionalgraphqlextensions.beans.GraphqlBean;
import com.juricamigac.additionalgraphqlextensions.beans.impl.GraphqlClasspathBeanImpl;
import com.juricamigac.additionalgraphqlextensions.beans.impl.GraphqlRawStringBeanImpl;
import com.juricamigac.additionalgraphqlextensions.enums.GraphqlObjectEnum;
import graphql.GraphQL;
import graphql.schema.idl.RuntimeWiring;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ReflectionResolverService {

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private GraphqlSchemaResolver graphqlSchemaResolver;

    private static final Reflections REFLECTIONS = new Reflections(ReflectionResolverService.class.getClassLoader(),
            new SubTypesScanner(),
            new TypeAnnotationsScanner(),
            new FieldAnnotationsScanner());

    public Set<Class<?>> getBeansWithDataFetcherAnnotations() {
        return REFLECTIONS.getTypesAnnotatedWith(DataFetcherQL.class);
    }

    public Set<DataFetcherBean> getDataFetcherBeans(Set<Class<?>> dataFetcherAnnotations) {
        return dataFetcherAnnotations.stream()
                .filter(Objects::nonNull)
                .filter(classType -> classType.isAnnotationPresent(DataFetcherQL.class))
                .map(classType -> createDataFetcherBean(classType, classType.getAnnotation(DataFetcherQL.class)))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Field> getFieldsAnnotatedWithRuntimeWiring() {
        return REFLECTIONS.getFieldsAnnotatedWith(RuntimeWiringQL.class);
    }

    public Set<Field> getFieldsAnnotatedWithGraphQlObject() {
        return REFLECTIONS.getFieldsAnnotatedWith(GraphqlObject.class);
    }

    public void setRuntimeWiringToAnnotationFields(final Set<Field> fields, final RuntimeWiring runtimeWiring) {
        fields.stream()
                .filter(field -> field.getAnnotatedType().getType().equals(RuntimeWiring.class))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        field.set(beanFactory.getBean(field.getDeclaringClass()), runtimeWiring);
                    } catch (IllegalAccessException e) {
                        log.error("Error during injection of Runtime wiring", e);
                    }
                });
    }

    public void setGraphQlObject(final Set<Field> fields, final RuntimeWiring runtimeWiring) {
        fields.forEach(field -> {
            try {
                field.setAccessible(true);
                Optional<GraphqlBean> graphqlBean = this.graphqlSchemaResolver.getGraphqlBean(field, runtimeWiring);
                if (graphqlBean.isPresent()) {
                    GraphqlBean bean = graphqlBean.get();
                    if (bean.getSchema().isPresent()) {
                        final GraphQL graphQL = GraphQL.newGraphQL(bean.getSchema().get()).build();
                        field.set(beanFactory.getBean(field.getDeclaringClass()), graphQL);
                    }
                }
            } catch (final IllegalAccessException e) {
                log.error("Error during injection of Runtime wiring", e);
            }
        });
    }

    private DataFetcherBean createDataFetcherBean(final Class<?> dataFetcherClass, final DataFetcherQL annotation) {
        return DataFetcherBean.builder()
                .dataFetcherClass(dataFetcherClass)
                .dataFetcherAnnotation(annotation)
                .build();
    }

}
