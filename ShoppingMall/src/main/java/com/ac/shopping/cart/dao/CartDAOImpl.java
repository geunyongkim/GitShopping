package com.ac.shopping.cart.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.ac.shopping.cart.dto.CartDTO;
import com.ac.shopping.cart.dto.Non_mem_CartDTO;
import com.ac.shopping.cart.dto.Non_mem_OrderDTO;
import com.ac.shopping.cart.dto.OrderDTO;
import com.ac.shopping.cart.dto.Order_listDTO;

@Repository
public class CartDAOImpl implements CartDAO {

	@Inject
	SqlSession sqlSession;

	// 1. 장바구니 추가
	@Override
	public void insert(CartDTO cartDto) {
		sqlSession.insert("cart.insertCart", cartDto);
	}

	// 2. 장바구니 목록
	@Override
	public List<CartDTO> listCart(String m_Id) {
		return sqlSession.selectList("cart.listCart", m_Id);

	}

	// 3. 장바구니 삭제
	@Override
	public void delete(String product_No, String m_Id) {
		// 파라미터 지정 필요

		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("product_No", product_No);
		param.put("m_Id", m_Id);

		System.out.println(product_No);
		System.out.println(m_Id);

		// 넣을 변수는 2개인데 받을 수 있는 변수의 수는 1개 뿐일때
		// hashmap을 써서 두개의 변수를 임시로 param으로 통합시킨다.

		sqlSession.delete("cart.deleteCart", param);
	}

	// 4.장바구니 수정
	@Override
	public void modifyCart(CartDTO cartDto) {
		sqlSession.update("cart.modifyCart", cartDto);
	}
	public void modifyCartList(List<String> quantity, List<String> product_no, String m_Id) {
	      for(int i=0;i<quantity.size();i++){
	         
	         Map<String, Object> map = new HashMap<String, Object>();
	         map.put("basket_Quantity", quantity.get(i));
	         map.put("product_No", product_no.get(i));
	         map.put("m_Id", m_Id);
	         
	         sqlSession.update("cart.modifyCart",map);
	      }
	      
	   }
	// 장바구니 금액 합계
	@Override
	public int sumMoney(String m_Id) {
		sqlSession.selectOne("cart.sumMoney", m_Id);
		return sqlSession.selectOne("cart.sumMoney", m_Id);
		// 장바구니 상품전체 금액select조회한 결과를 리턴
	}

	// 6. 장바구니에 동일한 상품이 있는지 확인
	@Override
	public int countCart(String product_No, String m_Id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("product_No", product_No);
		map.put("m_Id", m_Id);
		return sqlSession.selectOne("cart.countCart", map);
		// 장바구니에 동일한 상품이 있는지 select 조회한 결과를 리턴
	}

	@Override
	public void updateCart(CartDTO cartDto) {
		sqlSession.update("cart.sumCart", cartDto);
		// 동일한 상품일 경우 수량을 합산한여 update
	}
	
	@Override
	public String direct_order(CartDTO cdto, OrderDTO odto) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("clist", cdto);
		map.put("odto", odto);
		int count = sqlSession.selectOne("cart.countCart", cdto.getM_Id());
		String order_no = "";
//		for (int i = 0; i < count; i++) {
//			if (i == 0) {
				sqlSession.insert("cart.order",map);
				order_no = (String)map.get("PARM10");
				map.put("order_no", order_no);
//			}else{
//				sqlSession.insert("cart.order2",map);
//				System.out.println(order_no);
//			}
//		}
		// 주문번호, 제품번호, 사이즈, 컬러, 수량
		// M_REORDER
				return order_no;
	}
	
	@Override
	public String order(String m_id, OrderDTO odto){
		List<CartDTO> clist = sqlSession.selectList("cart.listCart", m_id);
		int count = sqlSession.selectOne("cart.countCart", m_id);
		String order_no = "";
		System.out.println(odto.getName());


		System.out.println(count);
		System.out.println(m_id);
		for (int i = 0; i < count; i++) {
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("clist", clist.get(i));
			param.put("odto", odto);
			param.put("order_no", order_no);
			
			if (i == 0) {
				sqlSession.insert("cart.order",param);
				order_no = (String)param.get("PARM10");
			}else{
				System.out.println(order_no);
				sqlSession.insert("cart.re_order",param);
			}
		}
		return order_no;
	}

//	public void nonMem_order(HashMap<String, Object> param) {
//		String order_no = "";
//
//		for (int i = 0; i < param..size(); i++) {
//			
//			HashMap<String, Object> param = new HashMap<String, Object>();
//			param.put("clist", clist.get(i));
//			param.put("odto", odto);
//			param.put("order_no", order_no);
//			
//			if (i == 0) {
//				sqlSession.insert("cart.order",param);
//				order_no = (String)param.get("PARM10");
//			}else{
//				System.out.println(order_no);
//				sqlSession.insert("cart.re_order",param);
//			}
//		}
//	}

	public String nonMem_order(Non_mem_OrderDTO nmodto, List<Non_mem_CartDTO> plist) {
		
		String order_no = "";
		for (int i = 0; i < plist.size(); i++) {
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("nmodto", nmodto);
			param.put("plist", plist.get(i));
			param.put("order_no", order_no);
			
			if (i == 0) {
				sqlSession.insert("cart.nonMem_order",param);
				order_no = (String)param.get("PARM12");
			}else{
				System.out.println(order_no);
				sqlSession.insert("cart.re_nonMem_order",param);
			}
		}
		return order_no;
		
	}

	public HashMap<String, Object> order_list(HttpSession session, String order_no) {
		HashMap<String, Object> param = new HashMap<String, Object>();
		//String m_Id = (String) session.getAttribute("m_id");
		if ((String) session.getAttribute("m_id") != null && (String) session.getAttribute("m_id") != "") {
			List<Order_listDTO> olist = sqlSession.selectList("cart.order_list", order_no);
			param.put("order_no", order_no);
			param.put("olist", olist);
		}else{
			List<Order_listDTO> olist = sqlSession.selectList("cart.nonorder_list", order_no);
			param.put("order_no", order_no);
			param.put("olist", olist);
		}
		return param;
	}



}
