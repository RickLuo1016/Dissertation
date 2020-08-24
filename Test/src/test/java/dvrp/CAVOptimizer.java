package dvrp;

import org.matsim.contrib.dvrp.optimizer.Request;


import java.util.List;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.passenger.PassengerRequestAcceptedEvent;
import org.matsim.contrib.dvrp.passenger.PassengerRequestScheduledEvent;
import org.matsim.contrib.dvrp.path.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.path.VrpPaths;
import org.matsim.contrib.dvrp.router.DvrpRoutingNetworkProvider;
import org.matsim.contrib.dvrp.router.TimeAsTravelDisutility;
import org.matsim.contrib.dvrp.run.DvrpMode;
import org.matsim.contrib.dvrp.schedule.DriveTaskImpl;
import org.matsim.contrib.dvrp.schedule.Schedule;
import org.matsim.contrib.dvrp.schedule.Schedule.ScheduleStatus;
import org.matsim.contrib.dvrp.schedule.Schedules;
import org.matsim.contrib.dvrp.schedule.StayTask;
import org.matsim.contrib.dvrp.schedule.StayTaskImpl;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.router.DijkstraFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author michalm
 */
final class CAVOptimizer implements VrpOptimizer {
	private final EventsManager eventsManager;
	private final MobsimTimer timer;

	private final TravelTime travelTime;
	private final LeastCostPathCalculator router;

	private final DvrpVehicle vehicle;// we have only one vehicle
	
	private static final double PICKUP_DURATION = 120;
	private static final double DROPOFF_DURATION = 60;

	@Inject
	public CAVOptimizer(EventsManager eventsManager,
			@Named(DvrpRoutingNetworkProvider.DVRP_ROUTING) Network network, @DvrpMode(TransportMode.taxi) Fleet fleet,
			MobsimTimer timer) {
		
		this.eventsManager = eventsManager;
		this.timer = timer;
		travelTime = new FreeSpeedTravelTime();
		router = new DijkstraFactory().createPathCalculator(network, new TimeAsTravelDisutility(travelTime),
				travelTime);

		vehicle = fleet.getVehicles().values().iterator().next();
		vehicle.getSchedule()
				.addTask(new StayTaskImpl(vehicle.getServiceBeginTime(), vehicle.getServiceEndTime(),
						vehicle.getStartLink(), "wait"));
	}

	@Override
	public void requestSubmitted(Request request) {
		eventsManager.processEvent(
				new PassengerRequestAcceptedEvent(timer.getTimeOfDay(), TransportMode.taxi, request.getId()));

		Schedule schedule = vehicle.getSchedule();
		StayTask lastTask = (StayTask)Schedules.getLastTask(schedule);// only WaitTask possible here
		double currentTime = timer.getTimeOfDay();

		switch (lastTask.getStatus()) {
			case PLANNED:
				schedule.removeLastTask();// remove wait task
				break;

			case STARTED:
				lastTask.setEndTime(currentTime);// shorten wait task
				break;

			case PERFORMED:
			default:
				throw new IllegalStateException();
		}

		CAVRequest req = (CAVRequest)request;
		Link fromLink = req.getFromLink();
		Link toLink = req.getToLink();

		double t0 = schedule.getStatus() == ScheduleStatus.UNPLANNED ? //
				Math.max(vehicle.getServiceBeginTime(), currentTime) : //
				Schedules.getLastTask(schedule).getEndTime();

		VrpPathWithTravelData pathToCustomer = VrpPaths.calcAndCreatePath(lastTask.getLink(), fromLink, t0, router,
				travelTime);
		schedule.addTask(new DriveTaskImpl(pathToCustomer));

		double t1 = pathToCustomer.getArrivalTime();
		double t2 = t1 + PICKUP_DURATION;// 2 minutes for picking up the passenger
		schedule.addTask(new CAVServeTask(t1, t2, fromLink, true, req));

		VrpPathWithTravelData pathWithCustomer = VrpPaths.calcAndCreatePath(fromLink, toLink, t2, router, travelTime);
		schedule.addTask(new DriveTaskImpl(pathWithCustomer));

		double t3 = pathWithCustomer.getArrivalTime();
		double t4 = t3 + DROPOFF_DURATION;// 1 minute for dropping off the passenger
		schedule.addTask(new CAVServeTask(t3, t4, toLink, false, req));

		// just wait (and be ready) till the end of the vehicle's time window (T1)
		double tEnd = Math.max(t4, vehicle.getServiceEndTime());
		schedule.addTask(new StayTaskImpl(t4, tEnd, toLink, "wait"));

		eventsManager.processEvent(
				new PassengerRequestScheduledEvent(timer.getTimeOfDay(), TransportMode.taxi, request.getId(),
						vehicle.getId(), t1, t4));
	}

	@Override
	public void nextTask(DvrpVehicle vehicle1) {
		updateTimings();
		vehicle1.getSchedule().nextTask();
	}

	/**
	 * Simplified version. For something more advanced, see
	 * {@link org.matsim.contrib.taxi.scheduler.TaxiScheduler#updateBeforeNextTask(DvrpVehicle)} in the taxi contrib
	 */
	private void updateTimings() {
		Schedule schedule = vehicle.getSchedule();
		if (schedule.getStatus() != ScheduleStatus.STARTED) {
			return;
		}

		double now = timer.getTimeOfDay();
		Task currentTask = schedule.getCurrentTask();
		double diff = now - currentTask.getEndTime();

		if (diff == 0) {
			return;
		}

		currentTask.setEndTime(now);

		List<? extends Task> tasks = schedule.getTasks();
		int nextTaskIdx = currentTask.getTaskIdx() + 1;

		// all except the last task (waiting)
		for (int i = nextTaskIdx; i < tasks.size() - 1; i++) {
			Task task = tasks.get(i);
			task.setBeginTime(task.getBeginTime() + diff);
			task.setEndTime(task.getEndTime() + diff);
		}

		// wait task
		if (nextTaskIdx != tasks.size()) {
			Task waitTask = tasks.get(tasks.size() - 1);
			waitTask.setBeginTime(waitTask.getBeginTime() + diff);

			double tEnd = Math.max(waitTask.getBeginTime(), vehicle.getServiceEndTime());
			waitTask.setEndTime(tEnd);
		}
	}
}