package ru.pavel_zhukoff.forms;

import ru.pavel_zhukoff.annotations.form.FormItem;
import ru.pavel_zhukoff.annotations.form.NotNull;

public class UserForm {

    @FormItem
    String name;

    @FormItem(name = "a")
    @NotNull
    int age;

    String some;

    @Override
    public String toString() {
        return "UserForm{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", some='" + some + '\'' +
                '}';
    }
}
