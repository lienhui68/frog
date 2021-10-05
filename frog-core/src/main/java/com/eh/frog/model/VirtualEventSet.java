/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualEventSet.java, v 0.1 2021-09-15 3:02 下午 david Exp $$
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualEventSet {
	private List<VirtualEventObject> virtualEventObjects = new ArrayList<>();

	/**
	 * Add a message check object
	 *
	 * @param eventObject
	 * @param topicId
	 * @return
	 */
	public VirtualEventSet addEventObject(Object eventObject, String topicId) {
		VirtualEventObject virtualEventObject = new VirtualEventObject();
		virtualEventObject.setMsgClass(topicId);
		virtualEventObject.setEventObject(new VirtualObject(eventObject));
		virtualEventObjects.add(virtualEventObject);
		return this;
	}
}