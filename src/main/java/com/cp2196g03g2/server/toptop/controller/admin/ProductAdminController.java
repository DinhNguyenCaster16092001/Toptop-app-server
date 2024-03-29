package com.cp2196g03g2.server.toptop.controller.admin;

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
import com.cp2196g03g2.server.toptop.dto.CouponDTO;
import com.cp2196g03g2.server.toptop.dto.PagableObject;
import com.cp2196g03g2.server.toptop.dto.PagingRequest;
import com.cp2196g03g2.server.toptop.dto.ProductDto;
import com.cp2196g03g2.server.toptop.entity.Coupon;
import com.cp2196g03g2.server.toptop.entity.Product;
import com.cp2196g03g2.server.toptop.service.ICouponService;
import com.cp2196g03g2.server.toptop.service.IProductService;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/management/product")
@PreAuthorize("hasAnyAuthority('ROLE_SHOP_USER', 'ROLE_SUPERADMIN')")
public class ProductAdminController {
	
	@Autowired
	private IProductService productService;
	
	@GetMapping
	public PagableObject<Product> findAllByPage(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(value = "keyword", defaultValue = AppConstants.DEFAULT_KEYWORD, required = false) String keyword
    ){
		PagingRequest request = new PagingRequest(pageNo, pageSize, sortBy, sortDir, keyword);
		return productService.findAllByPage(request);
	}
	

	@PostMapping
	public Product saveProduct(@RequestBody ProductDto dto) {
		return productService.saveProduct(dto);
	}
	
	@PostMapping("/{id}")
	public void deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
	}
	
	
}
