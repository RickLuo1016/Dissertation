package Final;

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
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.ScenarioUtils;

public class CreatePlans {
	// All(wgs) North 22.6457 South 22.4472 West 113.8609 East 114.3416
		// Nanshan North 22.6457 South 22.4472 West 113.8609 East 113.9811
		// Futian North 22.6457 South 22.4472 West 113.9811 East 114.0999
		// Luohu North 22.6457 South 22.4472 West 114.0999 East 114.2104
		// Yantian North 22.6457 South 22.4472 West 114.2104 East 114.3416
	
	// All (Xian80) SW-X,Y-38485681.69, 2483328.21 NE-X,Y-38535112.60, 2505343.32
		// Nanshan SW-X,Y-38485681.69, 2483328.21 NE-X,Y-38498057.30, 2505303.14
		// Futian SW-X,Y-38498054.52, 2483321.69 NE-X,Y-38510268.55, 2505306.46
		// Luohu SW-X,Y-38510283.24, 2483324.99 NE-X,Y-38521626.67, 2505318.31
		// Yantian SW-X,Y-38521657.62, 2483336.75 NE-X,Y-38535112.60, 2505343.32
	
	private static Coord coordSW_Nanshan = new Coord(38485681.69, 2483328.21);
	private static Coord coordNE_Nanshan = new Coord(38498057.30, 2505303.14);
	private static double pop_Nanshan = 0.34;
	
	private static Coord coordSW_Futian = new Coord(38498054.52, 2483321.69);
	private static Coord coordNE_Futian = new Coord(38510268.55, 2505306.46);
	private static double pop_Futian = 0.37;
	
	private static Coord coordSW_Luohu = new Coord(38510283.24, 2483324.99);
	private static Coord coordNE_Luohu = new Coord(38521626.67, 2505318.31);
	private static double pop_Luohu = 0.23;
	
	private static Coord coordSW_Yantian= new Coord(38521657.62, 2483336.75);
	private static Coord coordNE_Yantian = new Coord(38535112.60, 2505343.32);
	private static double pop_Yantian = 0.06;
	
	// maximum and minimum coordinates
	private static Coord coordSW;		// minimum x and y (south-west)
	private static Coord coordNE;		// maximum x and y (north-east)
	

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
	
	private static Coord randomCoord_Nanshan() {
		double x, y;
		x = coordSW_Nanshan.getX()+(coordNE_Nanshan.getX() - coordSW_Nanshan.getX())*Math.random();
		y = coordSW_Nanshan.getY()+(coordNE_Nanshan.getY() - coordSW_Nanshan.getY())*Math.random();
		return new Coord(x, y);
	
	}
	
	private static Coord randomCoord_Futian() {
		double x, y;
		x = coordSW_Futian.getX()+(coordNE_Futian.getX() - coordSW_Futian.getX())*Math.random();
		y = coordSW_Futian.getY()+(coordNE_Futian.getY() - coordSW_Futian.getY())*Math.random();
		return new Coord(x, y);
	
	}
	
	
	private static Coord randomCoord_Luohu() {
		double x, y;
		x = coordSW_Luohu.getX()+(coordNE_Luohu.getX() - coordSW_Luohu.getX())*Math.random();
		y = coordSW_Luohu.getY()+(coordNE_Luohu.getY() - coordSW_Luohu.getY())*Math.random();
		return new Coord(x, y);
	
	}
	
	private static Coord randomCoord_Yantian() {
		double x, y;
		x = coordSW_Yantian.getX()+(coordNE_Yantian.getX() - coordSW_Yantian.getX())*Math.random();
		y = coordSW_Yantian.getY()+(coordNE_Yantian.getY() - coordSW_Yantian.getY())*Math.random();
		return new Coord(x, y);
	
	}
	
	
	// get random normally distributed time
	private static int randomTime(int normalTime, int variance){

		Random rand = new Random();
		return (int)(variance*rand.nextGaussian()+normalTime);
		
	};
	
	
	
	public static void main(String[] args){
		
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		
		// read network
		new MatsimNetworkReader(scenario.getNetwork()).readFile("scenarios/final/network.xml.gz");
		
		// generate plans
		setMaxMinCoord(scenario.getNetwork());
		fillScenario(scenario,scenario.getNetwork(),totalPopulation);
		
		// write  plans
		new PopulationWriter(scenario.getPopulation()).write("scenarios/final/plans.xml.gz");
	};
	
	
	// create agents
	private static Population fillScenario(Scenario scenario, Network network, int n) {
		Population population = scenario.getPopulation();
		
		// Nanshan
		int i = 0 ;
		int j = 0 ;
		while(i < n * pop_Nanshan) {
			Coord coordHome= randomCoord_Nanshan();
			Coord coordWork = randomCoord();
			if(NetworkUtils.getNearestLink(network,coordHome)!= null && NetworkUtils.getNearestLink(network,coordWork)!= null) {
				j++;
				createOnePlan(scenario, population, j, coordHome, coordWork, 0.5688);
				i++;
				}
			else {
				continue;
			}
		}
		
		// Futian
		i = 0 ;
		while(i < n * pop_Futian) {
			Coord coordHome= randomCoord_Futian();
			Coord coordWork = randomCoord();
			if(NetworkUtils.getNearestLink(network,coordHome)!= null && NetworkUtils.getNearestLink(network,coordWork)!= null) {
				j++;
				createOnePlan(scenario, population, j, coordHome, coordWork, 0.5266);
				i++;
				}
			else {
				continue;
			}
		}
		
		// Luohu
		i = 0 ;
		while(i < n * pop_Luohu) {
			Coord coordHome= randomCoord_Luohu();
			Coord coordWork = randomCoord();
			if(NetworkUtils.getNearestLink(network,coordHome)!= null && NetworkUtils.getNearestLink(network,coordWork)!= null) {
				j++;
				createOnePlan(scenario, population, j, coordHome, coordWork, 0.3642);
				i++;
				}
			else {
				continue;
			}
		}
		
		// Yantian
		i = 0 ;
		while(i < n * pop_Yantian) {
			Coord coordHome= randomCoord_Yantian();
			Coord coordWork = randomCoord();
			if(NetworkUtils.getNearestLink(network,coordHome)!= null && NetworkUtils.getNearestLink(network,coordWork)!= null) {
				j++;
				createOnePlan(scenario, population, j, coordHome, coordWork, 0.2410);
				i++;
				}
			else {
				continue;
			}
		}
		
//		for (int i = 0; i < 100; i++) {
//			Coord coordHome = randomCoord();
//			Coord coordWork = randomCoord();
//			createOnePlan(scenario, population, i, coordHome, coordWork);
//		}
		
		return population;
	};
	
	
	
	private static void randomAction(Plan plan, Leg LegMode, double Probability, Coord home, Coord work, boolean Employed, Network network) {

		double R = Math.random();
		double P = Math.random();
		Coord Random = null;
		
		//System.out.println("1");
		if (R < Probability) {
			//System.out.println("2");
			while (true) {
				Random = randomCoord();
				if(NetworkUtils.getNearestLink(network,Random)!= null) {break;};
				};
			Activity RandomAct1 = PopulationUtils.createActivityFromCoord("random", Random);
			RandomAct1.setMaximumDuration(60*60);
			RandomAct1.setEndTime(randomTime(17*60*60+60*58, 3*60*60));
			
			Activity RandomAct2 = PopulationUtils.createActivityFromCoord("home", home);
			RandomAct2.setEndTime(randomTime(7*60*60+60*58, 60*60));
			
			Activity RandomAct3 = PopulationUtils.createActivityFromCoord("work", work);
			RandomAct3.setEndTime(randomTime(16*60*60+48*60, 60*60));
			
			if(Employed) {
				if(P > 0.5)	{
					PopulationUtils.insertLegAct(plan, 3, LegMode, RandomAct1);
					PopulationUtils.insertLegAct(plan, 5, LegMode, RandomAct3);
				}
				else {
					PopulationUtils.insertLegAct(plan, 1, LegMode, RandomAct1);
					PopulationUtils.insertLegAct(plan, 3, LegMode, RandomAct2);
					
				};
			}
			else {
				plan.addLeg(LegMode);
				plan.addActivity(RandomAct1);
				plan.addLeg(LegMode);
				plan.addActivity(RandomAct2);
			};
		}
		else{
			//System.out.println("2");
		};
	}
	
	// 0.55,0.83,0.92,0.97
	private static double MPR = 0.97;
	private static int totalPopulation = 882;
	private static void createOnePlan(Scenario scenario, Population population, int i, Coord coordHome, Coord coordWork, double Employment) {
	
		
		double Employed = Math.random();
		double RandomMPR = Math.random();
		double RandomAct = 0.3;
		PopulationFactory populationFactory = population.getFactory();
		
		
		if (Employed < Employment) {
			if(RandomMPR < MPR) {
			// for convenience
				
				Person person = populationFactory.createPerson(Id.createPersonId("person"+i));
				Plan plan = populationFactory.createPlan();
				
				Activity home1 = populationFactory.createActivityFromCoord("home", coordHome);
				home1.setEndTime(randomTime(7*60*60+60*58, 60*60));
				Leg towork = populationFactory.createLeg("taxi");
				
				plan.addActivity(home1);
				plan.addLeg(towork);
				
				Activity work = populationFactory.createActivityFromCoord("work", coordWork);
				work.setEndTime(randomTime(16*60*60+48*60, 60*60));
				work.setStartTime(randomTime(8*60*60+60*28, 60*60));
				Leg tohome = populationFactory.createLeg("taxi");
				
				plan.addActivity(work);
				plan.addLeg(tohome);
				
				Activity home2 = populationFactory.createActivityFromCoord("home", coordHome);
				plan.addActivity(home2);
				
				randomAction(plan, populationFactory.createLeg("taxi"), RandomAct, coordHome, coordWork, true,scenario.getNetwork());
				
				person.addPlan(plan);
				population.addPerson(person);
				
			}
			else {
				// for convenience
				
				Person person = populationFactory.createPerson(Id.createPersonId("person"+i));
				Plan plan = populationFactory.createPlan();
				
				Activity home1 = populationFactory.createActivityFromCoord("home", coordHome);
				home1.setEndTime(randomTime(7*60*60+60*58, 60*60));
				Leg towork = populationFactory.createLeg("car");
				
				plan.addActivity(home1);
				plan.addLeg(towork);
				
				Activity work = populationFactory.createActivityFromCoord("work", coordWork);
				work.setEndTime(randomTime(16*60*60+48*60, 60*60));
				work.setStartTime(randomTime(8*60*60+60*28, 60*60));
				Leg tohome = populationFactory.createLeg("car");
				
				plan.addActivity(work);
				plan.addLeg(tohome);
				
				Activity home2 = populationFactory.createActivityFromCoord("home", coordHome);
				plan.addActivity(home2);
				
				randomAction(plan, populationFactory.createLeg("car"), RandomAct, coordHome, coordWork, true, scenario.getNetwork());
				
				person.addPlan(plan);
				population.addPerson(person);
			}
		}
		else{
			
			if(RandomMPR < MPR) {
				Person person = populationFactory.createPerson(Id.createPersonId("person"+i));
				Plan plan = populationFactory.createPlan();
				
				Activity home = populationFactory.createActivityFromCoord("home", coordHome);
				home.setEndTime(randomTime(7*60*60+60*58, 60*60));
				plan.addActivity(home);
				
				Leg taxi = populationFactory.createLeg("taxi");

				randomAction(plan, taxi, RandomAct, coordHome, randomCoord(), false, scenario.getNetwork());
				
				person.addPlan(plan);
				population.addPerson(person);
			}
			else {
				
				Person person = populationFactory.createPerson(Id.createPersonId("person"+i));
				Plan plan = populationFactory.createPlan();
				
				Activity home = populationFactory.createActivityFromCoord("home", coordHome);
				home.setEndTime(randomTime(7*60*60+60*58, 60*60));
				plan.addActivity(home);
				Leg car = populationFactory.createLeg("car");
				
				randomAction(plan, car, RandomAct, coordHome, randomCoord(), false, scenario.getNetwork());
				
				person.addPlan(plan);
				population.addPerson(person);
				
				}
			}
	};
};
	
	

