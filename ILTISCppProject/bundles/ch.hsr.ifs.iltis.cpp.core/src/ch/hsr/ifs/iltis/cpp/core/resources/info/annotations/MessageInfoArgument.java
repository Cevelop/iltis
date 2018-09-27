package ch.hsr.ifs.iltis.cpp.core.resources.info.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Used in combination with {@link InfoArgument} to mark a field to be used as a message pattern argument.
 * 
 * This annotation can only be used to annotate {@link String} fields.
 * 
 * 
 * @author tstauber
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageInfoArgument {

   int value();

}
