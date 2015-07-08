package yoshikihigo.clonegear;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Config {

	static private Config SINGLETON = null;

	static public boolean initialize(final String[] args) {

		if (null != SINGLETON) {
			return false;
		}

		final Options options = new Options();

		{
			final Option option = new Option("src", "source", true,
					"source code of clone detection target");
			option.setArgName("sourcecode");
			option.setArgs(1);
			option.setRequired(false);
			options.addOption(option);
		}

		{
			final Option option = new Option("o", "output", true,
					"output file for detection results");
			option.setArgName("file");
			option.setArgs(1);
			option.setRequired(false);
			options.addOption(option);
		}
		
		{
			final Option option = new Option("lang", "language", true,
					"programming language for analysis");
			option.setArgName("language");
			option.setArgs(1);
			option.setRequired(false);
			options.addOption(option);
		}

		{
			final Option option = new Option("thrld", "threshold", true,
					"threshold of detected clone size");
			option.setArgName("threshold");
			option.setArgs(1);
			option.setRequired(false);
			options.addOption(option);
		}
		
		{
			final Option option = new Option("soft", "software", true,
					"software name");
			option.setArgName("software");
			option.setArgs(1);
			option.setRequired(false);
			options.addOption(option);
		}

		{
			final Option option = new Option("thd", "thread", true,
					"end revision of repository for test");
			option.setArgName("thread");
			option.setArgs(1);
			option.setRequired(false);
			options.addOption(option);
		}

		{
			final Option option = new Option("v", "verbose", false,
					"verbose output for progressing");
			option.setRequired(false);
			options.addOption(option);
		}

		{
			final Option option = new Option("csv", "csv", true,
					"CSV file for writing modification patterns");
			option.setArgName("file");
			option.setArgs(1);
			option.setRequired(false);
			options.addOption(option);
		}

		try {
			final CommandLineParser parser = new PosixParser();
			final CommandLine commandLine = parser.parse(options, args);
			SINGLETON = new Config(commandLine);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(0);
		}

		return true;
	}

	static public Config getInstance() {

		if (null == SINGLETON) {
			System.err.println("Config is not initialized.");
			System.exit(0);
		}

		return SINGLETON;
	}

	private final CommandLine commandLine;

	private Config(final CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	public String getLANGUAGE() {
		return this.commandLine.hasOption("lang") ? this.commandLine
				.getOptionValue("lang") : "";
	}

	public String getSOFTWARE() {
		if (!this.commandLine.hasOption("soft")) {
			System.err.println("option \"soft\" is not specified.");
			System.exit(0);
		}
		return this.commandLine.getOptionValue("soft");
	}
	
	public String getSource(){
		if(!this.commandLine.hasOption("src")){
			System.err.println("option \"src\" is not specified.");
			System.exit(0);
		}
		return this.commandLine.getOptionValue("src");
	}

	public boolean hasOUTPUT() {
		return this.commandLine.hasOption("o");
	}
	
	public String getOUTPUT(){
		if(!this.commandLine.hasOption("o")){
			System.err.println("option \"o\" is not specified.");
			System.exit(0);
		}
		return this.commandLine.getOptionValue("o");
	}
	
	public int getTHRESHOLD() {
		return this.commandLine.hasOption("thrld") ? Integer
				.parseInt(this.commandLine.getOptionValue("thrld")) : 50;
	}
	
	public int getTHREAD() {
		return this.commandLine.hasOption("thd") ? Integer
				.parseInt(this.commandLine.getOptionValue("thd")) : 1;
	}

	public boolean isVERBOSE() {
		return this.commandLine.hasOption("v");
	}
}
