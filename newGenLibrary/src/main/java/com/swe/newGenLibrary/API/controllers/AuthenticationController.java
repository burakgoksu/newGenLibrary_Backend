package com.swe.newGenLibrary.API.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swe.newGenLibrary.business.concretes.UserDetailsManager;
import com.swe.newGenLibrary.core.utulitites.jwt.JwtUtil;
import com.swe.newGenLibrary.core.utulitites.result.DataResult;
import com.swe.newGenLibrary.core.utulitites.result.SuccessDataResult;
import com.swe.newGenLibrary.entities.AuthenticationRequest;
import com.swe.newGenLibrary.entities.AuthenticationResponse;

import io.jsonwebtoken.io.IOException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

	private AuthenticationManager authenticationManager;
	private UserDetailsManager userDetailsManager;
	private JwtUtil jwtUtil;
	
	@Autowired
	public AuthenticationController(AuthenticationManager authenticationManager,
			UserDetailsManager userDetailsManager,JwtUtil jwtUtil) {
		super();
		this.authenticationManager = authenticationManager;
		this.userDetailsManager = userDetailsManager;
		this.jwtUtil = jwtUtil;
	}
	
	@PostMapping("/login")
	public Mono<DataResult<AuthenticationResponse>> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {
	    return Mono.fromCallable(() -> {
	        try {
	            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
	        } catch (BadCredentialsException e) {
	            throw new BadCredentialsException("Incorrect Username or Password");
	        } catch (DisabledException disabledException) {
	            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User is not created. Register User First");
	            return null;
	        }
	        final UserDetails userDetails = userDetailsManager.loadUserByUsername(authenticationRequest.getEmail());
	        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
	        return new SuccessDataResult<>(new AuthenticationResponse(jwt), "Successfully login and JWT created successfully for 24 hours");
	    });
	}

	
	
}
