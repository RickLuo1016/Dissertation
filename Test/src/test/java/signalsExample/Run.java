package signalsExample;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.av.robotaxi.fares.taxi.TaxiFareModule;
import org.matsim.contrib.av.robotaxi.fares.taxi.TaxiFaresConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpConfigConsistencyChecker;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.contrib.signals.builder.Signals.Configurator;
import org.matsim.contrib.signals.controller.laemmerFix.LaemmerSignalController;
import org.matsim.contrib.taxi.run.TaxiConfigConsistencyChecker;
import org.matsim.contrib.taxi.run.TaxiConfigGroup;
import org.matsim.contrib.taxi.run.TaxiModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

import dvrp.CAVModule;

public class Run {
	
	private static final String TAXIS_FILE = "C:/Reeply/Study/Dissertation/MATSim/Test/scenarios/test/taxis_1001.xml.gz";
	
	public static void main(String[] args) {
		Config config = ConfigUtils.loadConfig("scenarios/signals/config_simplified_robotaxi.xml", new DvrpConfigGroup(), new TaxiFaresConfigGroup(),
				new OTFVisConfigGroup(), new TaxiConfigGroup());
		
		Scenario scenario = CreateScenario.Create(config);		
		CreateSignals.Create(scenario);
		Controler controler = new Controler(scenario);

		/* the signals extensions works for planbased, sylvia and laemmer signal controller 
		 * by default and is pluggable for your own signal controller like this: */
		
		new Configurator(controler).addSignalControllerFactory(LaemmerSignalController.IDENTIFIER, LaemmerSignalController.LaemmerFactory.class);
        
		config.addConfigConsistencyChecker(new TaxiConfigConsistencyChecker());
		config.checkConsistency();
		String mode = TaxiConfigGroup.get(config).getMode();
		
		
		controler.addOverridingModule(new TaxiFareModule());
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(new TaxiModule());
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(mode));
		controler.addOverridingModule(new OTFVisLiveModule()); // OTFVis visualisation
		
        controler.run();
	}
}
