package com.cp2196g03g2.server.toptop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cp2196g03g2.server.toptop.entity.ApplicationUser;
import com.cp2196g03g2.server.toptop.entity.Video;

public interface IVideoRepository extends JpaRepository<Video, Long> {
	
	@Query("SELECT v FROM Video v LEFT JOIN v.hashTags h WHERE (v.title LIKE %:keyword% OR " + 
															   "v.user.alias LIKE %:keyword% OR "  +
															   "h.name LIKE %:keyword%) " +
															   "AND v.professed = 1 AND v.status = 1 " +
															   "GROUP BY v.title, v.id")
	Page<Video> findAllVideoByPage(Pageable pageable, @Param("keyword")String keyword);

	@Query("SELECT v FROM Video v WHERE v.user.id=:userId AND v.professed =:professed AND v.status = 1")
	Page<Video> findByUserAndProfessed(@Param("userId") String userId,@Param("professed")boolean professed,Pageable pageable);
	
	@Query("SELECT SUM(v.view) FROM Video v WHERE v.user.id =:userid")
	Long countViewByUserId(@Param("userid") String userid);
	
	@Query("SELECT SUM(v.heart) FROM Video v WHERE v.user.id =:userid")
	Long countHeartByUserId(@Param("userid") String userid);
	
}
