package com.cp2196g03g2.server.toptop.controller.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cp2196g03g2.server.toptop.dto.BooleanResult;
import com.cp2196g03g2.server.toptop.dto.ObjectKey;
import com.cp2196g03g2.server.toptop.dto.OtpRequestDto;
import com.cp2196g03g2.server.toptop.dto.ResetPasswordDto;
import com.cp2196g03g2.server.toptop.dto.UserDto;
import com.cp2196g03g2.server.toptop.entity.ApplicationUser;
import com.cp2196g03g2.server.toptop.entity.Role;
import com.cp2196g03g2.server.toptop.service.IUserService;
import com.cp2196g03g2.server.toptop.service.impl.EmailServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/account")
public class AccountController {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private EmailServiceImpl emailServiceImpl;
	
	@PostMapping
	public ApplicationUser saveUser(@RequestBody UserDto userDto, HttpServletRequest request) {
		ApplicationUser user = userService.saveCustomer(userDto);
		if(user != null) {
			HashMap<String, Object> model = new HashMap<>();
			model.put("otp", user.getOTP());
			emailServiceImpl.sendMail(user.getEmail(), "OTP Code", "otp-code", model);
		}
		return user;
	}
	
	@PutMapping("/otp")
	public ApplicationUser verifyOtp(@RequestBody OtpRequestDto otpRequestDto) {
		return userService.activeUserByOtpCode(otpRequestDto.getOtp(), otpRequestDto.getId());
	}
	
	@GetMapping("/alias")
	public BooleanResult existAlias(@RequestParam(value = "target", required = true) String alias, @RequestParam(value = "id", required = false) String id) {
		ObjectKey objectKey = new ObjectKey(alias, id);
		return new BooleanResult(userService.findByAlias(objectKey));
	}

	@GetMapping("/email")
	public BooleanResult existEmail(@RequestParam(value = "target", required = true) String email, @RequestParam(value = "id", required = false) String id) {
		ObjectKey objectKey = new ObjectKey(email, id);
		return new BooleanResult(userService.findByEmail(objectKey));
	}
	
	
	@GetMapping("/forgotPassword/{email}")
	public ApplicationUser forgotPassword(@PathVariable String email) {
		ApplicationUser user = userService.sendOtpCodeByEmail(email);
		if(user != null) {
			HashMap<String, Object> model = new HashMap<>();
			model.put("otp", user.getOTP());
			emailServiceImpl.sendMail(user.getEmail(), "OTP Code", "otp-code", model);
		}
		return user;
	}
	
	@PutMapping("/password/reset")
	public ApplicationUser resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
		return userService.resetPassword(resetPasswordDto);
	}
	
	
	@PostMapping("/social")
	public void loginSocial(@RequestBody UserDto userDto, HttpServletRequest request, HttpServletResponse response) {
			Calendar calendarAccessToken = Calendar.getInstance();
			Date nowAccessToken = calendarAccessToken.getTime();
			calendarAccessToken.add(Calendar.YEAR, 1);
			Date accessToken_expireDate = calendarAccessToken.getTime();

			Calendar calendarRefreshToken = Calendar.getInstance();
			Date nowRefreshToken = calendarRefreshToken.getTime();
			calendarRefreshToken.add(Calendar.YEAR, 5);
			Date refreshToken_expireDate = calendarRefreshToken.getTime();
			
			ApplicationUser user = userService.loginOrRegisterSocial(userDto);
			
			String[] role = {user.getRole().getName()};
			Algorithm algorithm = Algorithm.HMAC256("%hDWZP9zs7Upjs7$cZI#ZwKP8IW69$".getBytes());
			String access_token = JWT.create().withSubject(user.getEmail()).withExpiresAt(accessToken_expireDate)
					.withIssuedAt(nowAccessToken).withIssuer(request.getRequestURL().toString())
					.withArrayClaim("role", role)
					.withClaim("id", user.getId())
					.sign(algorithm);

			String refresh_token = JWT.create().withSubject(user.getEmail()).withExpiresAt(refreshToken_expireDate)
					.withIssuedAt(nowRefreshToken).withIssuer(request.getRequestURL().toString()).sign(algorithm);

			response.setHeader("access_token", access_token);
			response.setHeader("refresh_token", refresh_token);
			Map<String, String> tokens = new HashMap<>();
			tokens.put("access_token", access_token);
			tokens.put("refesh_token", refresh_token);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			try {
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}