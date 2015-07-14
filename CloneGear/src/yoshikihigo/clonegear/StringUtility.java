package yoshikihigo.clonegear;

import java.util.ArrayList;
import java.util.List;

import yoshikihigo.clonegear.data.Statement;
import yoshikihigo.clonegear.lexer.CLineLexer;
import yoshikihigo.clonegear.lexer.JavaLineLexer;
import yoshikihigo.clonegear.lexer.LineLexer;
import yoshikihigo.clonegear.lexer.PythonLineLexer;
import yoshikihigo.clonegear.lexer.token.Token;
import yoshikihigo.commentremover.CRConfig;
import yoshikihigo.commentremover.CommentRemover;
import yoshikihigo.commentremover.CommentRemoverJC;
import yoshikihigo.commentremover.CommentRemoverPY;

public class StringUtility {

	public static List<Statement> splitToStatements(final String text,
			final LANGUAGE language) {

		if (text.isEmpty()) {
			return new ArrayList<Statement>();
		}

		final String[] args = new String[1];
		args[0] = "-q";
		final CRConfig config = CRConfig.initialize(args);

		switch (language) {
		case JAVA: {
			final CommentRemover remover = new CommentRemoverJC(config);
			final String normalizedText = remover.perform(text);
			final LineLexer lexer = new JavaLineLexer();
			final List<Token> tokens = lexer.lexFile(normalizedText);
			final List<Statement> statements = Statement.getStatements(tokens);
			return statements;
		}
		case C:
		case CPP: {
			final CommentRemover remover = new CommentRemoverJC(config);
			final String normalizedText = remover.perform(text);
			final LineLexer lexer = new CLineLexer();
			final List<Token> tokens = lexer.lexFile(normalizedText);
			final List<Statement> statements = Statement.getStatements(tokens);
			return statements;
		}
		case PYTHON: {
			final CommentRemover remover = new CommentRemoverPY(config);
			final String normalizedText = remover.perform(text);
			final LineLexer lexer = new PythonLineLexer();
			final List<Token> tokens = lexer.lexFile(normalizedText);
			final List<Statement> statements = Statement.getStatements(tokens);
			return statements;
		}
		default: {
			System.err.println("invalid programming language.");
			System.exit(0);
		}
		}

		return null;
	}
}
