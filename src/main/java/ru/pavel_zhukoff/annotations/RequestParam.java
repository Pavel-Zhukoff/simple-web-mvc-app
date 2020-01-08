package ru.pavel_zhukoff.annotations;

import ru.pavel_zhukoff.enums.RequestType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestParam {
    String uri() default "";
    RequestType requsetType() default RequestType.GET;
}
