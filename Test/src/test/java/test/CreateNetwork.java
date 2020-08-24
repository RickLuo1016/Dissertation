package test;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.network.algorithms.NetworkSimplifier;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;

// https://github.com/matsim-org/matsim-code-examples/blob/11.x/src/main/java/org/matsim/codeexamples/network/RunCreateNetworkFromOSM.java

public class CreateNetwork {
	
	public static void main(String[] args) {
		new CreateNetwork().FromOSM();
	}
	
	private void FromOSM() {
		
		// an empty network
		Network network = NetworkUtils.createNetwork();
		
		// Shenzhen EPSG code EPSG:32650 (WGS_1984_UTM_Zone_50N)
		CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation(
				TransformationFactory.WGS84, "EPSG:32650"
		);
		
		// create an osm network reader
		OsmNetworkReader reader = new OsmNetworkReader(network, transformation, true, true);
		
		// store in network
		reader.parse("C:/Reeply/Study/Dissertation/MATSim/OSM/map_small.osm");
		
		// clean the network to remove unconnected parts where agents might get stuck
		new NetworkCleaner().run(network);
		new NetworkSimplifier().run(network);
		
		// write out the network into a file
		new NetworkWriter(network).write("scenarios/test/network1.xml.gz");
	}
}
