package Final;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;
import org.matsim.core.config.groups.QSimConfigGroup.StarttimeInterpretation;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

public class CreateConfiguration {
	public static void main(String[] args){
		
		Config config = ConfigUtils.loadConfig("C:/Reeply/Study/Dissertation/MATSim/Test/scenarios/test/emptyconfig.xml");
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);
		
		
		// set input files
		config.network().setInputFile("network.xml.gz");
		config.plans().setInputFile("plans.xml.gz");
		//config.vehicles().setVehiclesFile("taxis_1000.xml.gz");
		
		// add "home" and "work"
		ActivityParams home = new ActivityParams();
		home.setActivityType("home");
		home.setTypicalDuration(3600*8);

		ActivityParams work = new ActivityParams();
		work.setActivityType("work");
		work.setTypicalDuration(3600*8);
		
		// add activity
		config.planCalcScore().addActivityParams(home);
		config.planCalcScore().addActivityParams(work);
		
		// output options
		config.controler().setOutputDirectory("output/test");
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists );
	
		// visualisation
		config.addModule(new OTFVisConfigGroup());
		
		// add legmode
		ModeParams taxi = new ModeParams("taxi");
		config.planCalcScore().addModeParams(taxi);
		
		// qsim
		config.qsim().setSimStarttimeInterpretation(StarttimeInterpretation.onlyUseStarttime);
		
		// strategy
		StrategySettings stratSets = new StrategySettings();
		stratSets.setStrategyName("ReRoute");
		stratSets.setWeight(0.1);
		config.strategy().addStrategySettings(stratSets);
		config.strategy().setFractionOfIterationsToDisableInnovation(0.9);
		config.strategy().setMaxAgentPlanMemorySize(3);		
		
		//  iteration
		config.controler().setLastIteration(1);
		
		// write config
		new ConfigWriter(config).write("scenarios/test/config.xml");
	};
}
