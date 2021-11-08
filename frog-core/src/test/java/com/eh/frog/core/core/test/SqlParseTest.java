/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.core.test;

import com.eh.frog.core.sqlparser.SqlParseFacade;
import com.eh.frog.core.sqlparser.SqlParseResult;
import org.junit.jupiter.api.Test;

/**
 * @author f90fd4n david
 * @version 1.0.0: SqlParseTest.java, v 0.1 2021-11-05 4:05 下午 david Exp $$
 */
public class SqlParseTest {
	@Test
	public void testInsert() {
		String sql = "INSERT INTO `scheduler`.`look_cargo_intention`(`id`, `driver_id`, `cooperation_type`, `consign_order_id`, `start_province_id`, `start_province_name`, `start_city_id`, `start_city_name`, `start_district_id`, `start_district_name`, `start_detail_name`, `start_longitude`, `start_latitude`, `can_load_time`, `look_cargo_start_time`, `look_cargo_end_time`, `truck_use_type`, `truck_type`, `truck_length`, `truck_load`, `truck_volume`, `status`, `customer_service_id`, `customer_service_name`, `group`, `create_time`, `update_time`, `is_valid`, `product_list`) VALUES (10805, 1025229, 2, 9101, 320000, '江苏省', 320200, '无锡市', 270213, '梁溪区', '江苏省无锡市梁溪区兴昌北路', 120.291193, 31.599657, '2021-10-22 14:38:01', '2021-10-21 15:58:00', '2021-10-22 17:38:01', 2, 2, 13.0, 30.00, NULL, 20, NULL, '', 6, '2021-10-22 14:38:03', '2021-10-22 14:39:36', 1, '159');";
		SqlParseResult sqlParseResult = SqlParseFacade.route(sql);
		System.out.println(sqlParseResult);
	}

	@Test
	public void testUpdate() {
		String sql = "update tbl_user set a=1, b= 2 where c = 3";
		SqlParseResult sqlParseResult = SqlParseFacade.route(sql);
		System.out.println(sqlParseResult);
	}
}