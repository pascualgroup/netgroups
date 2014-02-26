package netgroups.multigraph;

import netgroups.NetworkData;
import mc3kit.*;
import static mc3kit.util.Math.*;
import mc3kit.model.*;
import mc3kit.util.IterableBitSet;

public class GroupLikelihood extends Variable {
	MultigraphModel model;
	NetworkData data;
	int r;
	
	public GroupLikelihood(MultigraphModel model, NetworkData data, int r) throws MC3KitException {
		super(model);
		
		this.model = model;
		this.data = data;
		this.r = r;
		
		model.addEdge(this, model.dcDirichletAlpha);
		model.addEdge(this, model.groupFuncs[r]);
	}
	
	@Override
	public boolean update() throws MC3KitException {
		double logP = 0.0;
		
		double alpha = model.dcDirichletAlpha.getValue();
//		System.err.printf("Dirichlet alpha = %f\n", alpha);
		
		// Assignment of link ends within groups are given by a Dirichlet-categorical distribution
		// whose marginal likelihood is calculated directly:
		IterableBitSet group = model.groupFuncs[r].getGroup();
		int groupSize = group.cardinality();
//		System.err.printf("groupSize = %d\n", groupSize);
		int sumDegree = 0;
		for(int i : group) {
//			System.err.printf("degree %d = %d\n", i, data.degree(i));
			sumDegree += data.degree(i);
		}
//		System.err.printf("sumDegree = %d\n", sumDegree);
		
		logP += logGamma(groupSize * alpha);
		logP -= logGamma(sumDegree + groupSize * alpha);
		for(int i : group) {
			logP += logGamma(data.degree(i) + alpha);
		}
		logP -= groupSize * logGamma(alpha);
		
//		System.err.printf("logP = %f\n", logP);
		
		setLogP(logP);
		return true;
	}
}
