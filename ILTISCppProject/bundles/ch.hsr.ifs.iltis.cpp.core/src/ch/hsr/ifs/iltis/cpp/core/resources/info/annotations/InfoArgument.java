package ch.hsr.ifs.iltis.cpp.core.resources.info.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ch.hsr.ifs.iltis.cpp.core.resources.info.IInfo;


/**
 * Used in combination with an {@link IInfo} to mark a field to be copied and persisted.
 *
 * This annotation can only be used to annotate fields of following types:
 * <ul>
 * <li>Boolean & boolean</li>
 * <li>Character & char</li>
 * <li>Byte & byte</li>
 * <li>Short & short</li>
 * <li>Integer & int</li>
 * <li>Long & long</li>
 * <li>Float & float</li>
 * <li>Double & double</li>
 * <li>String</li>
 * <li>Anything that extends IStringifyable</li>
 * </ul>
 *
 * @author tstauber
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InfoArgument {}
