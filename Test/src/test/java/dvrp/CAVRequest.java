package dvrp;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.contrib.dvrp.passenger.PassengerRequest;
import org.matsim.contrib.dvrp.passenger.PassengerRequestCreator;
import org.matsim.core.mobsim.framework.MobsimPassengerAgent;

/**
 * @author michalm
 */
public final class CAVRequest implements PassengerRequest {
	private final Id<Request> id;
	private final double submissionTime;
	private final double earliestStartTime;

	private final Id<Person> passengerId;
	private final String mode;

	private final Link fromLink;
	private final Link toLink;

	public CAVRequest(Id<Request> id, Id<Person> passengerId, String mode, Link fromLink, Link toLink,
			double departureTime, double submissionTime) {
		this.id = id;
		this.submissionTime = submissionTime;
		this.earliestStartTime = departureTime;
		this.passengerId = passengerId;
		this.mode = mode;
		this.fromLink = fromLink;
		this.toLink = toLink;
	}

	@Override
	public Id<Request> getId() {
		return id;
	}

	@Override
	public double getSubmissionTime() {
		return submissionTime;
	}

	@Override
	public double getEarliestStartTime() {
		return earliestStartTime;
	}

	@Override
	public Link getFromLink() {
		return fromLink;
	}

	@Override
	public Link getToLink() {
		return toLink;
	}

	@Override
	public Id<Person> getPassengerId() {
		return passengerId;
	}

	@Override
	public String getMode() {
		return mode;
	}

	@Override
	public void setRejected(boolean rejected) {
		throw new UnsupportedOperationException();
	}

	static final class CAVRequestCreator implements PassengerRequestCreator {
		@Override
		public CAVRequest createRequest(Id<Request> id, MobsimPassengerAgent passenger, Link fromLink, Link toLink,
				double departureTime, double submissionTime) {
			return new CAVRequest(id, passenger.getId(), TransportMode.taxi, fromLink, toLink, departureTime,
					submissionTime);
		}
	}
}
