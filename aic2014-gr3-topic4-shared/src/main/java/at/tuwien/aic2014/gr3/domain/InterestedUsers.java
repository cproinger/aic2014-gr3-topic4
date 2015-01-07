package at.tuwien.aic2014.gr3.domain;

import java.util.List;

public class InterestedUsers {
	private List<UserAndCount> broadRange;
	private List<UserAndCount> focused;
	
	public InterestedUsers(List<UserAndCount> broadRange,
			List<UserAndCount> focused) {
		super();
		this.broadRange = broadRange;
		this.focused = focused;
	}

	public List<UserAndCount> getBroadRange() {
		return broadRange;
	}

	public List<UserAndCount> getFocused() {
		return focused;
	}

	@Override
	public String toString() {
		return "InterestedUsers [broadRange=" + broadRange + ", focused="
				+ focused + "]";
	}
}
