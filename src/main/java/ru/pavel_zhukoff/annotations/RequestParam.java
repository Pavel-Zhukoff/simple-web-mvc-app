package ru.pavel_zhukoff.annotations;

import ru.pavel_zhukoff.enums.RequestType;

public @interface RequestParam {
    String uri() default "";
    RequestType requsetType() default RequestType.GET;
}
