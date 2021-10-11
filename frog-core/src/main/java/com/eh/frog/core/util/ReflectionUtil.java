/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.util;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author f90fd4n david
 * @version 1.0.0: ReflectionUtil.java, v 0.1 2021-10-11 10:05 上午 david Exp $$
 */
public class ReflectionUtil {

	/**
	 * Cache for {@link Class#getDeclaredMethods()} plus equivalent default methods
	 * from Java 8 based interfaces, allowing for fast iteration.
	 */
	private static final Map<Class<?>, Method[]> declaredMethodsCache = new ConcurrentReferenceHashMap<>(256);

	private static final Method[] NO_METHODS = {};

	/**
	 * Perform the given callback operation on all matching methods of the given
	 * class and superclasses (or given interface and super-interfaces).
	 * <p>The same named method occurring on subclass and superclass will appear
	 * twice, unless excluded by the specified {@link ReflectionUtils.MethodFilter}.
	 *
	 * @param clazz the class to introspect
	 * @param mc    the callback to invoke for each method
	 * @param mf    the filter that determines the methods to apply the callback to
	 * @throws IllegalStateException if introspection fails
	 */
	public static void doWithMethods(Class<?> clazz, MethodCallback mc, @Nullable MethodFilter mf) throws Exception {
		// Keep backing up the inheritance hierarchy.
		Method[] methods = getDeclaredMethods(clazz);
		for (Method method : methods) {
			if (mf != null && !mf.matches(method)) {
				continue;
			}
			try {
				mc.doWith(method);
			} catch (IllegalAccessException ex) {
				throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
			}
		}
	}


	/**
	 * This variant retrieves {@link Class#getDeclaredMethods()} from a local cache
	 * in order to avoid the JVM's SecurityManager check and defensive array copying.
	 * In addition, it also includes Java 8 default methods from locally implemented
	 * interfaces, since those are effectively to be treated just like declared methods.
	 *
	 * @param clazz the class to introspect
	 * @return the cached array of methods
	 * @throws IllegalStateException if introspection fails
	 * @see Class#getDeclaredMethods()
	 */
	private static Method[] getDeclaredMethods(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		Method[] result = declaredMethodsCache.get(clazz);
		if (result == null) {
			try {
				Method[] declaredMethods = clazz.getDeclaredMethods();
				List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
				if (defaultMethods != null) {
					result = new Method[declaredMethods.length + defaultMethods.size()];
					System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
					int index = declaredMethods.length;
					for (Method defaultMethod : defaultMethods) {
						result[index] = defaultMethod;
						index++;
					}
				} else {
					result = declaredMethods;
				}
				declaredMethodsCache.put(clazz, (result.length == 0 ? NO_METHODS : result));
			} catch (Throwable ex) {
				throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
						"] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
			}
		}
		return result;
	}

	@Nullable
	private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
		List<Method> result = null;
		for (Class<?> ifc : clazz.getInterfaces()) {
			for (Method ifcMethod : ifc.getMethods()) {
				if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
					if (result == null) {
						result = new LinkedList<>();
					}
					result.add(ifcMethod);
				}
			}
		}
		return result;
	}

	/**
	 * Action to take on each method.
	 */
	@FunctionalInterface
	public interface MethodCallback {

		/**
		 * Perform an operation using the given method.
		 *
		 * @param method the method to operate on
		 */
		void doWith(Method method) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException;
	}


	/**
	 * Callback optionally used to filter methods to be operated on by a method callback.
	 */
	@FunctionalInterface
	public interface MethodFilter {

		/**
		 * Determine whether the given method matches.
		 *
		 * @param method the method to check
		 */
		boolean matches(Method method);
	}
}