package com.project.spring.order;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import com.project.spring.login.MemberService;
import com.project.spring.cart.CartService;
import com.project.spring.detail.ProductService;
import com.project.spring.vo.MemberVo;
import com.project.spring.vo.OrderVo;

@Controller
@RequestMapping("/order/*")
public class OrderController { 
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MemberService memberService;

	@Autowired
	ProductService productService;
	
	@Autowired
	CartService cartService;
	
	// 방금(최근) 구매한 개인 주문목록만 조회
	@RequestMapping(value = "/recentMyOrder",method = RequestMethod.GET)
	public String recentOrderList(Model model, HttpSession session){
		MemberVo memberVo = (MemberVo)session.getAttribute("loginMemberVo");
		String member_id = memberVo.getMember_id();
		int orderCount = (int) session.getAttribute("orderCount");
		session.removeAttribute("orderCount");
		List<OrderVo> list = orderService.recentOrderList(orderCount, member_id);
		model.addAttribute("list", list);
		return "order/orderList";
	}
	
	//개인 주문리스트
	@RequestMapping(value = "/myOrder",method = RequestMethod.GET)
	public String myOrder(Model model, String arr_cart_no,HttpSession session) { 
		MemberVo memberVo = (MemberVo)session.getAttribute("loginMemberVo");
		List<OrderVo>list=orderService.myOrder(memberVo.getMember_id());
		model.addAttribute("list", list);
		return "order/orderList";
	}
	
	//주문 넣기
	@RequestMapping(value = "/insertOrder", method = RequestMethod.POST)
	public String order(String list, Model model, HttpSession session, OrderVo vo,String totalPrice) {
		String page="";
		JSONArray array = new JSONArray(list);
		MemberVo memberVo = (MemberVo)session.getAttribute("loginMemberVo");
		List<OrderVo> orderList = new ArrayList<>(); 
		String[] arr_product_id = new String[array.length()];
		int index = 0;
		for(Object obj: array) {
			JSONObject jsonObject = (JSONObject)obj;
			OrderVo orderVo = new OrderVo();
			orderVo.setMember_id(memberVo.getMember_id());
			orderVo.setProduct_id(String.valueOf(jsonObject.get("product_id")));
			orderVo.setOrder_amount((int)(jsonObject.get("cart_amount")));
			orderVo.setOrder_address(vo.getOrder_address());
			orderVo.setOrder_address_detail(vo.getOrder_address_detail());
			orderVo.setOrder_phonenum(vo.getOrder_phonenum());
			orderList.add(orderVo);	
			orderService.insertOrder(orderVo);
			
			// 주문시 재고차감 (t_product)
			productService.subInventory(orderVo);
			
			// 주문시 카드list 삭제 (t_cart)
			arr_product_id[index++] = (String) jsonObject.get("product_id");
		}
		cartService.deleteCart(arr_product_id, memberVo.getMember_id());
		session.setAttribute("orderCount", array.length());
		model.addAttribute("orderList",orderList);
		boolean result= orderService.updatePoint(memberVo.getMember_id(),Integer.parseInt(totalPrice));
		if(result) {
			page="redirect:/order/recentMyOrder";
			MemberVo memberVopoint= memberService.memberDetail(memberVo.getMember_id());
			memberVo.setMember_point(memberVopoint.getMember_point());
		}else {
			page="redirect:/shopping/payment";
		} 
		return page;

	}
  
}
