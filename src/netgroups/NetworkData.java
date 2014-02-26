package netgroups;

import java.util.*;

public class NetworkData {
	boolean directed = true;
	private List<String> nodes;
	private List<Edge> edges;
	private Map<String, Integer> indexMap;
	
	private boolean initialized = false;
	private int minCount;
	private int maxCount;
	private int edgeCount;
	private int[][] adjMat;
	private int[] inDegree;
	private int[] outDegree;
	
	public List<String> getNodes() {
		assert initialized;
		return nodes;
	}
	
	public boolean isDirected() {
		return directed;
	}
	
	public int get(int i, int j) {
		assert initialized;
		return adjMat[i][j];
	}
	
	public int degree(int i) {
		assert initialized;
		assert !directed;
		return inDegree[i];
	}
	
	public int inDegree(int i) {
		assert initialized;
		assert directed;
		return inDegree[i];
	}
	
	public int outDegree(int i) {
		assert initialized;
		assert directed;
		return outDegree[i];
	}
	
	public int nodeCount() {
		assert initialized ;
		return nodes.size();
	}
	
	public int minCount() {
		assert initialized;
		return minCount;
	}
	
	public int maxCount() {
		assert initialized;
		return maxCount;
	}
	
	public int edgeCount() {
		assert initialized;
		return edgeCount;
	}
	
	public void initialize() {
		if(!initialized) {
			assert edges != null;
			if(nodes == null) {
				Set<String> nodeSet = new HashSet<>();
				for(Edge edge : edges) {
					nodeSet.add(edge.from);
					nodeSet.add(edge.to);
				}
				nodes.addAll(nodeSet);
				Collections.sort(nodes);
			}
			else {
				Set<String> nodeSet = new HashSet<>();
				for(String node : nodes) {
					assert !nodeSet.contains(node);
					nodeSet.add(node);
				}
				for(Edge edge : edges) {
					assert nodeSet.contains(edge.from);
					assert nodeSet.contains(edge.to);
				}
			}
			
			adjMat = new int[nodes.size()][nodes.size()];
			inDegree = new int[nodes.size()];
			if(directed) {
				outDegree = new int[nodes.size()];
			}
			
			indexMap = new HashMap<String, Integer>();
			for(int i = 0; i < nodes.size(); i++) {
				indexMap.put(nodes.get(i), i);
			}
			
			for(Edge edge : edges) {
				int i = indexMap.get(edge.from);
				int j = indexMap.get(edge.to);
				adjMat[i][j] += edge.count;
				if(i == j || !directed) {
					adjMat[j][i] += edge.count;
				}
				
				inDegree[i] += edge.count;
				if(directed) {
					outDegree[j] += edge.count;
				}
				else {
					inDegree[j] += edge.count;
				}
			}

			edgeCount = 0;
			maxCount = 0;
			for(int i = 0; i < nodes.size(); i++) {
				for(int j = 0; j < nodes.size(); j++) {
					edgeCount += adjMat[i][j];
					if(adjMat[i][j] > maxCount) {
						maxCount = adjMat[i][j];
					}
				}
			}
			minCount = maxCount;
			for(int i = 0; i < nodes.size(); i++) {
				for(int j = 0; j < nodes.size(); j++) {
					if(adjMat[i][j] < minCount) {
						minCount = adjMat[i][j];
					}
				}
			}
			
			if(!directed) {
				edgeCount /= 2;
			}
			
			initialized = true;
		}
	}
}
