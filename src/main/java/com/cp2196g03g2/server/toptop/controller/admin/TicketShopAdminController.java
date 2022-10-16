package com.cp2196g03g2.server.toptop.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cp2196g03g2.server.toptop.constant.AppConstants;
import com.cp2196g03g2.server.toptop.dto.PagableObject;
import com.cp2196g03g2.server.toptop.dto.PagingRequest;
import com.cp2196g03g2.server.toptop.dto.TicketShopDto;
import com.cp2196g03g2.server.toptop.entity.Coupon;
import com.cp2196g03g2.server.toptop.entity.TicketShop;
import com.cp2196g03g2.server.toptop.repository.ITicketShopRepository;
import com.cp2196g03g2.server.toptop.service.ITicketShopService;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/management/ticketshop")
public class TicketShopAdminController {
	
	@Autowired
	private ITicketShopService ticketShopService;
	
	@GetMapping
	public PagableObject<TicketShop> findAllByPage(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(value = "keyword", defaultValue = AppConstants.DEFAULT_KEYWORD, required = false) String keyword)
    {
        PagingRequest request = new PagingRequest(pageNo, pageSize, sortBy, sortDir, keyword);
		return ticketShopService.findAllByPage(request);
	}
	
	@GetMapping("/{id}")
	public TicketShop findById(@PathVariable Integer id){
		return ticketShopService.findById(id);
	}
	
	@GetMapping("/user/{userid}")
	public List<TicketShop> findByUserId(@PathVariable String userid){
		return ticketShopService.findByUserId(userid);
	}
	
	
	@PostMapping
	public TicketShop save(@RequestBody TicketShopDto dto) {
		return ticketShopService.save(dto);
	}
	
	@PutMapping
	public TicketShop update(@RequestBody TicketShopDto dto) {
		return ticketShopService.updateStatusTicket(dto);
	}

}