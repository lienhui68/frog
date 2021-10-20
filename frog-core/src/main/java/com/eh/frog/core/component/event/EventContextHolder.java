/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eh.frog.core.component.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息拦截和check逻辑实现基于以下分析
 * * eg连接点格式：xxxSender.send(xxxMessage,...)
 * * why：因为消息中间件组件一般不会直接加载到容器中，一般都是上层bean wrap这个中间件组件，所以连接点各不相同，可以在客户端自行配置连接点
 * * 消息分组按照topicId更合适，但是拦截到bean.sendXxx方法方法时并不知道topic，可以采用Message的class作为分组key，因为topic和Message的1:1，如果多对1则存在自身设计不合理
 * Event content holder
 *
 * @author jie.peng
 * @version $Id: EventContextHolder.java, v 0.1 2015-6-29 下午06:00:25 jie.peng Exp $
 */
public class EventContextHolder {

	/**
	 * Business event message
	 * message class, message list
	 */
	private static Map<String, List<Object>> bizEvent = new HashMap<String, List<Object>>();

	/**
	 * Gets the event content held in the thread variable and clears the value in the thread variable.
	 * A thread is used only once to avoid data chaos when a thread is caused to run by multiple test cases.
	 *
	 * @return
	 */
	public static Map<String, List<Object>> getBizEvent() {
		return bizEvent;
	}

	public static void setEvent(String clazz, Object payLoad) {
		if (bizEvent == null) {
			bizEvent = new HashMap<>(8);
		}
		if (bizEvent.containsKey(clazz)) {
			bizEvent.get(clazz).add(payLoad);
		} else {
			List<Object> payLoads = new ArrayList<>();
			payLoads.add(payLoad);
			bizEvent.put(clazz, payLoads);
		}
	}

	/**
	 * Clean up thread variable
	 */
	public static void clear() {
		bizEvent = new HashMap<>(4);
	}
}
