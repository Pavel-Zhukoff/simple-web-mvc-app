package ru.pavel_zhukoff.request;

import ru.pavel_zhukoff.annotations.RequestParam;
import ru.pavel_zhukoff.annotations.form.FormItem;
import ru.pavel_zhukoff.annotations.form.NotNull;
import ru.pavel_zhukoff.exceptions.NoSuchNotNullParameter;

import java.lang.reflect.*;
import java.util.*;

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
        for (Map.Entry<String, Class<?>> entry: classes.entrySet()) {
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

    private void getMethodArgs() {
        for (Parameter param: method.getParameters()) {
            classes.put(param.getName(), param.getType());
        }
    }

    private Object buildArgumentObject(Class<?> clazz) throws NoSuchMethodException
            , IllegalAccessException
            , InvocationTargetException
            , InstantiationException
            , NoSuchNotNullParameter {
        Object obj = clazz.getConstructor(null).newInstance(null);
        System.out.println(Arrays.toString(clazz.getDeclaredFields()));
        for (Field field: clazz.getDeclaredFields()) {
            System.out.println(field.getType().getTypeName());
            if (field.isAnnotationPresent(FormItem.class)) {
                String fieldName = field.getAnnotation(FormItem.class).name().isEmpty()
                        ? field.getName() : field.getAnnotation(FormItem.class).name();
                field.setAccessible(true);

                if (params.containsKey(fieldName)){
                    field.set(obj, ValueParser.parseValue(field.getType().getTypeName(), params.get(fieldName)));
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
        return obj;
    }
}
