package dvrpExample1;


import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.examples.onetaxi.OneTaxiModule;
import org.matsim.contrib.dvrp.run.DvrpConfigConsistencyChecker;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

//import dvrpExample.*;
/**
 * @author michalm
 */

public final class RunOneTaxiExample {
	
	private static final String CONFIG_FILE = "./scenarios/test/one_taxi_config.xml";
	private static final String TAXIS_FILE = "one_taxi_vehicles.xml";

	public static void run(boolean otfvis, int lastIteration) {
		// load config
		Config config = ConfigUtils.loadConfig(CONFIG_FILE, new DvrpConfigGroup(), new OTFVisConfigGroup());
		config.controler().setLastIteration(lastIteration);
		config.addConfigConsistencyChecker(new DvrpConfigConsistencyChecker());
		config.checkConsistency();

//		// ---------------------------test------------------------------- //
//		System.out.println("1111111111");
//		System.out.println("2"+ConfigGroup.class);
//		
//		for (ConfigGroup configGroup : config.getModules().values()) {
//			System.out.println(configGroup.getClass()+ "   " + configGroup.getName());
//			
//		}
//		// System.out.println(config.getModules().values());
//		// ---------------------------test------------------------------- //
		

		
		// load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// setup controler
		Controler controler = new Controler(scenario);
		
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(new dvrpExample1.OneTaxiModule(TAXIS_FILE));
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(TransportMode.taxi));

		if (otfvis) {
			controler.addOverridingModule(new OTFVisLiveModule()); // OTFVis visualisation
		}
		
		// run simulation
		controler.run();
	}
	public static void main(String[] args) {
		
		run(true, 1); // switch to 'false' to turn off visualisation
	};
	
}