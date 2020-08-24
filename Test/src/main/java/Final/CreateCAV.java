package Final;

import org.matsim.contrib.dynagent.DynAgent;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.DvrpVehicleSpecification;
import org.matsim.contrib.dvrp.fleet.FleetWriter;
import org.matsim.contrib.dvrp.fleet.ImmutableDvrpVehicleSpecification;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentLogic;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreateCAV {
	
	public static void main(String[] args){
		
        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        int numberofVehicles = 100;
        
        double operationStartTime = 0.; //t0(00:00)
        double operationEndTime = 24 * 3600.;    //t1(24:00)
        
        int seats = 1;
        
        // input and output directory
        String networkfile = "scenarios/final/network.xml.gz";
        String taxisFile = "scenarios/final/taxis.xml";
        
        // create a list that contains only DvrpVehicleSpecification type
        List<DvrpVehicleSpecification> vehicles = new ArrayList<>();
        
        // a random number generator
        Random random = MatsimRandom.getLocalInstance();
        
        // read network file
        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkfile);
        
        // create a list that contains only ID(string) type
        List<Id<Link>> allLinks = new ArrayList<>();
        // get all the link IDs
        allLinks.addAll(scenario.getNetwork().getLinks().keySet());
        
        
        for (int i = 0; i < numberofVehicles; i++) {
        	
        	// randomly generate a start link which allow cars
            Link startLink;
            do {
                Id<Link> linkId = allLinks.get(random.nextInt(allLinks.size()));
                startLink = scenario.getNetwork().getLinks().get(linkId);
            }
            while (!startLink.getAllowedModes().contains(TransportMode.car));
            
            
            //for multi-modal networks: Only links where cars can ride should be used.
            DvrpVehicleSpecification v = ImmutableDvrpVehicleSpecification.newBuilder()
					.id(Id.create(Id.create("taxi_" + i, DvrpVehicle.class), DvrpVehicle.class))
                    .startLinkId(startLink.getId())
                    .capacity(seats)
                    .serviceBeginTime(operationStartTime)
                    .serviceEndTime(operationEndTime)
                    .build();
            
            vehicles.add(v);

        };
        
        // write 
        new FleetWriter(vehicles.stream()).write(taxisFile);
    }
}
