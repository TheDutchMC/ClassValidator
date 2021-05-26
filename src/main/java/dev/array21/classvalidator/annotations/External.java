package dev.array21.classvalidator.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that the annotated Field uses a Class that is external to the Class the Field is in, but should also be checked<br>
 * This is not needed for subclasses.
 * 
 * @author Tobias de Bruijn
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface External {}
