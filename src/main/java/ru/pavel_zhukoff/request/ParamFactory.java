package ru.pavel_zhukoff.request;

import ru.pavel_zhukoff.annotations.RequestParam;
import ru.pavel_zhukoff.annotations.form.FormItem;
import ru.pavel_zhukoff.annotations.form.NotNull;
import ru.pavel_zhukoff.exceptions.NoSuchNotNullParameter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamFactory {

    private Method method;
    private Map<String, Object> params;
    private Map<String, Class<?>> classes;

    public ParamFactory(Method method, Map<String, Object> params) {
        this.method = method;
        this.params = params;
        classes = new HashMap<>();
    }

    // TODO: Сделать  фабрику объектов-форм

    public Object[] getParsedArgs() {
        List<Object> args = new ArrayList<>(method.getParameterCount());
        getMethodArgs();
        System.out.println(classes);
        for (Map.Entry<String, Class<?>> entry: classes.entrySet()) {
            System.out.println(entry.getKey());
            try {
                args.add(buildArgumentObject(entry.getValue()));
            } catch (NoSuchMethodException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchNotNullParameter
                    | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return args.toArray();
    }

    // НЕ ПАШЕТ
    private void getMethodArgs() {
        for (Parameter param: method.getParameters()) {
            String key = param.getName();
            System.out.println();
            if (!param.getAnnotation(RequestParam.class).name().isEmpty()) {
                key = param.getAnnotation(RequestParam.class).name();
            }
            classes.put(key, param.getType());
        }
    }

    private Object buildArgumentObject(Class<?> clazz) throws NoSuchMethodException
            , IllegalAccessException
            , InvocationTargetException
            , InstantiationException
            , NoSuchNotNullParameter {
        Object obj = clazz.getConstructor(clazz).newInstance(null);
        for (Field field: clazz.getFields()) {
            if (field.isAnnotationPresent(FormItem.class)) {
                String fieldName = field.getAnnotation(FormItem.class).name().isEmpty()
                        ?
                        field.getName():field.getAnnotation(FormItem.class).name();
                field.setAccessible(true);
                if (params.containsKey(fieldName)){
                    field.set(obj, params.get(fieldName));
                } else {
                    if (field.isAnnotationPresent(NotNull.class)) {
                      throw new NoSuchNotNullParameter("Параметр "
                              + fieldName + " формы "
                              + clazz.getName() + " не найден в запросе!");
                    } else {
                        field.set(obj, null);
                    }
                }
            }

        }
        return new Object();
    }
}
