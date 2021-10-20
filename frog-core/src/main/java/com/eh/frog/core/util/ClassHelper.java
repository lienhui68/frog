/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.util;

import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author f90fd4n david
 * @version 1.0.0: ClassHelper.java, v 0.1 2021-10-20 8:27 下午 david Exp $$
 */
public class ClassHelper {
	private static final List<ClassLoader> m_classLoaders = new Vector();
	private static int m_lastGoodRootIndex = -1;

	public static void addClassLoader(ClassLoader loader) {
		m_classLoaders.add(loader);
	}

	private ClassHelper() {
	}

	public static Set<Method> getDeclaredAvailableMethods(Class<?> clazz) {
		Set<Method> methods = Sets.newHashSet();
		methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
		return methods;
	}

	public static Set<Method> getAvailableMethods(Class<?> clazz) {
		Set<Method> methods = Sets.newHashSet();
		methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));

		for (Class parent = clazz.getSuperclass(); Object.class != parent; parent = parent.getSuperclass()) {
			methods.addAll(extractMethods(clazz, parent, methods));
		}

		return methods;
	}

	private static Set<Method> extractMethods(Class<?> childClass, Class<?> clazz, Set<Method> collected) {
		Set<Method> methods = Sets.newHashSet();
		Method[] declaredMethods = clazz.getDeclaredMethods();
		Package childPackage = childClass.getPackage();
		Package classPackage = clazz.getPackage();
		boolean isSamePackage = false;
		if (null == childPackage && null == classPackage) {
			isSamePackage = true;
		}

		if (null != childPackage && null != classPackage) {
			isSamePackage = childPackage.getName().equals(classPackage.getName());
		}

		int len = declaredMethods.length;

		for (int i = 0; i < len; ++i) {
			Method method = declaredMethods[i];
			int methodModifiers = method.getModifiers();
			if ((Modifier.isPublic(methodModifiers) || Modifier.isProtected(methodModifiers) || isSamePackage && !Modifier.isPrivate(methodModifiers)) && !isOverridden(method, collected) && !Modifier.isAbstract(methodModifiers)) {
				methods.add(method);
			}
		}

		return methods;
	}

	private static boolean isOverridden(Method method, Set<Method> collectedMethods) {
		Class<?> methodClass = method.getDeclaringClass();
		Class<?>[] methodParams = method.getParameterTypes();
		Iterator it = collectedMethods.iterator();

		boolean sameParameters;
		do {
			Method m;
			Class[] paramTypes;
			do {
				do {
					do {
						if (!it.hasNext()) {
							return false;
						}

						m = (Method) it.next();
						paramTypes = m.getParameterTypes();
					} while (!method.getName().equals(m.getName()));
				} while (!methodClass.isAssignableFrom(m.getDeclaringClass()));
			} while (methodParams.length != paramTypes.length);

			sameParameters = true;

			for (int i = 0; i < methodParams.length; ++i) {
				if (!methodParams[i].equals(paramTypes[i])) {
					sameParameters = false;
					break;
				}
			}
		} while (!sameParameters);

		return true;
	}
}