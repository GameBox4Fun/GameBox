package top.naccl.exception;

/**
 * @Description: 404异常
 * @Author: Naccl
 * @Date: 2020-10-16
 */
public class NotFoundException extends RuntimeException {
	public NotFoundException() {
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
