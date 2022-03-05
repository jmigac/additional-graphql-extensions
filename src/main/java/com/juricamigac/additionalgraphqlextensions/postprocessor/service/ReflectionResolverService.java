package com.juricamigac.additionalgraphqlextensions.postprocessor.service;

import com.juricamigac.additionalgraphqlextensions.annotations.DataFetcherQL;
import com.juricamigac.additionalgraphqlextensions.annotations.GQL;
import com.juricamigac.additionalgraphqlextensions.annotations.GraphqlObject;
import com.juricamigac.additionalgraphqlextensions.annotations.RuntimeWiringQL;
import com.juricamigac.additionalgraphqlextensions.beans.DataFetcherBean;
import com.juricamigac.additionalgraphqlextensions.beans.GraphqlBean;
import graphql.GraphQL;
import graphql.schema.idl.RuntimeWiring;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;
import org.reflections8.scanners.FieldAnnotationsScanner;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
    private GraphqlSchemaResolver graphqlSchemaResolver;

    private static Reflections REFLECTIONS = null;

    @PostConstruct
    private void init() {
        try {
            REFLECTIONS = new Reflections(Thread.currentThread().getContextClassLoader(),
                    new FieldAnnotationsScanner(),
                    new TypeAnnotationsScanner(),
                    new SubTypesScanner());
        } catch (final Exception e) {
            log.error("Error in post construct", e);
        }
    }

    public Set<Class<?>> getBeansWithDataFetcherAnnotations() {
        return REFLECTIONS.getTypesAnnotatedWith(DataFetcherQL.class);
    }

    public Set<Class<?>> getBeansWithGQLAnnotation() {
        return REFLECTIONS.getTypesAnnotatedWith(GQL.class);
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
