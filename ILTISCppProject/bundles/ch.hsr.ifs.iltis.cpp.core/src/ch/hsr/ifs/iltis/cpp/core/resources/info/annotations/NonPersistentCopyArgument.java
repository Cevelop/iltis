package ch.hsr.ifs.iltis.cpp.core.resources.info.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ch.hsr.ifs.iltis.cpp.core.resources.info.IInfo;


/**
 * Used in combination with {@link IInfo} to mark a field to be included in copies, but not to be persisted into the resource marker.
 *
 * This annotation can only be used to annotate any field that is able to be cloned.
 *
 *
 * @author tstauber
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NonPersistentCopyArgument {}
