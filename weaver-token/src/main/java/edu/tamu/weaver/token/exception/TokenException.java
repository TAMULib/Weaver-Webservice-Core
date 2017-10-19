package edu.tamu.weaver.token.exception;

/**
 * Token exception
 */
public abstract class TokenException extends RuntimeException {

    private static final long serialVersionUID = -7747574311780830482L;

    private String errCode;
    private String errMsg;

    public TokenException(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    /**
     * Get error code.
     * 
     * @return String
     */
    public String getErrCode() {
        return errCode;
    }

    /**
     * Set error code.
     * 
     * @param errCode
     *            String
     */
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    /**
     * Get error message.
     * 
     * @return String
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * Set error message.
     * 
     * @param errMsg
     *            String
     */
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

}
