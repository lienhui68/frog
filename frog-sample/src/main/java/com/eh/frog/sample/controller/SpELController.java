/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author f90fd4n david
 * @version 1.0.0: SpELController.java, v 0.1 2021-11-15 3:12 下午 david Exp $$
 */
@Slf4j
@RestController
@RequestMapping("/SpEL")
public class SpELController {

	@Value("#{'Hello World1'}")
	String helloWorld1;        //变量word赋值直接量：字符串"Hello World"

	@Value("Hello World2")
	String helloWorld2;        //变量word赋值直接量：字符串"Hello World"

	//注入list
	@Value("#{{7,2,3,5,1}}")
	private List<Integer> fList;


	/**
	 * {@code @Value} 注入String
	 *
	 * @return
	 */
	@RequestMapping(value = "/valueAnnoString", method = RequestMethod.GET)
	public String valueAnnoString() {
		return helloWorld1 + " & " + helloWorld2;
	}


	/**
	 * {@code @Value} 注入List
	 *
	 * @return
	 * @throws NoSuchMethodException
	 */
	@RequestMapping(value = "/valueAnnoList", method = RequestMethod.GET)
	public List<Integer> valueAnnoList() {
		return fList;
	}


	/**
	 * 测试通过ExpressionParser调用SpEL表达式
	 * @return
	 */
	@RequestMapping(value = "/expressionParse", method = RequestMethod.GET)
	public List<Integer> expressionParse() {

		//创建ExpressionParser解析表达式
		ExpressionParser parser = new SpelExpressionParser();
		List<Integer> result1 = parser.parseExpression("{4,5,5,6}").getValue(List.class);
		return result1;
	}



	/**
	 * 使用java代码
	 * @return
	 */
	@RequestMapping(value = "/javaCode", method = RequestMethod.GET)
	public Integer javaCode() {

		//创建ExpressionParser解析表达式
		ExpressionParser parser = new SpelExpressionParser();

		//等同于直接用java代码，还有方法调用
		String str = parser.parseExpression("new String('Hello World').substring(3)").getValue(String.class);
		log.info("str=={}", str);

		//TType 等同于java的Integer.MAX_VALUE
		Integer integer = parser.parseExpression("T(Integer).MAX_VALUE").getValue(Integer.class);
		log.info("integer=={}", integer);
		return integer;
	}




	/**
	 * 注入并调用method方法
	 * @return
	 * @throws NoSuchMethodException
	 */
	@RequestMapping("methodInvoke")
	private boolean methodInvoke() throws NoSuchMethodException {
		//创建ctx容器
		StandardEvaluationContext ctx = new StandardEvaluationContext();
		//获取java自带的Integer类的parseInt(String)方法
		Method parseInt = Integer.class.getDeclaredMethod("parseInt", String.class);
		//将parseInt方法注册在ctx容器内, 推荐这样使用
		ctx.registerFunction("parseInt", parseInt);
		//再将parseInt方法设为parseInt2
		ctx.setVariable("parseInt2", parseInt);

		//创建ExpressionParser解析表达式
		ExpressionParser parser = new SpelExpressionParser();
		//SpEL语法，比对两个方法执行完成后，结果是否相同
		String expreString = "#parseInt('2') == #parseInt2('3')";
		//执行SpEL
		Expression expression = parser.parseExpression(expreString);
		Boolean value = expression.getValue(ctx, Boolean.class);
		return value;
	}



	/**
	 * 运算符
	 * @return
	 */
	@RequestMapping(value = "/operator", method = RequestMethod.GET)
	public boolean operator() {

		//创建ctx容器
		StandardEvaluationContext ctx = new StandardEvaluationContext();
		//将字符串defg放在 ctx容器内
		ctx.setVariable("abc", new String("defg"));
		//创建ExpressionParser解析表达式
		ExpressionParser parser = new SpelExpressionParser();
		String abc = parser.parseExpression("#abc").getValue(ctx, String.class);
		log.info("abc=={}", abc);

		//运算符判断
		Boolean result = parser.parseExpression("#abc.length() > 3").getValue(ctx, Boolean.class);
		log.info("result=={}", result);
		return result;
	}


	/**
	 * Elvis等用法
	 * @return
	 */
	@RequestMapping(value = "/elvis", method = RequestMethod.GET)
	public void elvis(){
		//创建ctx容器
		StandardEvaluationContext ctx = new StandardEvaluationContext();
		//将字符串defg放在 ctx容器内
		ctx.setVariable("name", null);
		//创建ExpressionParser解析表达式
		ExpressionParser parser = new SpelExpressionParser();
		String name = parser.parseExpression("#name?:'other'").getValue(ctx, String.class);
		log.info("name=={}",name);
		log.info("saved length() == {}", parser.parseExpression("#name?.lenth()").getValue(ctx));

		//将字符串defg放在 ctx容器内
		ctx.setVariable("name", "abc");
		name = parser.parseExpression("#name?:'other'").getValue(ctx, String.class);
		log.info("changed name=={}",name);
		log.info("changed saved length() == {}", parser.parseExpression("#name?.length()").getValue(ctx));


		//map获取
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("a", 1);
		ctx.setVariable("map", map);
		int mapA = parser.parseExpression("#map['a']").getValue(ctx, int.class);
		log.info("map['a']=={}", mapA);
		//修改
		parser.parseExpression("#map['a']").setValue(ctx, 6);
		mapA = parser.parseExpression("#map['a']").getValue(ctx, int.class);
		log.info("changed map['a']=={}", mapA);

		return ;
	}


	@RequestMapping("/listFunction")
	private void listFunction() {
		//创建ctx容器
		StandardEvaluationContext ctx = new StandardEvaluationContext();
		//创建ExpressionParser解析表达式
		ExpressionParser parser = new SpelExpressionParser();

		//list过滤
		ctx.setVariable("aList",fList);
		List<Integer> cList = parser.parseExpression("#aList.?[#this>3]").getValue(ctx, List.class);
		log.info("filter list=={}", cList);


		List<Book> books = new ArrayList<>();
		books.add(new Book("JAVA Program", 2000, 102.5));
		books.add(new Book("C Program", 1985, 36));
		books.add(new Book("scala", 2015, 60));

		//object过滤
		ctx.setVariable("books", books);
		List<Book> filterBooks1 = (List<Book>) parser.parseExpression("#books.?[year>2000]").getValue(ctx);
		log.info("filterBooks1=={}", filterBooks1);

		//投影
		List<String> filterBooksName = parser.parseExpression("#books.?[price<100].![name]").getValue(ctx, List.class);
		log.info("filterBooksName=={}", filterBooksName);

		// .name相当于getName()方法
		String ssss = parser.parseExpression("#books[0].name").getValue(ctx, String.class);
		System.out.println(ssss);

		return;
	}



	@Data
	class Book{
		private String name;
		private int year;
		private double price;

		public Book(String name, int year, double price) {
			this.name = name;
			this.year = year;
			this.price = price;
		}
	}

}