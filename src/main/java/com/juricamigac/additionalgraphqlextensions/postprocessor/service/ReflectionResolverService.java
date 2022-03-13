package com.juricamigac.additionalgraphqlextensions.postprocessor.service;

import com.juricamigac.additionalgraphqlextensions.annotations.DataFetcherQL;
import com.juricamigac.additionalgraphqlextensions.annotations.GQL;
import com.juricamigac.additionalgraphqlextensions.annotations.GraphqlObject;
import com.juricamigac.additionalgraphqlextensions.annotations.RuntimeWiringQL;
import com.juricamigac.additionalgraphqlextensions.beans.DataFetcherBean;
import com.juricamigac.additionalgraphqlextensions.beans.GraphqlBean;
import graphql.GraphQL;
import graphql.schema.idl.RuntimeWiring;
import lombok.NonNull;
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
import java.util.Collections;
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

    private static Optional<Reflections> REFLECTIONS = Optional.empty();

    @PostConstruct
    private void init() {
        try {
            REFLECTIONS = Optional.of(new Reflections(Thread.currentThread().getContextClassLoader(),
                    new FieldAnnotationsScanner(),
                    new TypeAnnotationsScanner(),
                    new SubTypesScanner()));
        } catch (final Exception e) {
            log.error("Error in post construct", e);
        }
    }

    /**
     * Filters the set of classes by DataFetcherQL annotation and created a set of DataFetcherBeans
     *
     * @param dataFetcherAnnotations Set of DataFetcherQL annotations
     * @return Set of DataFetcherBeans
     */
    public Set<DataFetcherBean> getDataFetcherBeans(@NonNull Set<Class<?>> dataFetcherAnnotations) {
        return dataFetcherAnnotations.stream()
                .filter(Objects::nonNull)
                .filter(classType -> classType.isAnnotationPresent(DataFetcherQL.class))
                .map(classType -> createDataFetcherBean(classType, classType.getAnnotation(DataFetcherQL.class)))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Get set of classes from reflection which are annotated with DataFetcherQL
     *
     * @return Set of classes
     */
    public Set<Class<?>> getBeansWithDataFetcherAnnotations() {
        return REFLECTIONS.isPresent()
                ? REFLECTIONS.get().getTypesAnnotatedWith(DataFetcherQL.class)
                : Collections.emptySet();
    }

    /**
     * Get set of classes from reflection which are annotated with GQL
     *
     * @return Set of classes
     */
    public Set<Class<?>> getBeansWithGQLAnnotation() {
        return REFLECTIONS.isPresent()
                ? REFLECTIONS.get().getTypesAnnotatedWith(GQL.class)
                : Collections.emptySet();
    }

    /**
     * Get set of fields which are annotated with RuntimeWiringQL
     *
     * @return Set of classes
     */
    public Set<Field> getFieldsAnnotatedWithRuntimeWiring() {
        return REFLECTIONS.isPresent()
                ? REFLECTIONS.get().getFieldsAnnotatedWith(RuntimeWiringQL.class)
                : Collections.emptySet();
    }

    /**
     * Get set of fields annotated with GraphqlObject
     *
     * @return Set of Fields
     */
    public Set<Field> getFieldsAnnotatedWithGraphQlObject() {
        return REFLECTIONS.isPresent()
                ? REFLECTIONS.get().getFieldsAnnotatedWith(GraphqlObject.class)
                : Collections.emptySet();
    }

    /**
     * Sets RuntimeWiring to parsed fields which need to contain that value
     *
     * @param fields        Set of RuntimeWiringQL annotated fields
     * @param runtimeWiring Object of type RuntimeWiring
     */
    public void setRuntimeWiringToAnnotationFields(@NonNull final Set<Field> fields, @NonNull final RuntimeWiring runtimeWiring) {
        fields.stream()
                .filter(field -> field.getAnnotatedType().getType().equals(RuntimeWiring.class))
                .forEach(field -> this.setRuntimeWiringFieldValue(field, runtimeWiring));
    }

    /**
     * Function to set a each field which is GraphQL object with runtime wiring values into GraphQL object ready to be queried
     *
     * @param fields        Set of fields annotated with GraphqlObject
     * @param runtimeWiring Object of type RuntimeWiring
     */
    public void setGraphQlObject(@NonNull final Set<Field> fields, @NonNull final RuntimeWiring runtimeWiring) {
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

    private void setRuntimeWiringFieldValue(final Field field, final RuntimeWiring runtimeWiring) {
        try {
            field.setAccessible(true);
            field.set(beanFactory.getBean(field.getDeclaringClass()), runtimeWiring);
        } catch (IllegalAccessException e) {
            log.error("Error during injection of Runtime wiring", e);
        }
    }

    private DataFetcherBean createDataFetcherBean(final Class<?> dataFetcherClass, final DataFetcherQL annotation) {
        return DataFetcherBean.builder()
                .dataFetcherClass(dataFetcherClass)
                .dataFetcherAnnotation(annotation)
                .build();
    }

}
