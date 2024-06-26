package com.project.spring.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.spring.vo.AdminVo;
import com.project.spring.vo.OrderVo;

@Service
public class OrderService {

	@Autowired
	OrderDao orderDao;
	
	// 방금(최근) 구매한 개인 주문목록만 조회
	public List<OrderVo> recentOrderList(int orderCount, String member_id){
		return orderDao.recentOrderList(orderCount, member_id);
	}
	
	public List<OrderVo> orderList() {
		return orderDao.orderList();
	}
	
	public List<OrderVo> myOrder(String member_id) {
		return orderDao.myOrder(member_id);
	}
	
	public OrderVo detailOrder(String order_no) {
		return orderDao.detailOrder(order_no);
	}
	
	public boolean insertOrder(OrderVo orderVo) {
		return orderDao.insertOrder(orderVo);
	}
	
	public boolean deleteOrder(String order_no) {
		return orderDao.deleteOrder(order_no);
	}
	
	public boolean updateOrder(OrderVo orderVo) {
		return orderDao.updateOrder(orderVo);
	}
	
	public List<OrderVo> orderListBymemId(String member_id){
		return orderDao.orderListBymemId(member_id);
	}
	
	@Transactional
	public Boolean updatePoint(String member_id,int usePoint) {
		int now_point=orderDao.nowPoint(member_id);
		int nowHavePoint=now_point-usePoint;
		return orderDao.updatePoint(member_id,nowHavePoint);
	}
	
	public boolean checkBuyer(String member_id,String product_id) {
		return orderDao.checkBuyer(member_id,product_id);
	}
	
	public List<OrderVo> getOrderList() {
		return orderDao.getOrderList();
	}
	
	public List<AdminVo> getChart(){
		return orderDao.getChart();
	}
	
	public List<AdminVo> getBestSeller(){
		return orderDao.getBestSeller();
	}

}
