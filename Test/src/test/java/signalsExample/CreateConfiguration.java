package signalsExample;

import org.matsim.contrib.signals.SignalSystemsConfigGroup;
import org.matsim.contrib.signals.data.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultSelector;

public class CreateConfiguration {
	
	public static void main(String[] args){
		Config config = ConfigUtils.loadConfig("scenarios/signals/config_simplified_robotaxi.xml");
		config.controler().setOutputDirectory("output/signals/");

		// config.travelTimeCalculator().setMaxTime(3600 * 5);
        // config.qsim().setUsingFastCapacityUpdate(false);
		
		SignalSystemsConfigGroup signalConfigGroup = ConfigUtils.addOrGetModule(config, SignalSystemsConfigGroup.GROUP_NAME, SignalSystemsConfigGroup.class);
		signalConfigGroup.setUseSignalSystems(true);

//		{
//			StrategySettings strat = new StrategySettings();
//			strat.setStrategyName(DefaultStrategy.ReRoute.toString());
//			strat.setWeight(0.1);
//			strat.setDisableAfter(config.controler().getLastIteration() - 95);
//			config.strategy().addStrategySettings(strat);
//		}
//		{
//			StrategySettings strat = new StrategySettings();
//			strat.setStrategyName(DefaultSelector.ChangeExpBeta.toString());
//			strat.setWeight(0.9);
//			strat.setDisableAfter(config.controler().getLastIteration());
//			config.strategy().addStrategySettings(strat);
//		}
		{
			StrategySettings strat = new StrategySettings();
			strat.setStrategyName(DefaultSelector.KeepLastSelected.toString());
			strat.setWeight(0.0);
			strat.setDisableAfter(config.controler().getLastIteration());
			config.strategy().addStrategySettings(strat);
		}

		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists) ;
		config.controler().setWriteEventsInterval(config.controler().getLastIteration());
		config.controler().setWritePlansInterval(config.controler().getLastIteration());
		config.vspExperimental().setWritingOutputEvents(true);
		config.planCalcScore().setWriteExperiencedPlans(true);
		config.controler().setCreateGraphs(true);

		// write config
		new ConfigWriter(config).write("scenarios/signals/config.xml");
	}
}
