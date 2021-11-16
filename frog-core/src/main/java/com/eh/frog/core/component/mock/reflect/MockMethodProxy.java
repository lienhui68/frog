/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.mock.reflect;

import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.ext.MockUnit;
import com.eh.frog.core.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author f90fd4n david
 * @version 1.0.0: MethodProxy.java, v 0.1 2021-11-11 4:02 下午 david Exp $$
 */
@NoArgsConstructor
@AllArgsConstructor
public class MockMethodProxy implements InvocationHandler {

	private Method mockMethod;
	// mock obj, any() -> returnObj
	private Object object;
	// mock obj, when(xxx).thenReturn(obj)，使用Spel表达式，变量使用args
	private List<MockUnit> mockUnits;


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (!mockMethod.equals(method)) {
			throw new IllegalArgumentException("mock方法与实际执行方法不一致");
		}
		//如果传进来是一个已实现的具体类
		if (Object.class.equals(method.getDeclaringClass())) {
			try {
				return method.invoke(this, args);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			//如果传进来的是一个接口（核心)
		} else {
			return run(args);
		}
		return null;
	}

	/**
	 * 实现接口的核心方法
	 *
	 * @param args
	 * @return
	 */
	public Object run(Object[] args) {
		if (Objects.nonNull(mockUnits)) {
			StandardEvaluationContext ctx = new StandardEvaluationContext();
			ExpressionParser parser = new SpelExpressionParser();
			ctx.setVariable("args",args);
			for (MockUnit u : mockUnits) {
				try {
					if (parser.parseExpression(u.getWhen()).getValue(ctx, Boolean.class)) {
						return u.getThenReturn();
					}
				} catch (EvaluationException e) {
					throw new FrogTestException(StringUtil.buildMessage("SpEL表达式:{}执行有误", u.getWhen()), e);
				} catch (ParseException e) {
					throw new FrogTestException(StringUtil.buildMessage("SpEL表达式:{}解析有误", u.getWhen()), e);
				}
			}
		}
		if (Objects.nonNull(object)) {
			return object;
		}
		return "method call success!";
	}

}