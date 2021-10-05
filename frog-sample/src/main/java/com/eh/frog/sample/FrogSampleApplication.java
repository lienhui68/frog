/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogSampleApplication.java, v 0.1 2021-09-14 9:09 下午 david Exp $$
 */
@MapperScan(value = "com.eh.frog.sample.orm.dao")
@SpringBootApplication
public class FrogSampleApplication {
	public static void main(String[] args) {
		SpringApplication.run(FrogSampleApplication.class, args);
	}
}