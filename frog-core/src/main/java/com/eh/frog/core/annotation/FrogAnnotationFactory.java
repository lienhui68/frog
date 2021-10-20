/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import com.eh.frog.core.template.FrogTestBase;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
			addFrogMethod(method, AfterClean.class, template);
			addFrogMethod(method, BeforeClean.class, template);
			addFrogMethod(method, BeforeCheck.class, template);
			addFrogMethod(method, AfterCheck.class, template);
			addFrogMethod(method, BeforePrepare.class, template);
			addFrogMethod(method, AfterPrepare.class, template);

		}

	}

	/**
	 * add method
	 *
	 * @param m
	 * @param clsz
	 */
	private void addFrogMethod(Method m, Class<? extends Annotation> clsz, FrogTestBase template) {

		if (!annotationMethods.containsKey(clsz.getSimpleName())) {
			annotationMethods.put(clsz.getSimpleName(), Lists.newLinkedList());
		}

		addFrogMethod(annotationMethods.get(clsz.getSimpleName()), m, clsz, template);
	}

	/**
	 * Add annotation method
	 *
	 * @param m
	 * @param template
	 */
	private void addFrogMethod(List<IFrogMethod> methodList, Method m,
	                           Class<? extends Annotation> clsz, FrogTestBase template) {
		if (m.isAnnotationPresent(clsz)) {
			IFrogMethod iFrogMethod = new FrogMethodImpl(m, template);
			int i = 0;
			for (; i < methodList.size(); i++) {
				if (methodList.get(i).getOrder() > iFrogMethod.getOrder()) {
					break;
				}
			}
			methodList.add(i, iFrogMethod);
		}
	}
}