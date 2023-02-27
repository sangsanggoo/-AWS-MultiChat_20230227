package dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class requestDto<T> {
	private String resource;
	private T body;
}
