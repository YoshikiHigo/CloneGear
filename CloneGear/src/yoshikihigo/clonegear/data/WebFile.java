package yoshikihigo.clonegear.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import yoshikihigo.clonegear.lexer.JavaLineLexer;
import yoshikihigo.clonegear.lexer.JavascriptLineLexer;
import yoshikihigo.clonegear.lexer.LineLexer;
import yoshikihigo.clonegear.lexer.PHPLineLexer;
import yoshikihigo.clonegear.lexer.token.Delimiter;
import yoshikihigo.clonegear.lexer.token.Token;
import yoshikihigo.commentremover.CRConfig;
import yoshikihigo.commentremover.CommentRemover;
import yoshikihigo.commentremover.CommentRemoverJC;
import yoshikihigo.commentremover.CommentRemoverJS;
import yoshikihigo.commentremover.CommentRemoverPHP;
import yoshikihigo.commentremover.FileUtility;

public abstract class WebFile extends SourceFile {

	protected WebFile(final String path, final int groupID) {
		super(path, groupID);
	}

	public List<Statement> getJavascriptStatements() {

		final String content = FileUtility.readFile(new File(this.path), null);
		final Source src = new Source(
				PHPFile.class != this.getClass() ? content : content + " ?>");
		final List<Element> elements = src.getAllElements("script");

		final String[] args = new String[] { "-q", "-blankline", "retain",
				"-bracketline", "retain", "-indent", "retain", "-blockcomment",
				"remove", "-linecomment", "remove" };
		final CRConfig config = CRConfig.initialize(args);
		final CommentRemover remover = new CommentRemoverJS(config);
		final LineLexer lexer = new JavascriptLineLexer();

		final List<Token> allTokens = new ArrayList<>();

		for (final Element e : elements) {
			if (this.isJavascript(e)) {
				final Segment segment = e.getContent();
				final int startLine = segment.getRowColumnVector().getRow();
				final int startColumn = segment.getRowColumnVector()
						.getColumn();

				final StringBuilder builder = new StringBuilder();
				for (int line = 1; line < startLine; line++) {
					builder.append(System.lineSeparator());
				}
				for (int column = 1; column < startColumn; column++) {
					builder.append(' ');
				}
				builder.append(segment.toString());

				final String normalizedText = remover.perform(builder
						.toString());
				final List<Token> tokens = lexer.lexFile(normalizedText);
				allTokens.addAll(tokens);
				allTokens.add(new Delimiter());
			}
		}

		final List<Statement> statements = Statement.getJSStatements(allTokens);
		return statements;
	}

	private boolean isJavascript(final Element e) {

		final String type = e.getAttributeValue("type");
		if ((null != type) && type.equals("text/javascript")) {
			return true;
		}

		final String language = e.getAttributeValue("language");
		if ((null != language) && language.equals("javascript")) {
			return true;
		}

		return false;
	}

	public List<Statement> getJSPStatements() {

		final String content = FileUtility.readFile(new File(this.path), null);
		final Source src = new Source(
				PHPFile.class != this.getClass() ? content : content + " ?>");
		final List<Element> elements = src.getAllElements();

		final String[] args = new String[] { "-q", "-blankline", "retain",
				"-bracketline", "retain", "-indent", "retain", "-blockcomment",
				"remove", "-linecomment", "remove" };
		final CRConfig config = CRConfig.initialize(args);
		final CommentRemover remover = new CommentRemoverJC(config);
		final LineLexer lexer = new JavaLineLexer();

		final List<Token> allTokens = new ArrayList<>();

		for (final Element e : elements) {
			final String text = e.toString();
			if (text.startsWith("<%") && !text.startsWith("<%@")
					&& text.endsWith("%>")) {

				final int startLine = e.getRowColumnVector().getRow();
				final int startColumn = e.getRowColumnVector().getColumn() + 2;

				final StringBuilder builder = new StringBuilder();
				for (int line = 1; line < startLine; line++) {
					builder.append(System.lineSeparator());
				}
				for (int column = 1; column < startColumn; column++) {
					builder.append(' ');
				}

				builder.append(text.substring(2, text.length() - 2));

				final String normalizedText = remover.perform(builder
						.toString());
				final List<Token> tokens = lexer.lexFile(normalizedText);
				allTokens.addAll(tokens);
				allTokens.add(new Delimiter());
			}
		}

		final List<Statement> statements = Statement.getJCStatements(allTokens);
		return statements;
	}

	public List<Statement> getPHPStatements() {

		final String content = FileUtility.readFile(new File(this.path), null);
		final Source src = new Source(
				PHPFile.class != this.getClass() ? content : content + " ?>");
		final List<Element> elements = src.getAllElements();

		final String[] args = new String[] { "-q", "-blankline", "retain",
				"-bracketline", "retain", "-indent", "retain", "-blockcomment",
				"remove", "-linecomment", "remove" };
		final CRConfig config = CRConfig.initialize(args);
		final CommentRemover remover = new CommentRemoverPHP(config);
		final LineLexer lexer = new PHPLineLexer();

		final List<Token> allTokens = new ArrayList<>();

		for (final Element e : elements) {
			final String text = e.toString();
			if (text.startsWith("<?php")) {

				final int startLine = e.getRowColumnVector().getRow();
				final int startColumn = e.getRowColumnVector().getColumn() + 5;

				final StringBuilder builder = new StringBuilder();
				for (int line = 1; line < startLine; line++) {
					builder.append(System.lineSeparator());
				}
				for (int column = 1; column < startColumn; column++) {
					builder.append(' ');
				}

				builder.append(text.substring(5, text.length() - 3));

				final String normalizedText = remover.perform(builder
						.toString());
				final List<Token> tokens = lexer.lexFile(normalizedText);
				allTokens.addAll(tokens);
				allTokens.add(new Delimiter());
			}
		}

		final List<Statement> statements = Statement
				.getPHPStatements(allTokens);
		return statements;
	}
}
