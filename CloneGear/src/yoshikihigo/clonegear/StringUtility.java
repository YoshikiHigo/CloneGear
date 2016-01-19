package yoshikihigo.clonegear;

import java.util.ArrayList;
import java.util.List;

import yoshikihigo.clonegear.data.Statement;
import yoshikihigo.clonegear.lexer.CLineLexer;
import yoshikihigo.clonegear.lexer.JavaLineLexer;
import yoshikihigo.clonegear.lexer.JavascriptLineLexer;
import yoshikihigo.clonegear.lexer.LineLexer;
import yoshikihigo.clonegear.lexer.PHPLineLexer;
import yoshikihigo.clonegear.lexer.PythonLineLexer;
import yoshikihigo.clonegear.lexer.token.Token;
import yoshikihigo.commentremover.CRConfig;
import yoshikihigo.commentremover.CommentRemover;
import yoshikihigo.commentremover.CommentRemoverJC;
import yoshikihigo.commentremover.CommentRemoverJS;
import yoshikihigo.commentremover.CommentRemoverPHP;
import yoshikihigo.commentremover.CommentRemoverPY;

public class StringUtility {

	public static List<Statement> splitToStatements(final String text,
			final LANGUAGE language) {

		if (text.isEmpty()) {
			return new ArrayList<Statement>();
		}

		switch (language) {
		case JAVA:
		case JSP: {
			final String[] args = new String[11];
			args[0] = "-q";
			args[1] = "-blankline";
			args[2] = "retain";
			args[3] = "-bracketline";
			args[4] = "retain";
			args[5] = "-indent";
			args[6] = "retain";
			args[7] = "-blockcomment";
			args[8] = "remove";
			args[9] = "-linecomment";
			args[10] = "remove";
			final CRConfig config = CRConfig.initialize(args);
			final CommentRemover remover = new CommentRemoverJC(config);
			final String normalizedText = remover.perform(text);
			final LineLexer lexer = new JavaLineLexer();
			final List<Token> tokens = lexer.lexFile(normalizedText);
			final List<Statement> statements = Statement
					.getJCStatements(tokens);
			return statements;
		}
		case JAVASCRIPT: {
			final String[] args = new String[11];
			args[0] = "-q";
			args[1] = "-blankline";
			args[2] = "retain";
			args[3] = "-bracketline";
			args[4] = "retain";
			args[5] = "-indent";
			args[6] = "retain";
			args[7] = "-blockcomment";
			args[8] = "remove";
			args[9] = "-linecomment";
			args[10] = "remove";
			final CRConfig config = CRConfig.initialize(args);
			final CommentRemover remover = new CommentRemoverJS(config);
			final String normalizedText = remover.perform(text);
			final LineLexer lexer = new JavascriptLineLexer();
			final List<Token> tokens = lexer.lexFile(normalizedText);
			final List<Statement> statements = Statement
					.getJSStatements(tokens);
			return statements;
		}
		case C:
		case CPP: {
			final String[] args = new String[11];
			args[0] = "-q";
			args[1] = "-blankline";
			args[2] = "retain";
			args[3] = "-bracketline";
			args[4] = "retain";
			args[5] = "-indent";
			args[6] = "retain";
			args[7] = "-blockcomment";
			args[8] = "remove";
			args[9] = "-linecomment";
			args[10] = "remove";
			final CRConfig config = CRConfig.initialize(args);
			final CommentRemover remover = new CommentRemoverJC(config);
			final String normalizedText = remover.perform(text);
			final LineLexer lexer = new CLineLexer();
			final List<Token> tokens = lexer.lexFile(normalizedText);
			final List<Statement> statements = Statement
					.getJCStatements(tokens);
			return statements;
		}
		case PHP: {
			final String[] args = new String[11];
			args[0] = "-q";
			args[1] = "-blankline";
			args[2] = "retain";
			args[3] = "-bracketline";
			args[4] = "retain";
			args[5] = "-indent";
			args[6] = "retain";
			args[7] = "-blockcomment";
			args[8] = "remove";
			args[9] = "-linecomment";
			args[10] = "remove";
			final CRConfig config = CRConfig.initialize(args);
			final CommentRemover remover = new CommentRemoverPHP(config);
			final String normalizedText = remover.perform(text);
			final LineLexer lexer = new PHPLineLexer();
			final List<Token> tokens = lexer.lexFile(normalizedText);
			final List<Statement> statements = Statement
					.getPHPStatements(tokens);
			return statements;
		}
		case PYTHON: {
			final String[] args = new String[7];
			args[0] = "-q";
			args[1] = "-blankline";
			args[2] = "retain";
			args[3] = "-blockcomment";
			args[4] = "remove";
			args[5] = "-linecomment";
			args[6] = "remove";
			final CRConfig config = CRConfig.initialize(args);
			final CommentRemover remover = new CommentRemoverPY(config);
			final String normalizedText = remover.perform(text);
			final LineLexer lexer = new PythonLineLexer();
			final List<Token> tokens = lexer.lexFile(normalizedText);
			final List<Statement> statements = Statement
					.getPYStatements(tokens);
			return statements;
		}
		default: {
			System.err.println("invalid programming language.");
			System.exit(0);
		}
		}

		return null;
	}

	public static boolean isBlankLine(final String line) {
		return line.chars().allMatch(c -> (' ' == c) || ('\t' == c));
	}
}
