package com.albedo.java.common.core.util;

import cn.hutool.core.util.ArrayUtil;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by somewhere on 2017/3/2.
 */
public class ResultBuilder {
	public static ResponseEntity<R> build(R customMessage) {
		return new ResponseEntity(customMessage, customMessage.getHttpStatus() == null ?
			HttpStatus.OK : customMessage.getHttpStatus());
	}

	public static ResponseEntity<R> buildOk(String... messages) {
		return new ResponseEntity(R.buildOk(messages), HttpStatus.OK);
	}

	public static ResponseEntity<R> buildOk(Object data, String... messages) {
		return new ResponseEntity(R.buildOkData(data, messages), HttpStatus.OK);
	}

	public static ResponseEntity<R> buildFail(String... messages) {
		return buildFail(null, messages);
	}

	public static ResponseEntity<R> buildFail(Object data, HttpStatus httpStatus, String... messages) {
		if (ArrayUtil.isEmpty(messages)) {
			messages = new String[]{"failed"};
		}
		R warn = R.buildFailData(data, messages);
		warn.setHttpStatus(httpStatus);
		return new ResponseEntity(warn, httpStatus != null ? httpStatus : HttpStatus.OK);

	}

	public static ResponseEntity<R> buildFail(HttpStatus httpStatus, String... messages) {

		return buildFail(null, httpStatus, messages);
	}

	public static ResponseEntity<R> buildFail(Object data, String... messages) {

		return buildFail(data, HttpStatus.OK, messages);
	}

	public static ResponseEntity<R> buildOkData(Object data) {
		String[] msg;
		if (data instanceof BindingResult) {
			List<String> errorsList = new ArrayList();
			BindingResult bindingResult = (BindingResult) data;
			errorsList.addAll(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()));
			data = null;
			msg = new String[errorsList.size()];
			msg = errorsList.toArray(msg);
		} else {
			msg = new String[0];
		}
		return buildOk(data, msg);
	}

	public static ResponseEntity<R> buildObject(Object data) {
		return new ResponseEntity(data, HttpStatus.OK);
	}

	public static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse) {
		return wrapOrNotFound(maybeResponse, null);
	}

	public static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse, HttpHeaders header) {
		return (ResponseEntity) maybeResponse.map((response) -> ResponseEntity.ok().headers(header).
			body(R.buildOkData(response))).orElse(new ResponseEntity(HttpStatus.NOT_FOUND));
	}

}
