package dvrpExample1;
//package dvrpExample;
//
//import org.matsim.api.core.v01.Scenario;
//import org.matsim.contrib.dvrp.MatsimVrpContextImpl;
//import org.matsim.contrib.dvrp.data.VrpData;
//import org.matsim.contrib.dvrp.extensions.taxi.TaxiUtils;
//import org.matsim.contrib.dvrp.passenger.PassengerEngine;
//import org.matsim.contrib.dvrp.run.VrpLauncherUtils;
//import org.matsim.contrib.dynagent.run.DynAgentLauncherUtils;
//import org.matsim.core.api.experimental.events.EventsManager;
//import org.matsim.core.mobsim.qsim.QSim;
//import org.matsim.vis.otfvis.OTFVisConfigGroup.ColoringScheme;
//
//
//public class RunOneTaxiExample070
//{
//    private final String dir;
//    private final String netFile;
//    private final String plansFile;
//    private final String vehiclesFile;
//    private final boolean otfVis;
//
//
//    public RunOneTaxiExample070(boolean otfVis)
//    {
//        this.otfVis = otfVis;
//
//        dir = "./src/main/resources/";
//        netFile = dir + "grid_network.xml";
//        plansFile = dir + "one_taxi/one_taxi_population.xml";
//        vehiclesFile = dir + "one_taxi/one_taxi_vehicles.xml";
//    }
//
//
//    public void go()
//    {
//        MatsimVrpContextImpl context = new MatsimVrpContextImpl();
//
//        Scenario scenario = VrpLauncherUtils.initScenario(netFile, plansFile);
//        context.setScenario(scenario);
//
//        VrpData vrpData = VrpLauncherUtils.initVrpData(context, vehiclesFile);
//        context.setVrpData(vrpData);
//
//        OneTaxiOptimizer optimizer = new OneTaxiOptimizer(context);
//
//        QSim qSim = DynAgentLauncherUtils.initQSim(scenario);
//        context.setMobsimTimer(qSim.getSimTimer());
//
//        PassengerEngine passengerEngine = VrpLauncherUtils.initPassengerEngine(TaxiUtils.TAXI_MODE,
//                new OneTaxiRequestCreator(), optimizer, context, qSim);
//
//        VrpLauncherUtils.initAgentSources(qSim, context, optimizer,
//                new OneTaxiActionCreator(passengerEngine, qSim.getSimTimer()));
//
//        EventsManager events = qSim.getEventsManager();
//
//        if (otfVis) { // OFTVis visualization
//            DynAgentLauncherUtils.runOTFVis(qSim, true, ColoringScheme.taxicab);
//        }
//
//        qSim.run();
//        events.finishProcessing();
//    }
//
//
//    public static void main(String... args)
//    {
//        new RunOneTaxiExample070(false).go();
//    }
//}