package cn.aimengmeng.tool.pdf.dto;

public class ResultCode {
	private long code;
	private String errMsg;

	public ResultCode(){}
	
	public ResultCode(long code, String errMsg) {
		super();
		this.code = code;
		this.errMsg = errMsg;
	}
	
	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	@Override
	public String toString() {
		return "ResultCode [code=" + code + ", errMsg=" + errMsg + "]";
	}
	
}
