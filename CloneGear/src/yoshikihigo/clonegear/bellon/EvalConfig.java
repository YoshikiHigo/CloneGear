package yoshikihigo.clonegear.bellon;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import yoshikihigo.clonegear.CGConfig;
import yoshikihigo.clonegear.LANGUAGE;

public class EvalConfig {

	static private EvalConfig SINGLETON = null;

	static public boolean initialize(final String[] args) {

		if (null != SINGLETON) {
			return false;
		}

		final Options options = new Options();

		{
			final Option results = new Option("results", "results", true,
					"clone detection results");
			results.setArgName("file");
			results.setArgs(1);
			results.setRequired(true);
			options.addOption(results);
		}

		{
			final Option oracle = new Option("oracle", "oracle", true,
					"clone detection results");
			oracle.setArgName("file");
			oracle.setArgs(1);
			oracle.setRequired(true);
			options.addOption(oracle);
		}

		{
			final Option threshold = new Option("threshold", "threshold", true,
					"clone detection results");
			threshold.setArgName("numerical value");
			threshold.setArgs(1);
			threshold.setRequired(false);
			options.addOption(threshold);
		}
		
		{
			final Option good = new Option("good", "good", true,
					"file to output good results");
			good.setArgName("file");
			good.setArgs(1);
			good.setRequired(false);
			options.addOption(good);
		}
		
		{
			final Option ok = new Option("ok", "ok", true,
					"file to output ok results");
			ok.setArgName("file");
			ok.setArgs(1);
			ok.setRequired(false);
			options.addOption(ok);
		}

		try {
			final CommandLineParser parser = new PosixParser();
			final CommandLine commandLine = parser.parse(options, args);
			SINGLETON = new EvalConfig(commandLine);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(0);
		}

		return true;
	}

	static public EvalConfig getInstance() {

		if (null == SINGLETON) {
			System.err.println("Config is not initialized.");
			System.exit(0);
		}

		return SINGLETON;
	}

	private final CommandLine commandLine;

	private EvalConfig(final CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	public String getRESULTS() {
		return this.commandLine.getOptionValue("results");
	}

	public String getORACLE() {
		return this.commandLine.getOptionValue("oracle");
	}
	
	public float getTHRESHOLD() {

		if(!this.commandLine.hasOption("threshold")){
			return 0.7f;
		}
		
		final String text = this.commandLine.getOptionValue("threshold");
		final float value = Float.parseFloat(text);
		if(value < 0f || 1f < value){
			System.err.println("value for option \"-threshold\" must be between 0.0 and 1.0.");
			System.exit(0);
		}
		
		return value;
	}
	
	public String getOK() {
		return this.commandLine.getOptionValue("ok");
	}
	
	public String getGOOD() {
		return this.commandLine.getOptionValue("good");
	}
}
