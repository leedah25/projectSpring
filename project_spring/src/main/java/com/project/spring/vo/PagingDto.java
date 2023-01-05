package com.project.spring.vo;

import lombok.Data;

@Data
public class PagingDto {
	private int page = 1;
	private int startRow = 1;
	private int endRow = 5;
	private int startPage = 1;
	private int endPage = 5;
	private int totalPage;
	private int perPage=8;
	private int count; 
	
	private final int BLOCK_COUNT = 3;
	
	public void setPagingInfo(int page, int perPage, int count) {
		if(page > 0) {
			this.page = page;
		}
		this.perPage = perPage;
		this.count = count;
		
		this.page = page;
		this.endRow = page * perPage;
		this.startRow = this.endRow - (perPage-1);
		
		this.startPage = ((page-1)/BLOCK_COUNT)*BLOCK_COUNT+1;
		this.endPage = this.startPage + (BLOCK_COUNT-1);
		
		this.count = count;
		
		// count	totalPage
		// 500 		50 =500/10
		// 501		51=501/10올림
		// 30		4 = 30/8
		// count	count/10올림
		
		// Math.ceil 소숫자 숫자 올림!
		
		this.totalPage = (int)Math.ceil((float)count/perPage);
		if (this.endPage > this.totalPage) {
			this.endPage = this.totalPage;
		}
	}
}
