package dvrp;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.fleet.FleetModule;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.passenger.DefaultPassengerRequestValidator;
import org.matsim.contrib.dvrp.passenger.PassengerEngineQSimModule;
import org.matsim.contrib.dvrp.passenger.PassengerRequestCreator;
import org.matsim.contrib.dvrp.passenger.PassengerRequestValidator;
import org.matsim.contrib.dvrp.run.AbstractDvrpModeModule;
import org.matsim.contrib.dvrp.run.AbstractDvrpModeQSimModule;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentLogic;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentSourceQSimModule;
import org.matsim.contrib.dynagent.run.DynRoutingModule;


/**
 * @author Michal Maciejewski (michalm)
 */

public class CAVModule extends AbstractDvrpModeModule {
	private final String taxisFile;

	public CAVModule(String taxisFile) {
		
		super(TransportMode.taxi);
		this.taxisFile = taxisFile;
	}

	@Override
	public void install() {
		addRoutingModuleBinding(getMode()).toInstance(new DynRoutingModule(getMode()));
		install(new FleetModule(getMode(), taxisFile));
		bindModal(PassengerRequestValidator.class).to(DefaultPassengerRequestValidator.class);

		installQSimModule(new AbstractDvrpModeQSimModule(getMode()) {
			@Override
			protected void configureQSim() {
				install(new VrpAgentSourceQSimModule(getMode()));
				install(new PassengerEngineQSimModule(getMode()));

				// optimizer that dispatches taxis
				System.out.println(CAVOptimizer.class);
				bindModal(VrpOptimizer.class).to(CAVOptimizer.class).asEagerSingleton();
				
				// converts departures of the "taxi" mode into taxi requests
				bindModal(PassengerRequestCreator.class).to(CAVRequest.CAVRequestCreator.class)
						.asEagerSingleton();
				
				// converts scheduled tasks into simulated actions (legs and activities)
				bindModal(VrpAgentLogic.DynActionCreator.class).to(CAVAction.class).asEagerSingleton();
			}
		});
	}
	
}
