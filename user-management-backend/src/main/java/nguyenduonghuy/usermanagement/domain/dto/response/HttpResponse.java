package nguyenduonghuy.usermanagement.domain.dto.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING,
				pattern = "dd-MM-yyyy hh:mm:ss",
				timezone = "Asia/Ho_Chi_Minh")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime timeStamp;
	private int httpStatusCode;
	private HttpStatus httpStatus;
	private String reason;
	private String message;
}