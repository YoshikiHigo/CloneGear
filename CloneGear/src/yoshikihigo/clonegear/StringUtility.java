package yoshikihigo.clonegear;

import java.util.ArrayList;
import java.util.List;

import yoshikihigo.clonegear.data.Statement;
import yoshikihigo.clonegear.lexer.CLineLexer;
import yoshikihigo.clonegear.lexer.JavaLineLexer;
import yoshikihigo.clonegear.lexer.LineLexer;
import yoshikihigo.clonegear.lexer.token.Token;
import yoshikihigo.commentremover.CommentRemover;

public class StringUtility {

	public static List<Statement> splitToStatements(final String text,
			final String language) {

		if (text.isEmpty()) {
			return new ArrayList<Statement>();
		}

		final String[] args = new String[8];
		args[0] = "-l";
		args[1] = language;
		args[2] = "-i";
		args[3] = text;
		args[4] = "-q";
		args[5] = "-a";
		args[6] = "-d";
		args[7] = "-e";
		final CommentRemover remover = new CommentRemover();
		remover.perform(args);
		final String nonCommentText = remover.result;

		final LineLexer lexer;
		switch (language) {
		case "java": {
			lexer = new JavaLineLexer();
			break;
		}
		case "c": {
			lexer = new CLineLexer();
			break;
		}
		default:
			lexer = null;
			System.err.print("undefined language value: ");
			System.err.println(language);
			System.exit(0);
		}

		final List<Token> tokens = lexer.lexFile(nonCommentText);
		final List<Statement> statements = Statement.getStatements(tokens);

		return statements;
	}
}
