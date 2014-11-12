package at.tuwien.aic2014.gr3.domain;

public class StatusRange {

	private long userId;
	
	private long fromStatusId;
	
	private long toStatusId;

	public StatusRange(long userId, long fromStatusId, long toStatusId) {
		super();
		this.userId = userId;
		this.fromStatusId = fromStatusId;
		this.toStatusId = toStatusId;
	}

	public long getUserId() {
		return userId;
	}

	public long getFromStatusId() {
		return fromStatusId;
	}

	public long getToStatusId() {
		return toStatusId;
	}

	@Override
	public String toString() {
		return "StatusRange [userId=" + userId + ", fromStatusId="
				+ fromStatusId + ", toStatusId=" + toStatusId + "]";
	}
	
	
}
