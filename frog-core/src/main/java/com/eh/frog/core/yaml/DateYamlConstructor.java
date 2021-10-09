/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.yaml;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * snakeyaml只支持utc时间格式，需要自定义指定时间格式
 * ref:https://bitbucket.org/asomov/snakeyaml/issues/419/add-native-support-for-parsing-serializing
 *
 * @author f90fd4n david
 * @version 1.0.0: DateYamlConstructor.java, v 0.1 2021-10-09 9:15 上午 david Exp $$
 */
public class DateYamlConstructor extends Constructor {
	public DateYamlConstructor() {
		this.yamlClassConstructors.put(NodeId.scalar, new DateConstructor());
	}

	private class DateConstructor extends ConstructScalar {
		@Override
		public Object construct(Node node) {
			if (node.getType().equals(Date.class)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = null;
				try {
					date = sdf.parse(((ScalarNode) node).getValue());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return date;
			} else {
				return super.construct(node);
			}
		}
	}
}