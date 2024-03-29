package com.cp2196g03g2.server.toptop.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cp2196g03g2.server.toptop.constant.AppConstants;
import com.cp2196g03g2.server.toptop.dto.BooleanResult;
import com.cp2196g03g2.server.toptop.dto.HeartDto;
import com.cp2196g03g2.server.toptop.dto.PagableObject;
import com.cp2196g03g2.server.toptop.dto.PagingRequest;
import com.cp2196g03g2.server.toptop.dto.UpdateVideoDto;
import com.cp2196g03g2.server.toptop.dto.VideoDto;
import com.cp2196g03g2.server.toptop.entity.ApplicationUser;
import com.cp2196g03g2.server.toptop.entity.Video;
import com.cp2196g03g2.server.toptop.service.IHashTagService;
import com.cp2196g03g2.server.toptop.service.IVideoService;
@CrossOrigin
@RestController
@RequestMapping("/api/v1/video")
public class VideoController {
	
	@Autowired
	private IVideoService videoService;
	

	
	@GetMapping("/watch")
	public PagableObject<Video> findAllByPage(
			@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
			@RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
			@RequestParam(value = "keyword", defaultValue = AppConstants.DEFAULT_KEYWORD, required = false) String keyword,
			@RequestParam(value = "active", defaultValue = AppConstants.DEFAULT_STATUS, required = false) int status) {
		PagingRequest request = new PagingRequest(pageNo, pageSize, sortBy, sortDir, keyword);
		return videoService.findAllByPage(request);
	}
	
	@GetMapping("/favourite")
	public PagableObject<Video> findAllByPage(
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
			@RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
		PagingRequest request = new PagingRequest(pageNo, pageSize, sortBy, sortDir);
		return videoService.findFavouriteVideoByPage(request, userId);
	}
	

	@GetMapping("/watch/{id}")
	public Video findVideoById(@PathVariable Long id) {
		return videoService.findById(id);
	}
	
	
	@GetMapping("/watch/user/{id}")
	public PagableObject<Video> findVideoByUserId(@PathVariable String id,
			@RequestParam(value = "professed",required = false) boolean professed,
			@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
			@RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
		PagingRequest request = new PagingRequest(pageNo, pageSize, sortBy, sortDir);
		return videoService.findVideoByUserId(request, id, professed);
	}

	
	@PutMapping("/watch/{id}")
	public Video updateViewVideo(@PathVariable Long id) {
		return videoService.updateViewVideo(id);
	}
	
	@PutMapping("/heart")
	public Video updateHeartVideo(@RequestBody HeartDto dto) {
		return videoService.updateHeartVideo(dto);
	}
	
	
	@GetMapping("/heart/{videoId}/user/{userId}")
	public boolean updateHeartVideo(@PathVariable Long videoId, @PathVariable  String userId) {
		return videoService.isUserHeartBefore(videoId, userId);
	}
	
		
	
	@PutMapping("/share/{id}")
	public Video updateHeartVideo(@PathVariable Long id) {
		return videoService.updateShareVideo(id);
	}
	
	@PutMapping("/{id}")
	public Video updateVideo(@PathVariable Long id , @RequestBody UpdateVideoDto dto) {
		dto.setId(id);
		return videoService.updateEnableCommentProfessedVideo(dto);
	}
	
	@PostMapping("/interactive")
	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_SHOP_USER')")
	public Video createVideo(@RequestBody VideoDto videoDto) {
		return videoService.save(videoDto);
	}
	
	
	@DeleteMapping("/{id}")
	public void deleteVideoById(@PathVariable Long id) {
		videoService.deleteVideoById(id);
	}
	
}
