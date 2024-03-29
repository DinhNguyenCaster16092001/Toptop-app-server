package com.cp2196g03g2.server.toptop.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cp2196g03g2.server.toptop.dto.CommentDto;
import com.cp2196g03g2.server.toptop.dto.LikeDto;
import com.cp2196g03g2.server.toptop.dto.PagableObject;
import com.cp2196g03g2.server.toptop.dto.PagingRequest;
import com.cp2196g03g2.server.toptop.entity.ApplicationUser;
import com.cp2196g03g2.server.toptop.entity.Comment;
import com.cp2196g03g2.server.toptop.entity.Notification;
import com.cp2196g03g2.server.toptop.entity.Video;
import com.cp2196g03g2.server.toptop.enums.NotificationType;
import com.cp2196g03g2.server.toptop.model.CommentModel;
import com.cp2196g03g2.server.toptop.repository.ICommentRepository;
import com.cp2196g03g2.server.toptop.repository.IUserRepository;
import com.cp2196g03g2.server.toptop.repository.IVideoRepository;
import com.cp2196g03g2.server.toptop.service.ICommentService;
import com.cp2196g03g2.server.toptop.service.INotificationService;

@Service
public class CommentServiceImpl implements ICommentService {

	@Autowired
	private ICommentRepository commentRepository;

	@Autowired
	private IVideoRepository videoRepository;

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private INotificationService notificationService;

	@Override
	@Transactional
	public Comment save(CommentDto commentDto) {
		Video video = videoRepository.findById(commentDto.getVideoId()).get();
		ApplicationUser user = userRepository.findById(commentDto.getUserId()).get();
		Comment comment = new Comment();
		comment.setParent(null);
		comment.setContent(commentDto.getContent());
		comment.setUser(user);
		comment.setVideo(video);
		Comment savedComment = commentRepository.save(comment);
		if (!ignoreOwnerVideo(user, video)) {
			Notification notification = new Notification(video.getUser(), user, comment.getContent(), video,
					savedComment, false, false, 1);
			notificationService.createNotification(notification);
		}

		return savedComment;
	}

	@Override
	public List<Comment> findAllParentCommentByVideoId(Long videoId) {
		return commentRepository.findAllParentCommentByVideoId(videoId);
	}

	@Override
	@Transactional
	public PagableObject<Comment> findChildrenCommentByParentId(Long parentId, PagingRequest request) {
		Sort sort = request.getSortDir().equalsIgnoreCase(Sort.Direction.ASC.name())
				? Sort.by(request.getSortBy()).ascending()
				: Sort.by(request.getSortBy()).descending();

		Pageable pageable = PageRequest.of(request.getPageNo(), request.getPageSize(), sort);

		Page<Comment> comments = commentRepository.findAllChilrendCommentByParentId(parentId, pageable);
		List<Comment> listOfComments = comments.getContent();

		PagableObject<Comment> commentPage = new PagableObject<>();
		commentPage.setData(listOfComments);
		commentPage.setPageNo(request.getPageNo());
		commentPage.setPageSize(request.getPageSize());
		commentPage.setTotalElements(comments.getTotalElements());
		commentPage.setTotalPages(comments.getTotalPages());
		commentPage.setLast(comments.isLast());

		return commentPage;
	}

	@Override
	public Comment replyComment(CommentDto commentDto) {
		// get video
		Video video = videoRepository.findById(commentDto.getVideoId()).get();

		// get comment parent
		Comment parentComment = commentRepository.findById(commentDto.getParentId()).get();

		// Get User Comment
		ApplicationUser user = userRepository.findById(commentDto.getUserId()).get();

		Comment childComment = new Comment(commentDto.getContent(), user, video, parentComment);

		// comment to database
		Comment savedCommet = commentRepository.save(childComment);
		if (savedCommet != null) {
			Set<String> userIds = getListUserId(parentComment, childComment);
			if (userIds.size() > 0) {
				for (String userTo : userIds) {
					Notification notification = new Notification(userRepository.findById(userTo).get(), user,
							childComment.getContent(), video, parentComment, false, false, 2);
					notification.setContent(childComment.getContent());
					notificationService.createNotification(notification);
				}
			}
		}

		return savedCommet;
	}

	private Set<String> getListUserId(Comment parentComment, Comment childComment) {
		List<Comment> chidrenComments = parentComment.getChildren();
		Set<String> set = new HashSet<>();
		if (chidrenComments.size() > 0) {
			set = chidrenComments.stream().flatMap(p -> Stream.of(p.getUser().getId())).collect(Collectors.toSet());
		}
		if (parentComment.getUser().getId() != childComment.getUser().getId()) {
			set.add(parentComment.getUser().getId());
		}

		if (parentComment.getVideo().getUser().getId() != childComment.getUser().getId()) {
			set.add(parentComment.getVideo().getUser().getId());
		}
		set.removeIf(x -> x.equals(childComment.getUser().getId()));
		return set;
	}

	@Override
	public PagableObject<Comment> findAllParentCommentByVideoId(Long videoId, PagingRequest request) {
		Sort sort = request.getSortDir().equalsIgnoreCase(Sort.Direction.ASC.name())
				? Sort.by(request.getSortBy()).ascending()
				: Sort.by(request.getSortBy()).descending();

		Pageable pageable = PageRequest.of(request.getPageNo(), request.getPageSize(), sort);

		Page<Comment> comments = commentRepository.findAllParentCommentByVideoId(videoId, pageable);
		List<Comment> listOfComments = comments.getContent();

		PagableObject<Comment> commentPage = new PagableObject<>();
		commentPage.setData(listOfComments);
		commentPage.setPageNo(request.getPageNo());
		commentPage.setPageSize(request.getPageSize());
		commentPage.setTotalElements(comments.getTotalElements());
		commentPage.setTotalPages(comments.getTotalPages());
		commentPage.setLast(comments.isLast());

		return commentPage;
	}

	private boolean ignoreOwnerComment(ApplicationUser user, Video comment) {
		return user.getId().equals(comment.getUser().getId());
	}

	private boolean ignoreOwnerVideo(ApplicationUser user, Video video) {
		return user.getId().equals(video.getUser().getId());
	}

	@Override
	public CommentModel findById(Long id) {
		Comment comment = commentRepository.findById(id).get();
		if (comment.getParent() != null) {
			return toModel(comment.getParent());
		}
		return toModel(comment);
	}

	private CommentModel toModel(Comment comment) {
		CommentModel model = new CommentModel();
		model.setId(comment.getId());
		model.setContent(comment.getContent());
		model.setCreatedDate(comment.getCreatedDate());
		model.setUser(comment.getUser());
		if (comment.getChildren().size() > 0)
			model.setChildren(comment.getChildren());
		return model;
	}

	@Override
	public PagableObject<Comment> findParentById(Long id) {
		Comment comment = commentRepository.findById(id).get();
		List<Comment> comments = new ArrayList<>();
		if(comment.getParent() != null)
			comments.add(comment.getParent());
		else
			comments.add(comment);		
		PagableObject<Comment> commentPage = new PagableObject<>();
		commentPage.setData(comments);
		commentPage.setPageNo(0);
		commentPage.setPageSize(1);
		commentPage.setTotalElements(1);
		commentPage.setTotalPages(1);
		commentPage.setLast(true);
		return commentPage;
		
	}
}
