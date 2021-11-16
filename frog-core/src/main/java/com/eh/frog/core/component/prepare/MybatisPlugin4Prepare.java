/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.prepare;

import com.eh.frog.core.context.FrogRuntimeContextHolder;
import com.eh.frog.core.enums.PrepareFillDbType;
import com.eh.frog.core.sqlparser.SqlParseFacade;
import com.eh.frog.core.sqlparser.SqlParseResult;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.assertj.core.util.Lists;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author f90fd4n david
 * @version 1.0.0: MybatisPlugin4Prepare.java, v 0.1 2021-11-05 2:28 下午 david Exp $$
 */
@Intercepts({
		@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class MybatisPlugin4Prepare implements Interceptor {
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		Object parameter = invocation.getArgs()[1];

		BoundSql boundSql = mappedStatement.getBoundSql(parameter);
		Configuration configuration = mappedStatement.getConfiguration();

		Integer ret = (Integer) invocation.proceed();
		if (Objects.equals(ret, 1)) {
			DbPluginParseData dbPluginParseData = parseSql(configuration, boundSql);
			SqlParseResult sqlParseResult = SqlParseFacade.route(dbPluginParseData.getSql());
			if (PrepareFillDbType.UPDATE.equals(sqlParseResult.getPrepareFillDbType())) {
				DbPrepareFillHandler.fillUpdateData(sqlParseResult, FrogRuntimeContextHolder.getContext().getDbDataProcessor());
			} else if (PrepareFillDbType.INSERT.equals(sqlParseResult.getPrepareFillDbType())) {
				DbPrepareFillHandler.fillInsertData(sqlParseResult, dbPluginParseData.getParams());
			} else {
				throw new IllegalAccessException();
			}
		}
		return ret;
	}

	private DbPluginParseData parseSql(Configuration configuration, BoundSql boundSql) {
		String sql = boundSql.getSql();
		if (sql == null || sql.length() == 0) {
			return null;
		}
		DbPluginParseData dbPluginParseData = new DbPluginParseData();
		sql = beautifySql(sql);

		List<Object> params = Lists.newArrayList();
		Object parameterObject = boundSql.getParameterObject();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();//#{}

		if (!parameterMappings.isEmpty() && parameterObject != null) {
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				sql = replaceSql(sql, parameterObject);
				params.add(parameterObject);
			} else {
				MetaObject metaObject = configuration.newMetaObject(parameterObject);
				for (ParameterMapping parameterMapping : parameterMappings) {
					String propertyName = parameterMapping.getProperty();
					if (metaObject.hasGetter(propertyName)) {
						Object obj = metaObject.getValue(propertyName);
						sql = replaceSql(sql, obj);
						params.add(obj);
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						Object obj = boundSql.getAdditionalParameter(propertyName);
						sql = replaceSql(sql, obj);
						params.add(obj);
					}
				}
			}
		}
		dbPluginParseData.setSql(sql);
		dbPluginParseData.setParams(params);
		return dbPluginParseData;
	}

	private String replaceSql(String sql, Object parameterObject) {
		String result;
		if (Objects.isNull(parameterObject)) {
			result = null;
		} else if (parameterObject instanceof String) {
			result = "'" + parameterObject.toString() + "'";
		} else if (parameterObject instanceof Date) {
			result = "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(parameterObject) + "'";
		} else {
			result = parameterObject.toString();
		}
		return sql.replaceFirst("\\?", result);
	}

	private String beautifySql(String sql) {
		return sql.replaceAll("[\\s\n]+", " ");
	}

	/**
	 * 兼容低版本mybatis没有写默认方法
	 *
	 * @param target
	 * @return
	 */
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	/**
	 * 兼容低版本mybatis没有写默认方法
	 *
	 * @param properties
	 */
	@Override
	public void setProperties(Properties properties) {
		// NOP
	}
}