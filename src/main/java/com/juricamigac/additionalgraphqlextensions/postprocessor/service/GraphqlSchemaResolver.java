package com.juricamigac.additionalgraphqlextensions.postprocessor.service;

import com.juricamigac.additionalgraphqlextensions.annotations.GraphqlObject;
import com.juricamigac.additionalgraphqlextensions.beans.GraphqlBean;
import com.juricamigac.additionalgraphqlextensions.beans.impl.GraphqlClasspathBeanImpl;
import com.juricamigac.additionalgraphqlextensions.beans.impl.GraphqlRawStringBeanImpl;
import com.juricamigac.additionalgraphqlextensions.enums.GraphqlObjectEnum;
import graphql.schema.idl.RuntimeWiring;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Optional;

@Slf4j
@Service
public class GraphqlSchemaResolver {

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Fetch created optional GraphQL bean which contains information about Graphql schema type
     *
     * @param field         Field which containts GraphqlObject annotation
     * @param runtimeWiring Value which will later on be used for setting up RuntimeWiring object and GraphQL object in the end
     * @return Optional GraphqlBean
     */
    public Optional<GraphqlBean> getGraphqlBean(@NonNull final Field field, @NonNull final RuntimeWiring runtimeWiring) {
        final GraphqlObject annotation = field.getAnnotation(GraphqlObject.class);
        if (annotation.schemaType() == GraphqlObjectEnum.RAW_STRING) {
            return this.getRawStringSchemaBean(annotation, runtimeWiring, field);
        } else if (annotation.schemaType() == GraphqlObjectEnum.CLASSPATH) {
            return this.getClassPathSchemaBean(annotation, runtimeWiring, field);
        }
        return Optional.empty();
    }

    private Optional<GraphqlBean> getRawStringSchemaBean(final GraphqlObject annotation, final RuntimeWiring runtimeWiring, final Field field) {
        return Optional.of(GraphqlRawStringBeanImpl.builder()
                .runtimeWiring(runtimeWiring)
                .annotation(annotation)
                .rawSchema(getSchema(field))
                .build());
    }

    private Optional<GraphqlBean> getClassPathSchemaBean(final GraphqlObject annotation, final RuntimeWiring runtimeWiring, final Field field) {
        return Optional.of(GraphqlClasspathBeanImpl.builder()
                .runtimeWiring(runtimeWiring)
                .annotation(annotation)
                .schemaFile(getSchemaFile(field))
                .build());
    }

    private Optional<File> getSchemaFile(final Field field) {
        try {
            final GraphqlObject annotation = field.getAnnotation(GraphqlObject.class);
            if (annotation.schemaType() == GraphqlObjectEnum.CLASSPATH) {
                final String resourcePath = annotation.schemaValue();
                final String schemaPath = StringUtils.join(ResourceLoader.CLASSPATH_URL_PREFIX, resourcePath);
                return Optional.of(resourceLoader.getResource(schemaPath).getFile());
            }
        } catch (final IOException e) {
            log.error("Error during fetch of the file for the shema path", e);
        }
        return Optional.empty();
    }

    private String getSchema(final Field field) {
        final GraphqlObject annotation = field.getAnnotation(GraphqlObject.class);
        if (annotation.schemaType() == GraphqlObjectEnum.RAW_STRING && StringUtils.isNotEmpty(annotation.schemaValue())) {
            return annotation.schemaValue();
        }
        return StringUtils.EMPTY;
    }

}
