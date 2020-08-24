package dvrp;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.schedule.StayTaskImpl;

/**
 * @author michalm
 */
public class CAVServeTask extends StayTaskImpl {
	private final CAVRequest request;
	private final boolean isPickup;// pickup or dropoff

	public CAVServeTask(double beginTime, double endTime, Link link, boolean isPickup, CAVRequest request) {
		super(beginTime, endTime, link, isPickup ? "pickup" : "dropoff");
		this.request = request;
		this.isPickup = isPickup;
	}

	public CAVRequest getRequest() {
		return request;
	}

	public boolean isPickup() {
		return isPickup;
	}
}
