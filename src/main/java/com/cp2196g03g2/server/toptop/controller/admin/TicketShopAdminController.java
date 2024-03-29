package com.cp2196g03g2.server.toptop.controller.admin;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.cp2196g03g2.server.toptop.entity.ApplicationUser;
import com.cp2196g03g2.server.toptop.entity.TicketShop;
import com.cp2196g03g2.server.toptop.enums.TicketStatus;
import com.cp2196g03g2.server.toptop.model.ChartCloumModel;
import com.cp2196g03g2.server.toptop.repository.IRoleRepository;
import com.cp2196g03g2.server.toptop.repository.IUserRepository;
import com.cp2196g03g2.server.toptop.service.ITicketShopService;
import com.cp2196g03g2.server.toptop.service.IUserService;
import com.cp2196g03g2.server.toptop.service.impl.EmailServiceImpl;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/management/ticketshop")
@PreAuthorize("hasAnyAuthority('ROLE_TICKET_MODERATOR', 'ROLE_SUPERADMIN')")
public class TicketShopAdminController {
	
	@Autowired
	private ITicketShopService ticketShopService;
	
	@Autowired
	private IUserService userService;
	
	
	@Autowired
	private IUserRepository userRepository;
	
	@Autowired
	private IRoleRepository roleRepository;
	
	@Autowired
	private EmailServiceImpl emailServiceImpl;
	
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
	
	
	@GetMapping("/reports/month/{status}")
	public Long totalUserSendingTicketByMonth(@PathVariable Integer status) {
		return ticketShopService.totalTicketByCurrentMonthPassStatus(status);
	}
	
	@GetMapping("/reports/year/{status}")
	public Long totalUserSendingTicketByYear(@PathVariable Integer status) {
		return ticketShopService.totalTicketByCurrentYearPassStatus(status);
	}
	
	
	@GetMapping("/reports")
	public List<ChartCloumModel> staticsTicketByYear(@RequestParam(required = true) Integer status, @RequestParam(name = "year", required = false) Integer year) {
		return ticketShopService.ticketStatisticsByYearAndStatus(status, year);
	}
	
	
	@GetMapping("/user/{userid}")
	public ApplicationUser findByUserId(@PathVariable String userid){
		return userService.findById(userid);
	}
	
	
	@PostMapping
	public TicketShop save(@RequestBody TicketShopDto dto) {
		return ticketShopService.save(dto);
	}
	
	@PutMapping
	public TicketShop update(@RequestBody TicketShopDto dto) {
		
		TicketShop ticketUpdate = ticketShopService.updateStatusTicket(dto);
		if(ticketUpdate.getStatus().equals(TicketStatus.ACTIVE)) {
			ApplicationUser user = userRepository.findById(ticketUpdate.getUser().getId()).get();
			user.setRole(roleRepository.findById(6L).get());
			HashMap<String, Object> model = new HashMap<>();
			model.put("name", user.getFullName());
			model.put("project_name", ticketUpdate.getContent());
			emailServiceImpl.sendMail(user.getEmail(), "Congragulation", "ticket-enable", model);
		}
		return ticketUpdate;
	}
	
	
	

}
