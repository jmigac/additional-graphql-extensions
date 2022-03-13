package com.juricamigac.additionalgraphqlextensions.postprocessor;

import com.juricamigac.additionalgraphqlextensions.beans.DataFetcherBean;
import com.juricamigac.additionalgraphqlextensions.postprocessor.service.ReflectionResolverService;
import graphql.schema.DataFetcher;
import graphql.schema.idl.RuntimeWiring;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class DataFetcherPostProcessor {

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private ReflectionResolverService reflectionResolverService;

    @PostConstruct
    private void init() {
        try {
            RuntimeWiring RUNTIME_WIRING = RuntimeWiring.MOCKED_WIRING;
            final Set<Class<?>> dataFetcherAnnotation = this.reflectionResolverService.getBeansWithDataFetcherAnnotations();
            final Set<DataFetcherBean> dataFetcherBeans = this.reflectionResolverService.getDataFetcherBeans(dataFetcherAnnotation);
            final Set<Field> runtimeWiringFields = this.reflectionResolverService.getFieldsAnnotatedWithRuntimeWiring();
            final Set<Field> graphQlObjects = this.reflectionResolverService.getFieldsAnnotatedWithGraphQlObject();
            if (!dataFetcherBeans.isEmpty()) {
                final RuntimeWiring.Builder runtimeBuilder = RuntimeWiring.newRuntimeWiring();
                for (DataFetcherBean bean : dataFetcherBeans) {
                    final Optional<DataFetcher> dataFetcher = this.getRawDataFetcherBean(bean);
                    dataFetcher.ifPresent(dtBean -> runtimeBuilder.type(this.getCapitalizedGraphQLOperation(bean), b -> b.dataFetcher(bean.getDataFetcherAnnotation().operationName(), dtBean)));
                }
                RUNTIME_WIRING = runtimeBuilder.build();
                this.reflectionResolverService.setRuntimeWiringToAnnotationFields(runtimeWiringFields, RUNTIME_WIRING);
                this.reflectionResolverService.setGraphQlObject(graphQlObjects, RUNTIME_WIRING);
            }
        } catch (final Exception e){
            log.error("Error in post construct", e);
        }
    }

    private String getCapitalizedGraphQLOperation(final DataFetcherBean bean) {
        return StringUtils.capitalize(bean.getDataFetcherAnnotation().operation().toString().toLowerCase());
    }

    private Optional<DataFetcher> getRawDataFetcherBean(final DataFetcherBean bean) {
        try{
            final DataFetcher dataFetcher = ((DataFetcher) beanFactory.getBean(bean.getDataFetcherClass()));
            return Optional.of(dataFetcher);
        } catch (final BeansException e) {
            log.error("Error during fetching the bean", e);
        }
        return Optional.empty();
    }

}
