package netgroups.multigraph;

import static java.lang.Math.*;
import static mc3kit.util.Math.*;
import netgroups.NetworkData;
import mc3kit.model.*;

public class NormalizationLikelihood extends Variable {
	double logP;
	
	public NormalizationLikelihood(MultigraphModel model, NetworkData data) {
		super(model);
		
		logP = 0.0;
		int n = data.nodeCount();
		for(int i = 0; i < n; i++) {
			for(int j = i+1; j < n; j++) {
				logP -= logFactorial(data.get(i,j));
			}
		}
		for(int i = 0; i < n; i++) {
			int Aii = data.get(i,i);
			logP -= (Aii / 2) * log(2.0);
			logP -= logFactorial(Aii / 2);
		}
	}
	
	@Override
	public boolean update() {
		setLogP(logP);
		return true;
	}
}
