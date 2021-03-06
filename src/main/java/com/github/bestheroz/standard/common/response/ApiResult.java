package com.github.bestheroz.standard.common.response;

import com.github.bestheroz.standard.common.exception.ExceptionCode;
import com.github.bestheroz.standard.common.util.MapperUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.util.Assert;

public class ApiResult extends HashMap<String, Object> {
  public static final String CODE_KEY = "code";
  public static final String MESSAGE_KEY = "message";
  public static final String DATA_KEY = "data";
  public static final String PAGINATION_TOTAL_LENGTH_KEY = "paginationTotalLength";
  private static final ApiResult SUCCESS_NORMAL = ApiResult.code(ExceptionCode.SUCCESS_NORMAL);
  private static final long serialVersionUID = 753116906067010122L;

  public static ApiResult ok() {
    return SUCCESS_NORMAL;
  }

  public static ApiResult ok(final Object data) {
    final ApiResult apiResult = ApiResult.code(ExceptionCode.SUCCESS_NORMAL);
    apiResult.put(DATA_KEY, data);
    return apiResult;
  }

  public static ApiResult ok(final Object data, final Long paginationTotalLength) {
    final ApiResult apiResult = ApiResult.code(ExceptionCode.SUCCESS_NORMAL);
    apiResult.put(DATA_KEY, data);
    apiResult.put(PAGINATION_TOTAL_LENGTH_KEY, paginationTotalLength);
    return apiResult;
  }

  public static ApiResult code(final ExceptionCode exceptionCode) {
    final ApiResult apiResult = new ApiResult();
    apiResult.put(CODE_KEY, exceptionCode.getCode());
    apiResult.put(MESSAGE_KEY, exceptionCode.getMessage());
    return apiResult;
  }

  public ApiResult add(final String key, final Object value) {
    Assert.hasText(key, "Parameter `key` must not be blank");
    Assert.notNull(value, "Parameter `value` must not be null");
    this.put(key, value);
    return this;
  }

  @Override
  public String toString() {
    final Map<String, Object> map = new HashMap<>();
    map.put(CODE_KEY, this.get(CODE_KEY));
    map.put(MESSAGE_KEY, this.get(MESSAGE_KEY));
    Optional.ofNullable(this.get(DATA_KEY)).ifPresent(item -> map.put(DATA_KEY, this.get(item)));
    Optional.ofNullable(this.get(PAGINATION_TOTAL_LENGTH_KEY))
        .ifPresent(item -> map.put(PAGINATION_TOTAL_LENGTH_KEY, this.get(item)));
    return MapperUtils.toString(map);
  }
}
