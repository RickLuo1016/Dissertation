package Final;


import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.av.robotaxi.fares.taxi.TaxiFareModule;
import org.matsim.contrib.av.robotaxi.fares.taxi.TaxiFaresConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.contrib.taxi.run.MultiModeTaxiConfigGroup;
import org.matsim.contrib.taxi.run.TaxiConfigConsistencyChecker;
import org.matsim.contrib.taxi.run.TaxiConfigGroup;
import org.matsim.contrib.taxi.run.TaxiModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

/**
 * This class runs an example robotaxi scenario including fares. The simulation runs for 10 iterations, this takes
 * quite a bit time (25 minutes or so). You may switch on OTFVis visualisation in the main method below. The scenario
 * should run out of the box without any additional files. If required, you may find all input files in the resource
 * path or in the jar maven has downloaded). There are two vehicle files: 2000 vehicles and 5000, which may be set in
 * the config. Different fleet sizes can be created using
 * {@link org.matsim.contrib.av.robotaxi.vehicles.CreateTaxiVehicles}
 */
public class Run {

	public static void main(String[] args) {
		//new CreateNetwork();
//		new CreatePlans();
//		new CreateCAV();
		String configFile = "scenarios/final/config_simplified_robotaxi.xml";
		Run.run(configFile, true);
	}

	public static void run(String configFile, boolean otfvis) {
		Config config = ConfigUtils.loadConfig(configFile, new DvrpConfigGroup(), new TaxiFaresConfigGroup(),
				new OTFVisConfigGroup(), new TaxiConfigGroup());
		createControler(config, otfvis).run();
	}

	public static Controler createControler(Config config, boolean otfvis) {
		config.addConfigConsistencyChecker(new TaxiConfigConsistencyChecker());
		config.checkConsistency();
		String mode = TaxiConfigGroup.get(config).getMode();

		Scenario scenario = ScenarioUtils.loadScenario(config);

		Controler controler = new Controler(scenario);
		controler.addOverridingModule(new TaxiFareModule());
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(new TaxiModule());
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(mode));

		if (otfvis) {
			controler.addOverridingModule(new OTFVisLiveModule());
		}

		return controler;
	}
}