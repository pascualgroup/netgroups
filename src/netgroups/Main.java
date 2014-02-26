package netgroups;

import com.google.gson.GsonBuilder;

import mc3kit.mcmc.*;
import mc3kit.output.SampleOutputStep;
import mc3kit.step.swap.SwapStep;
import mc3kit.step.univariate.UnivariateProposalStep;
import mc3kit.step.verification.VerificationStep;
import static mc3kit.util.JsonUtils.*;

public class Main {
	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.printf("Usage: java -jar netgroups.jar netgroups.Main config.json\n");
			System.exit(1);
		}
		
		// Load config
		Config config = null;;
		try {
			config = parseObject(Config.class, args[0]);
			String configJson = new GsonBuilder().setPrettyPrinting().create().toJson(config, Config.class);
			System.err.printf("Loaded configuration:\n%s\n", configJson);
		}
		catch(Exception e) {
			System.err.printf("Could not load config file. Error:\n");
			e.printStackTrace();
			System.exit(1);
		}
		
		// Load data
		NetworkData data = null;
		try {
			data = parseObject(NetworkData.class, config.dataFilename);
			data.initialize();
		}
		catch(Exception e) {
			System.err.printf("Could not load data file. Error:\n");
			e.printStackTrace();
			System.exit(1);
		}
		
		// Create and configure MCMC
		MCMC mcmc = null;
		try {
			mcmc = new MCMC("db", false);
			mcmc.setThin(config.thin);
			mcmc.setRandomSeed(config.randomSeed);
			mcmc.setChainCount(config.chainCount);
			mcmc.setLogLevel(config.logLevel);
			mcmc.setLogFilename(config.logFilename);
			mcmc.setLogAllChains(config.logAllChains);
			mcmc.setHeatFunction(new PowerHeatFunction(config.heatPower, config.minHeatExponent));
			mcmc.setModelFactory(new GroupModelFactory(config, data));
			mcmc.addStep(new UnivariateProposalStep(config.targetAcceptanceRate, config.tuneFor, config.tuneEvery));
			SwapStep evenSwapStep = new SwapStep(ChainParity.EVEN, config.tuneEvery * config.chainCount);
			SwapStep oddSwapStep = new SwapStep(ChainParity.ODD, config.tuneEvery * config.chainCount);
			for(int i = 0; i < config.chainCount; i++) {
				mcmc.addStep(evenSwapStep);
				mcmc.addStep(oddSwapStep);
			}
			mcmc.addStep(new VerificationStep(config.verifyEvery, config.tolerance));
			
			if(config.sampleFilename != null) {
				mcmc.addStep(new SampleOutputStep(config.sampleFilename, config.thin));
			}
			
			if(config.bestSampleFilename != null) {
				mcmc.addStep(new SampleOutputStep(config.bestSampleFilename, 1, true));
			}
		}
		catch(Exception e) {
			System.err.printf("Error configuring MCMC:\n");
			e.printStackTrace();
			System.exit(1);
		}
		
		// Run
		try {
			mcmc.runFor(config.runFor);
		}
		catch(Throwable e) {
			System.err.printf("Exception while running:\n");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
