package com.project.spring.list;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.spring.vo.PagingDto;
import com.project.spring.vo.ProductVo;
@Service
public class ListService {
	
	@Autowired
	ListDao listDao;
	
	public List<ProductVo> getProductList(PagingDto pagingDto) {
		return listDao.getProductList(pagingDto);
	}
	
<<<<<<< HEAD
	public int getCount(String category) {
		return listDao.getCount(category);
=======
	public int getCount(PagingDto pagingDto) {
		return listDao.getCount(pagingDto);
>>>>>>> refs/remotes/origin/main
	}
	
	public List<ProductVo> getListByCategory(String category, PagingDto pagingDto){
		return listDao.getListByCategory(category, pagingDto);
	}
}
