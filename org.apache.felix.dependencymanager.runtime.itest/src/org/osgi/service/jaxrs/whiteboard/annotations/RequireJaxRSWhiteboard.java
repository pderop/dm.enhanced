package org.osgi.service.jaxrs.whiteboard.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to require the Http Whiteboard implementation. It
 * can be used directly, or as a meta-annotation.
 * <p>
 * This annotation is applied to several of the Http Whiteboard component
 * property annotations meaning that it does not normally need to be applied to
 * DS components which use the Http Whiteboard.
 * 
 * @author $Id: d8dd2f2d079255ec829406ab1e496054a4528d1b $
 * @since 1.0
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface RequireJaxRSWhiteboard {
	// This is a purely informational annotation and has no elements.
}
