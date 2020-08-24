package test;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
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

import dvrp.*;

public class Run {
	
	private static final String CONFIG_FILE = "C:/Reeply/Study/Dissertation/MATSim/Test/scenarios/test/config_simplified.xml";
	private static final String TAXIS_FILE = "C:/Reeply/Study/Dissertation/MATSim/Test/scenarios/test/taxis_100.xml.gz";
	
	public static void main(String[] args){
		
//		Config config = ConfigUtils.loadConfig("scenarios/test/config.xml");
//		Scenario scenario = ScenarioUtils.loadScenario(config);
//		Controler controler = new Controler(scenario);
//		controler.run();
		
		Config config = ConfigUtils.loadConfig(CONFIG_FILE, new DvrpConfigGroup(), new OTFVisConfigGroup());
		config.controler().setLastIteration(2);
		config.addConfigConsistencyChecker(new DvrpConfigConsistencyChecker());
		config.checkConsistency();
		
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// setup controler
		Controler controler = new Controler(scenario);
		
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(new CAVModule(TAXIS_FILE));
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(TransportMode.taxi));

		controler.addOverridingModule(new OTFVisLiveModule()); // OTFVis visualisation
		
		// run simulation
		controler.run();
	}
}
