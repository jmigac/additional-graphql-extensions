package com.juricamigac.additionalgraphqlextensions.beans;

import com.juricamigac.additionalgraphqlextensions.annotations.DataFetcherQL;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataFetcherBean {

    private Class<?> dataFetcherClass;
    private DataFetcherQL dataFetcherAnnotation;

}
