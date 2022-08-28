package org.landister.vampire.backend.model.response;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

// Note make sure that all constructors have the type="type" in them
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {

  protected String type;


  public BaseResponse() {
    type="invalid";
  }

  public BaseResponse(String type) {
    this.type = type;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public BaseResponse type(String type) {
    setType(type);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BaseResponse)) {
            return false;
        }
        BaseResponse baseResponse = (BaseResponse) o;
        return Objects.equals(type, baseResponse.type);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type);
  }

  @Override
  public String toString() {
    return "{" +
      " type='" + getType() + "'" +
      "}";
  }

  
}
