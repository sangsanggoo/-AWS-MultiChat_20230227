package dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class responseDto<T> {
	private String resource;
	private T body;
}
