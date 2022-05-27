package nguyenduonghuy.usermanagement.filter;

import static nguyenduonghuy.usermanagement.constant.SecurityConstant.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import nguyenduonghuy.usermanagement.utils.JwtTokenProvider;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
			response.setStatus(HttpStatus.OK.value());
		} else {
			String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
				filterChain.doFilter(request, response);
				return;
			}
				String token = authorizationHeader.substring(TOKEN_PREFIX.length());
				String username = jwtTokenProvider.getSubject(token);
				if (jwtTokenProvider.isTokenValid(username, token) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
					List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
					Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} else {
					SecurityContextHolder.clearContext();
				}
			filterChain.doFilter(request, response);
		}
	}
}
