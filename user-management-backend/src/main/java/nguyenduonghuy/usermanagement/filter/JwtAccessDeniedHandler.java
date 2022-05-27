package nguyenduonghuy.usermanagement.filter;

import static nguyenduonghuy.usermanagement.constant.SecurityConstant.ACCESS_DENIED_MESSAGE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import nguyenduonghuy.usermanagement.domain.dto.response.HttpResponse;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
		HttpResponse httpResponse = this.setHttpResponse(UNAUTHORIZED, ACCESS_DENIED_MESSAGE);
		response.setHeader("error", exception.getMessage());
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setStatus(UNAUTHORIZED.value());
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