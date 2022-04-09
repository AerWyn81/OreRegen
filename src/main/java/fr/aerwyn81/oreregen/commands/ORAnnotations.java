package fr.aerwyn81.oreregen.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ORAnnotations {

    String command();
    String permission() default "";
    boolean isPlayerCommand() default false;
    String[] args() default {};
}
