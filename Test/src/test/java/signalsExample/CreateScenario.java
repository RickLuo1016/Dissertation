package signalsExample;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.signals.SignalSystemsConfigGroup;
import org.matsim.contrib.signals.data.SignalsData;
import org.matsim.contrib.signals.data.SignalsDataLoader;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

public class CreateScenario {
	
	protected static Scenario Create(Config config) {
		Scenario scenario = ScenarioUtils.loadScenario(config);
		// add missing scenario elements
		ConfigUtils.addOrGetModule(config, SignalSystemsConfigGroup.GROUP_NAME, SignalSystemsConfigGroup.class);
		scenario.addScenarioElement(SignalsData.ELEMENT_NAME, new SignalsDataLoader(config).loadSignalsData());

		// createNetwork(scenario);
		// createPopulation(scenario);
		// createSignals(scenario);
		return scenario;
	}
	
	public static void main(String[] args) {
		Config config = ConfigUtils.loadConfig("scenarios/signals/config.xml");
		CreateScenario.Create(config);
	}
}
