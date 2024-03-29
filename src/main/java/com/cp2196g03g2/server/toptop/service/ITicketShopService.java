package com.cp2196g03g2.server.toptop.service;

import java.util.List;

import com.cp2196g03g2.server.toptop.dto.PagableObject;
import com.cp2196g03g2.server.toptop.dto.PagingRequest;
import com.cp2196g03g2.server.toptop.dto.TicketShopDto;
import com.cp2196g03g2.server.toptop.entity.TicketShop;
import com.cp2196g03g2.server.toptop.model.ChartCloumModel;

public interface ITicketShopService {
	List<TicketShop> findAll();
	TicketShop findById(Integer id);
	List<TicketShop> findByUserId(String id);
	TicketShop save(TicketShopDto ticketShopDto);
	TicketShop updateStatusTicket(TicketShopDto ticketShopDto);
	PagableObject<TicketShop> findAllByPage(PagingRequest request);
	Long totalTicketByCurrentMonthPassStatus(Integer status);
	Long totalTicketByCurrentYearPassStatus(Integer status);
	List<ChartCloumModel> ticketStatisticsByYearAndStatus(Integer status, Integer year);
}
