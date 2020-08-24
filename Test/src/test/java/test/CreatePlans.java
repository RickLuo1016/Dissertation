package test;

import java.util.Random;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.contrib.dvrp.vrpagent.VrpLegFactory;
import org.matsim.contrib.dynagent.DynAgent;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;

public class CreatePlans {
	
	// maximum and minimum coordinates
	public static Coord coordSW;		// minimum x and y (south-west)
	public static Coord coordNE;		// maximum x and y (north-east)
	
	
	// set maximum and minimum coordinates
	private static void setMaxMinCoord(Network network){
		
		for (Id<Node> nodeId : network.getNodes().keySet()){
			Coord nodeCoord = network.getNodes().get(nodeId).getCoord();
			if(coordSW == null||coordNE == null){
				coordSW = nodeCoord;
				coordNE = nodeCoord;
				continue;
			};
			coordSW = new Coord(coordSW.getX()<nodeCoord.getX()?coordSW.getX():nodeCoord.getX(), 
			coordSW.getY()<nodeCoord.getY()?coordSW.getY():nodeCoord.getY());
			coordNE = new Coord(coordNE.getX()>nodeCoord.getX()?coordNE.getX():nodeCoord.getX(), 
			coordNE.getY()>nodeCoord.getY()?coordNE.getY():nodeCoord.getY());
		};
		
	};
	
	
	// get random coordinates
	private static Coord randomCoord(){

		double x, y;
		x = coordSW.getX()+(coordNE.getX() - coordSW.getX())*Math.random();
		y = coordSW.getY()+(coordNE.getY() - coordSW.getY())*Math.random();
		return new Coord(x, y);
	
	};
	
	
	// get random normally distributed time
	private static int randomTime(int normalTime, int variance){

		Random rand = new Random();
		return (int)(variance*rand.nextGaussian()+normalTime);
		
	};
	
	
	
	public static void main(String[] args){
		
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		
		// read network
		new MatsimNetworkReader(scenario.getNetwork()).readFile("scenarios/test/network1.xml.gz");
		
		// generate plans
		setMaxMinCoord(scenario.getNetwork());
		fillScenario(scenario);
		
		// write  plans
		new PopulationWriter(scenario.getPopulation()).write("scenarios/test/plans1.xml.gz");
	};
	
	
	// create agents(500)
	private static Population fillScenario(Scenario scenario) {
		
		Population population = scenario.getPopulation();
		for (int i = 0; i < 100; i++) {
			Coord coord = randomCoord();
			Coord coordWork = randomCoord();
			createOnePlan(scenario, population, i, coord, coordWork);
		}
		return population;
	};
	
	
	private static void createOnePlan(Scenario scenario, Population population, int i, Coord coordhome, Coord coordWork) {
	
		// for convenience
		PopulationFactory populationFactory = population.getFactory();
		
		Person person = populationFactory.createPerson(Id.createPersonId("person"+i));
		Plan plan = populationFactory.createPlan();
		
		Activity home1 = populationFactory.createActivityFromCoord("home", coordhome);
		home1.setEndTime(randomTime(8*60*60, 60*60));
		Leg towork = populationFactory.createLeg("taxi");
		
		plan.addActivity(home1);
		plan.addLeg(towork);
		
		Activity work = populationFactory.createActivityFromCoord("work", coordWork);
		work.setEndTime(randomTime(19*60*60, 3*60*60));
		Leg tohome = populationFactory.createLeg("taxi");
		
		plan.addActivity(work);
		plan.addLeg(tohome);
		
		Activity home2 = populationFactory.createActivityFromCoord("home", coordhome);
		plan.addActivity(home2);
		
		person.addPlan(plan);
		population.addPerson(person);
		
	};
	
	
}
