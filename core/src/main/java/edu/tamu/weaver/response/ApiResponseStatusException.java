package edu.tamu.weaver.response;

public class ApiResponseStatusException extends RuntimeException {

  private int status;

  public ApiResponseStatusException(String message, int status) {
    super(message);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

}
