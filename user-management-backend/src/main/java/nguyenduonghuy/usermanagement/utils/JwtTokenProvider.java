package nguyenduonghuy.usermanagement.utils;

import static nguyenduonghuy.usermanagement.constant.SecurityConstant.ADMINISTRATION;
import static nguyenduonghuy.usermanagement.constant.SecurityConstant.AUTHORITIES;
import static nguyenduonghuy.usermanagement.constant.SecurityConstant.EXPIRATION_TIME;
import static nguyenduonghuy.usermanagement.constant.SecurityConstant.NGUYEN_DUONG_HUY;
import static nguyenduonghuy.usermanagement.constant.SecurityConstant.TOKEN_CANNOT_BE_VERIFIED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;

import nguyenduonghuy.usermanagement.domain.dto.UserPrincipal;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String secret;

	public String generateJwtToken(UserPrincipal userPrincipal) {
		Algorithm algorithm = Algorithm.HMAC512(secret.getBytes());
		String[] claims = this.getClaimsFromUser(userPrincipal);
		Date today = new Date();
		Date tokenExpiredDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
		return JWT.create()
				.withIssuer(NGUYEN_DUONG_HUY)
				.withAudience(ADMINISTRATION).withIssuedAt(today)
				.withSubject(userPrincipal.getUsername())
				.withArrayClaim(AUTHORITIES, claims)
				.withExpiresAt(tokenExpiredDate).sign(algorithm);
	}

	public List<GrantedAuthority> getAuthorities(String token) {
		String[] claims = getClaimsFromToken(token);
		return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	public Authentication getAuthentication(String user, List<GrantedAuthority> authorities, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken usernamePasswordAuthToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
		usernamePasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		return usernamePasswordAuthToken;
	}

	public String getSubject(String token) {
		JWTVerifier verifier = this.getJWTVerifier();
		return verifier.verify(token).getSubject();
	}

	public boolean isTokenValid(String username, String token) {
		JWTVerifier verifier = this.getJWTVerifier();
		return StringUtils.isNotEmpty(username) && isNotTokenExpired(verifier, token);
	}

	private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
		List<String> authorities = new ArrayList<>();
		userPrincipal.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));
		return authorities.toArray(new String[0]);
	}

	private String[] getClaimsFromToken(String token) {
		JWTVerifier verifier = this.getJWTVerifier();
		return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
	}

	private JWTVerifier getJWTVerifier() {
		JWTVerifier verifier;
		try {
			Algorithm algorithm = Algorithm.HMAC512(secret);
			verifier = JWT.require(algorithm).withIssuer(NGUYEN_DUONG_HUY).build();
		} catch (JWTVerificationException e) {
			throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
		}
		return verifier;
	}

	private boolean isNotTokenExpired(JWTVerifier verifier, String token) {
		Date tokenExpiredDate = verifier.verify(token).getExpiresAt();
		return tokenExpiredDate.after(new Date());
	}
}
