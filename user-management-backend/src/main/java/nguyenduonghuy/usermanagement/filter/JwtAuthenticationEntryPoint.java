package nguyenduonghuy.usermanagement.filter;

import static nguyenduonghuy.usermanagement.constant.SecurityConstant.FORBIDEN_MESSAGE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import nguyenduonghuy.usermanagement.domain.dto.response.HttpResponse;

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
		HttpResponse httpResponse = this.setHttpResponse(FORBIDDEN, FORBIDEN_MESSAGE);
		response.setHeader("error", exception.getMessage());
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setStatus(FORBIDDEN.value());
		OutputStream outputStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream, httpResponse);
		outputStream.flush();
	}

	private HttpResponse setHttpResponse(HttpStatus httpStatus, String message) {
		return HttpResponse.builder()
							.timeStamp(LocalDateTime.now())
							.httpStatusCode(httpStatus.value())
							.httpStatus(httpStatus)
							.reason(httpStatus.getReasonPhrase().toUpperCase())
							.message(message)
							.build();
	}
}