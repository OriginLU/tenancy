package com.zeroone.tenancy.utils;

import com.google.common.collect.Maps;
import lombok.Generated;
import org.apache.commons.lang3.ObjectUtils;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ResourceUtils {


    private ResourceUtils() {
    }


    public static Set<Class<?>> getClasses(String packageName, Class<? extends Annotation> annotationClass) {

        // 初始化工具类
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(packageName).addScanners(new SubTypesScanner()).addScanners(new FieldAnnotationsScanner()));

        // 获取某个包下类型注解对应的类
        return reflections.getTypesAnnotatedWith(annotationClass, true);
    }

}
