<!DOCTYPE html>
<%--
  - Author: Martin Jericho
  - Created: 2011-10-28
  - Last Modified: 2011-10-28
  - Description: Demonstration of the Renderer class of the Jericho HTML Parser
  - http://jericho.htmlparser.net/docs/javadoc/net/htmlparser/jericho/Renderer.html
--%><%@ page info="Jericho HTML Parser - Render to Text"
%><%@ page import="net.htmlparser.jericho.*, java.util.*, java.io.*, javax.servlet.*, javax.servlet.http.*"
%><%!
	private int parseInt(String text, int defaultValue) {
		if (text==null) return defaultValue;
		try {
			return Integer.parseInt(text.trim());
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}
%><%
	Writer responseWriter=response.getWriter();
	String output="";
	String parserLog="";
	String sourceText=request.getParameter("SourceText");
	boolean initialise=sourceText==null;
	int maxLineLength=76;
	int hrLineLength=72;
	boolean includeHyperlinkURLs=true;
	boolean includeAlternateText=true;
	boolean decorateFontStyles=false;
	int blockIndentSize=4;
	int listIndentSize=6;
	if (sourceText==null) {
		sourceText="";
	} else {
		maxLineLength=parseInt(request.getParameter("MaxLineLength"),maxLineLength);
		hrLineLength=parseInt(request.getParameter("HRLineLength"),hrLineLength);
		includeHyperlinkURLs=request.getParameter("IncludeHyperlinkURLs")!=null;
		includeAlternateText=request.getParameter("IncludeAlternateText")!=null;
		decorateFontStyles=request.getParameter("DecorateFontStyles")!=null;
		blockIndentSize=parseInt(request.getParameter("BlockIndentSize"),blockIndentSize);
		listIndentSize=parseInt(request.getParameter("ListIndentSize"),listIndentSize);
		Source source=new Source(sourceText);
	 	Writer logWriter=new StringWriter();
		source.setLogger(new WriterLogger(logWriter));
		String rawOutput=source.getRenderer().setMaxLineLength(maxLineLength).setHRLineLength(hrLineLength).setIncludeHyperlinkURLs(includeHyperlinkURLs).setIncludeAlternateText(includeAlternateText).setDecorateFontStyles(decorateFontStyles).setBlockIndentSize(blockIndentSize).setListIndentSize(listIndentSize).toString();
		output=CharacterReference.encode(rawOutput);
		parserLog=CharacterReference.encodeWithWhiteSpaceFormatting(logWriter.toString());
	}
%><html lang="en">
	<head>
		<meta charset="utf-8" />
		<title>Jericho HTML Parser - Render to Text</title>
		<meta name="description" content="Render HTML to text suitable for email alternative text" />
		<link rel="stylesheet" type="text/css" href="../css/jericho.css" />
	</head>
	<body>
		<form class="fullscreen" method="post" accept-charset="utf-8">
			<div class="fullscreen-content">
				<div>
					<a style="float: right" title="Jericho HTML Parser Homepage" href="http://jericho.htmlparser.net/">Jericho HTML Parser</a>
					<h1>Jericho HTML Parser - Render to Text</h1>
					<div style="float: right"><a href="http://jericho.htmlparser.net/docs/javadoc/net/htmlparser/jericho/Renderer.html">click here for documentation</a></div>
					<div style="float: left">Enter HTML to render below:</div>
					<div style="margin-left: 51%">Output:</div>
				</div>
				<div id="textarea-container" style="top:50px; bottom:180px">
					<textarea style="float:left; width:49%; height:100%" id="SourceText" name="SourceText" class="Shaded"><%=CharacterReference.encode(sourceText)%></textarea>
					<textarea style="float:right; width:49%; height:100%" id="OutputText" name="OutputText" class="Shaded"><%=output%></textarea>
				</div>
				<div id="bottom-segment" style="height:170px">
					<div style="float:right"><input type="submit" class="Button" value="Process"/></div>
					<div style="float:left; margin-top: 3px">Options:</div>
					<div style="margin-left: 55px">
						<div>
							<label for="MaxLineLength" title="the column at which lines are to be wrapped">Maximum line length <input id="MaxLineLength" name="MaxLineLength" class="NumericField" value="<%=maxLineLength%>" /></label>
							<label for="HRLineLength" title="the number of hyphen characters used to render HR elements">Horizontal Rule &lt;hr&gt; line length <input id="HRLineLength" name="HRLineLength" class="NumericField" value="<%=hrLineLength%>" /></label>
							<label for="BlockIndentSize" title="the size of the indent to be used for anything other than LI elements">Block indent size <input id="BlockIndentSize" name="BlockIndentSize" class="NumericField" value="<%=blockIndentSize%>" /></label>
							<label for="ListIndentSize" title="the size of the indent to be used for LI elements">List indent size <input id="ListIndentSize" name="ListIndentSize" class="NumericField" value="<%=listIndentSize%>" /></label>
						</div>
						<div style="margin-top: 5px">
							<label for="IncludeHyperlinkURLs" class="checkbox-label" title="include the URL of hyperlinks in angle brackets after the link text"><input type="checkbox" class="Button" id="IncludeHyperlinkURLs" name="IncludeHyperlinkURLs" value="Y" <%=includeHyperlinkURLs ? "checked=\"checked\"" : "" %> /> Include hyperlink URLs</label>
							<label for="IncludeAlternateText" class="checkbox-label" title="include alternate text of tags that have an alt attribute"><input type="checkbox" class="Button" id="IncludeAlternateText" name="IncludeAlternateText" value="Y" <%=includeAlternateText ? "checked=\"checked\"" : "" %> /> Include alt attribute values</label>
							<label for="DecorateFontStyles" class="checkbox-label" title="include decoration characters around the content of some font style elements and phrase elements"><input type="checkbox" class="Button" id="DecorateFontStyles" name="DecorateFontStyles" value="Y" <%=decorateFontStyles ? "checked=\"checked\"" : "" %> /> Decorate font styles</label>
						</div>
					</div>
					<fieldset id="ParserLogFieldset" style="margin-top: 8px; height: 106px">
						<legend>Parser Log:</legend>
						<div id="ParserLog" class="Shaded" style="height: 87%"><%=parserLog%></div>
					</fieldset>
				</div>
			</div>
		</form>
	</body>
</html>