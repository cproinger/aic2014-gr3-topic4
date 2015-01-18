package at.tuwien.aic2014.gr3.domain;

import java.util.Map;

public class UserTopic {

	private String topic;
	private int cnt;

	public UserTopic(Map<String, Object> r) {
		this.topic = (String) r.get("to");
		this.cnt = (int) r.get("cnt");
	}
	
	public String getTopic() {
		return topic;
	}
	public int getCnt() {
		return cnt;
	}

	@Override
	public String toString() {
		return "UserTopic [topic=" + topic + ", cnt=" + cnt + "]";
	}
}