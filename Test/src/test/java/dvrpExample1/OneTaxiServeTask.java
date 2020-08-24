package dvrpExample1;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.schedule.StayTaskImpl;

/**
 * @author michalm
 */
public class OneTaxiServeTask extends StayTaskImpl {
	private final OneTaxiRequest request;
	private final boolean isPickup;// pickup or dropoff

	public OneTaxiServeTask(double beginTime, double endTime, Link link, boolean isPickup, OneTaxiRequest request) {
		super(beginTime, endTime, link, isPickup ? "pickup" : "dropoff");
		this.request = request;
		this.isPickup = isPickup;
	}

	public OneTaxiRequest getRequest() {
		return request;
	}

	public boolean isPickup() {
		return isPickup;
	}
}
