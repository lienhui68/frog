/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import com.eh.frog.core.template.FrogTestBase;
import com.google.common.collect.Lists;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogAnnotationFactory.java, v 0.1 2021-10-20 7:48 下午 david Exp $$
 */
public class FrogAnnotationFactory {
	/**
	 * Currently registered annotation method
	 */
	protected Map<String, List<IFrogMethod>> annotationMethods;


	public FrogAnnotationFactory(Map<String, List<IFrogMethod>> annotationMethods) {
		this.annotationMethods = annotationMethods;
	}

	/**
	 * scan and get @BeforeClean, @AfterClean, @BeforeCheck, @AfterCheck
	 *
	 * @BeforePrepare, @AfterPrepare, @BeforeTable, @AfterTable
	 */
	public void initAnnotationMethod(Set<Method> allMethod, FrogTestBase template) {
		for (Method method : allMethod) {
			if (method.isAnnotationPresent(BeforeClean.class)) {
				BeforeClean hook = method.getAnnotation(BeforeClean.class);
				addFrogMethod(method, BeforeClean.class, template, Lists.newArrayList(hook.includes()), Lists.newArrayList(hook.excludes()), hook.order());
				continue;
			}
			if (method.isAnnotationPresent(AfterClean.class)) {
				AfterClean hook = method.getAnnotation(AfterClean.class);
				addFrogMethod(method, AfterClean.class, template, Lists.newArrayList(hook.includes()), Lists.newArrayList(hook.excludes()), hook.order());
				continue;
			}
			if (method.isAnnotationPresent(BeforePrepare.class)) {
				BeforePrepare hook = method.getAnnotation(BeforePrepare.class);
				addFrogMethod(method, BeforePrepare.class, template, Lists.newArrayList(hook.includes()), Lists.newArrayList(hook.excludes()), hook.order());
				continue;
			}
			if (method.isAnnotationPresent(AfterPrepare.class)) {
				AfterPrepare hook = method.getAnnotation(AfterPrepare.class);
				addFrogMethod(method, AfterPrepare.class, template, Lists.newArrayList(hook.includes()), Lists.newArrayList(hook.excludes()), hook.order());
				continue;
			}
			if (method.isAnnotationPresent(BeforeExecute.class)) {
				BeforeExecute hook = method.getAnnotation(BeforeExecute.class);
				addFrogMethod(method, BeforeExecute.class, template, Lists.newArrayList(hook.includes()), Lists.newArrayList(hook.excludes()), hook.order());
				continue;
			}
			if (method.isAnnotationPresent(AfterExecute.class)) {
				AfterExecute hook = method.getAnnotation(AfterExecute.class);
				addFrogMethod(method, AfterExecute.class, template, Lists.newArrayList(hook.includes()), Lists.newArrayList(hook.excludes()), hook.order());
				continue;
			}
			if (method.isAnnotationPresent(BeforeCheck.class)) {
				BeforeCheck hook = method.getAnnotation(BeforeCheck.class);
				addFrogMethod(method, BeforeCheck.class, template, Lists.newArrayList(hook.includes()), Lists.newArrayList(hook.excludes()), hook.order());
				continue;
			}
			if (method.isAnnotationPresent(AfterCheck.class)) {
				AfterCheck hook = method.getAnnotation(AfterCheck.class);
				addFrogMethod(method, AfterCheck.class, template, Lists.newArrayList(hook.includes()), Lists.newArrayList(hook.excludes()), hook.order());
				continue;
			}
		}

	}

	/**
	 * add method
	 *
	 * @param m
	 * @param clsz
	 */
	private void addFrogMethod(Method m, Class<? extends Annotation> clsz, FrogTestBase template, List<String> includes, List<String> excludes, int order) {

		if (!annotationMethods.containsKey(clsz.getSimpleName())) {
			annotationMethods.put(clsz.getSimpleName(), Lists.newLinkedList());
		}

		IFrogMethod iFrogMethod = new FrogMethodImpl(m, template, includes, excludes, order);
		annotationMethods.get(clsz.getSimpleName()).add(iFrogMethod);
	}

}