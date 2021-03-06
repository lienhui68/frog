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
package com.eh.frog.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * after clean do something
 * 
 * @author tianzhu.wtzh
 * @version $Id: AfterClean.java, v 0.1 2016年5月12日 上午11:18:38 tianzhu.wtzh Exp $
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
@FrogHook
public @interface AfterClean {
	String[] includes() default {};

	String[] excludes() default {};

	int order() default 0;
}
