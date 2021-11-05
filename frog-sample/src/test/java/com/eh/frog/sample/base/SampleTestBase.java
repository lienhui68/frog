/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample.base;

import com.eh.frog.core.annotation.BeforeExecute;
import com.eh.frog.core.annotation.EnablePlugin;
import com.eh.frog.core.annotation.PluginSignature;
import com.eh.frog.core.context.FrogRuntimeContext;
import com.eh.frog.core.plugin.PersistencePlugin;
import com.eh.frog.core.template.FrogTestBase;
import com.eh.frog.plugin.redis.ClusterRedisPersistencePluginImpl;

/**
 * @author f90fd4n david
 * @version 1.0.0: SampleTestBase.java, v 0.1 2021-11-04 2:40 下午 david Exp $$
 */
@EnablePlugin({
		@PluginSignature(type = PersistencePlugin.class, plugins = {ClusterRedisPersistencePluginImpl.class})
})
public class SampleTestBase extends FrogTestBase {

	@BeforeExecute
	protected void prepareSampleMessage(FrogRuntimeContext frogRuntimeContext) {
		System.out.println("prepareSampleMessage order:0");
	}
}