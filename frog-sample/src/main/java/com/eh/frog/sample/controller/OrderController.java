package com.eh.frog.sample.controller;


import com.eh.frog.sample.base.Response;
import com.eh.frog.sample.enums.OrderEventType;
import com.eh.frog.sample.lion.OrderLionConfig;
import com.eh.frog.sample.mq.model.OrderEventMessage;
import com.eh.frog.sample.mq.producer.OrderEventSender;
import com.eh.frog.sample.orm.bean.Order;
import com.eh.frog.sample.orm.bean.User;
import com.eh.frog.sample.orm.dao.OrderMapper;
import com.eh.frog.sample.orm.dao.UserMapper;
import com.eh.frog.sample.rpc.response.Coupon;
import com.eh.frog.sample.rpc.virtual.CouponReq;
import com.eh.frog.sample.rpc.virtual.CouponRpcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
public class OrderController {

	// 数据库操作
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private UserMapper userMapper;
	// 消息发送
	@Autowired
	private OrderEventSender orderEventSender;
	// rpc服务
	@Autowired
	private CouponRpcService couponRpcService;

	@PostMapping("/order/create")
	public Response<Boolean> create(@Param("order") Order order) {
		// check用户是否存在
		User user = userMapper.selectByUserId(order.getBuyerId());
		if (user == null) {
			return Response.error("用户不存在");
		}
		// 获取分布式ID
		order.setOrderId(generateDistributedId());
		// 实际订单金额
		BigDecimal actualOrderAmount = order.getOrderAmount();
		// 如果业务开关打开，需要走优惠逻辑
		Coupon coupon = null;
		if (OrderLionConfig.getBizSwitch()) {
			coupon = couponRpcService.getCoupon(order.getBuyerId(), order.getOrderAmount(), new CouponReq("bbb", 1));
			// 更新实际金额
			actualOrderAmount = actualOrderAmount.subtract(coupon.getCouponAmount());
		}
		order.setOrderAmount(actualOrderAmount);
		order.setOrderStatus(1);
		orderMapper.insertSelective(order);
		// 发送订单创建成功消息
		OrderEventMessage orderEventMessage = new OrderEventMessage();
		orderEventMessage.setOrderId(order.getOrderId());
		orderEventMessage.setEventType(OrderEventType.CREATE);
		orderEventMessage.setOrderAmount(order.getOrderAmount());
		orderEventMessage.setOrderTime(order.getOrderTime());
		orderEventMessage.setCoupon(coupon);
		orderEventSender.sendMessage(orderEventMessage);
		return Response.success(true);
	}

	@PostMapping("/order/pay")
	public Response<Boolean> pay(@Param("id") Long orderId) {
		Order request = new Order();
		request.setOrderId(orderId);
		request.setOrderStatus(30);
		orderMapper.updateByOrderId(request);
		return Response.success(true);
	}

//	@PostMapping("/order/pay")
//	public Response<Boolean> writeRpc(@Param("id") Long orderId) {
//		couponRpcService.getCoupon(111L, new BigDecimal(100), new CouponReq("bbb", 1));
//		return Response.success(true);
//	}

	private Long generateDistributedId() {
		return RandomUtils.nextLong();
	}

}