package yoshikihigo.clonegear;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CGConfig {

	static private CGConfig SINGLETON = null;

	static public boolean initialize(final String[] args) {

		final Options options = new Options();

		{
			final Option source = new Option("src", "source", true,
					"source code of clone detection target");
			source.setArgName("sourcecode");
			source.setArgs(1);
			source.setRequired(false);
			options.addOption(source);
		}

		{
			final Option list = new Option("list", "list", true,
					"file listing target source files");
			list.setArgName("listfile");
			list.setArgs(1);
			list.setRequired(false);
			options.addOption(list);
		}

		{
			final Option similarityOutput = new Option("sml", "similarity",
					true, "output file for similarities between clone sets");
			similarityOutput.setArgName("file");
			similarityOutput.setArgs(1);
			similarityOutput.setRequired(false);
			options.addOption(similarityOutput);
		}

		{
			final Option language = new Option("lang", "language", true,
					"programming language for analysis");
			language.setArgName("language");
			language.setArgs(1);
			language.setRequired(false);
			options.addOption(language);
		}

		{
			final Option threshold = new Option("thrld", "threshold", true,
					"threshold of detected clone size");
			threshold.setArgName("threshold");
			threshold.setArgs(1);
			threshold.setRequired(false);
			options.addOption(threshold);
		}

		{
			final Option gap = new Option("gap", "gap", true,
					"threshold of gap size allowed in clones");
			gap.setArgName("gap");
			gap.setArgs(1);
			gap.setRequired(false);
			options.addOption(gap);
		}

		{
			final Option thread = new Option("thd", "thread", true,
					"end revision of repository for test");
			thread.setArgName("thread");
			thread.setArgs(1);
			thread.setRequired(false);
			options.addOption(thread);
		}

		{
			final Option verbose = new Option("v", "verbose", false,
					"verbose output for progressing");
			verbose.setRequired(false);
			options.addOption(verbose);
		}

		{
			final Option result = new Option("result", "result", true,
					"clone detection results");
			result.setArgName("file");
			result.setArgs(1);
			result.setRequired(false);
			options.addOption(result);
		}

		{
			final Option cui = new Option("cui", "cui", false,
					"run in CUI mode");
			cui.setRequired(false);
			options.addOption(cui);
		}

		{
			final Option folding = new Option("folding", "folding", false,
					"do folding preprocessing for clone detection");
			folding.setRequired(false);
			options.addOption(folding);
		}

		{
			final Option cg = new Option("cg", "cross-group", true,
					"detect clones cross groups");
			cg.setArgName("YES-or-NO");
			cg.setArgs(1);
			cg.setRequired(false);
			options.addOption(cg);
		}

		{
			final Option cf = new Option("cf", "cross-file", true,
					"detect clones cross files");
			cf.setArgName("YES-or-NO");
			cf.setArgs(1);
			cf.setRequired(false);
			options.addOption(cf);
		}

		{
			final Option wf = new Option("wf", "within-file", true,
					"detect clones cross files");
			wf.setArgName("YES-or-NO");
			wf.setArgs(1);
			wf.setRequired(false);
			options.addOption(wf);
		}

		{
			final Option gemini = new Option("g", "gemini", false,
					"launch Gemini after finishing clone detection");
			gemini.setRequired(false);
			options.addOption(gemini);
		}

		{
			final Option debug = new Option("debug", "debug", false,
					"print some informlation for debugging");
			debug.setRequired(false);
			options.addOption(debug);
		}

		{
			final Option module = new Option("module", "module", false,
					"do not consider module boundaries");
			module.setRequired(false);
			options.addOption(module);
		}

		{
			final Option bellon = new Option("bellon", "bellon", false,
					"use bellon format for output file");
			bellon.setRequired(false);
			options.addOption(bellon);
		}

		try {
			final CommandLineParser parser = new PosixParser();
			final CommandLine commandLine = parser.parse(options, args);
			SINGLETON = new CGConfig(commandLine);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(0);
		}

		return true;
	}

	static public CGConfig getInstance() {

		if (null == SINGLETON) {
			System.err.println("Config is not initialized.");
			System.exit(0);
		}

		return SINGLETON;
	}

	private final CommandLine commandLine;

	private CGConfig(final CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	public Set<LANGUAGE> getLANGUAGE() {

		final Set<LANGUAGE> languages = new HashSet<>();

		if (this.commandLine.hasOption("lang")) {
			final String option = this.commandLine.getOptionValue("lang");
			final StringTokenizer tokenizer = new StringTokenizer(option, ":");
			while (tokenizer.hasMoreTokens()) {
				try {
					final String value = tokenizer.nextToken();
					final LANGUAGE language = LANGUAGE.valueOf(value
							.toUpperCase());
					languages.add(language);
				} catch (final IllegalArgumentException e) {
					System.err.println("invalid option value for \"-lang\"");
					System.exit(0);
				}
			}
		}

		else {
			for (final LANGUAGE language : LANGUAGE.values()) {
				languages.add(language);
			}
		}

		return languages;
	}

	public boolean hasSOURCE() {
		return this.commandLine.hasOption("src");
	}

	public String getSOURCE() {
		if (!this.commandLine.hasOption("src")) {
			System.err.println("option \"src\" is not specified.");
			System.exit(0);
		}
		return this.commandLine.getOptionValue("src");
	}

	public boolean hasLIST() {
		return this.commandLine.hasOption("list");
	}

	public String getLIST() {
		if (!this.commandLine.hasOption("list")) {
			System.err.println("option \"list\" is not specified.");
			System.exit(0);
		}
		return this.commandLine.getOptionValue("list");
	}

	public boolean hasSIMILARITY() {
		return this.commandLine.hasOption("sml");
	}

	public String getSIMILARITY() {
		if (!this.commandLine.hasOption("sml")) {
			System.err.println("option \"sml\" is not specified.");
			System.exit(0);
		}
		return this.commandLine.getOptionValue("sml");
	}

	public int getTHRESHOLD() {
		return this.commandLine.hasOption("thrld") ? Integer
				.parseInt(this.commandLine.getOptionValue("thrld")) : 50;
	}

	public int getGAP() {
		return this.commandLine.hasOption("gap") ? Integer
				.parseInt(this.commandLine.getOptionValue("gap")) : 2;
	}

	public int getTHREAD() {
		return this.commandLine.hasOption("thd") ? Integer
				.parseInt(this.commandLine.getOptionValue("thd")) : 1;
	}

	public boolean isVERBOSE() {
		return this.commandLine.hasOption("v");
	}

	public boolean hasRESULT() {
		return this.commandLine.hasOption("result");
	}

	public String getRESULT() {
		if (!this.commandLine.hasOption("result")) {
			System.err.println("option \"result\" is not specified.");
			System.exit(0);
		}
		return this.commandLine.getOptionValue("result");
	}

	public boolean isCrossGroupDetection() {
		if (!this.commandLine.hasOption("cg")) {
			return true;
		}
		final String value = this.commandLine.getOptionValue("cg");
		if (value.equalsIgnoreCase("YES")) {
			return true;
		} else if (value.equalsIgnoreCase("NO")) {
			return false;
		} else {
			System.err.println("\"" + value
					+ "\" is invalid value for \"--cross-group\".");
			System.err.println("acceptable values are YES or NOT.");
			System.exit(0);
		}
		return true;
	}

	public boolean isCrossFileDetection() {
		if (!this.commandLine.hasOption("cf")) {
			return true;
		}
		final String value = this.commandLine.getOptionValue("cf");
		if (value.equalsIgnoreCase("YES")) {
			return true;
		} else if (value.equalsIgnoreCase("NO")) {
			return false;
		} else {
			System.err.println("\"" + value
					+ "\" is invalid value for \"--cross-file\".");
			System.err.println("acceptable values are YES or NOT.");
			System.exit(0);
		}
		return true;
	}

	public boolean isWithinFileDetection() {
		if (!this.commandLine.hasOption("wf")) {
			return true;
		}
		final String value = this.commandLine.getOptionValue("wf");
		if (value.equalsIgnoreCase("YES")) {
			return true;
		} else if (value.equalsIgnoreCase("NO")) {
			return false;
		} else {
			System.err.println("\"" + value
					+ "\" is invalid value for \"--within-file\".");
			System.err.println("acceptable values are YES or NOT.");
			System.exit(0);
		}
		return true;
	}

	public boolean isCUI() {
		return this.commandLine.hasOption("cui");
	}

	public boolean isFOLDING() {
		return this.commandLine.hasOption("folding");
	}

	public boolean isGEMINI() {
		return this.commandLine.hasOption("g");
	}

	public boolean isDEBUG() {
		return this.commandLine.hasOption("debug");
	}

	public boolean isBELLON() {
		return this.commandLine.hasOption("bellon");
	}

	public boolean isMODULE() {
		return this.commandLine.hasOption("module");
	}
}
