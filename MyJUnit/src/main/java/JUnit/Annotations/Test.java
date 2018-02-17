package JUnit.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add this annotation to methods that you want to run as tests.
 * To ignore test you should set ignore as a non-empty string with message.
 * Method can't have "test" and "before/beforeClass/after/afterClass" annotations at the same time.
 * Set expected as exception's class to expect thrown excpetions.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    String ignore() default "";
    Class expected() default NotAnException.class;
}
