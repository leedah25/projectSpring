package com.project.spring.board;

import lombok.Data;

@Data
public class BoardPagingDto {
	private int page=1;
	private int startRow=1;
	private int endRow=5;
	private int startPage=1;
	private int endPage=5;
	private int totalPage;
	private int count;
	private int perPage=8;
	private final int BLOCK_COUNT=5;
	
	public void setPagingInform(int page, int perPage,int count) {
		if(page>0) {
			this.page=page;
		}
		this.perPage = perPage;
		this.count = count;
		this.page = page;
		this.endRow=page*perPage;
		this.startRow=this.endRow-(perPage-1);
		this.startPage=((page-1)/BLOCK_COUNT)*BLOCK_COUNT+1;
		this.endPage=this.startPage+(BLOCK_COUNT-1);
		this.totalPage=(int)Math.ceil((double)count/perPage);
		if(this.endPage>this.totalPage) {
			this.endPage=this.totalPage;
		}
	}
}
