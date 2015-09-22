<!DOCTYPE html>
<%--
  - Author: Martin Jericho
  - Created: 2006-08-19
  - Last Modified: 2011-10-28
  - Description: Demonstration of the SourceFormatter class of the Jericho HTML Parser
  - http://jericho.htmlparser.net/docs/javadoc/net/htmlparser/jericho/SourceFormatter.html
--%><%@ page info="Jericho HTML Parser - Source Formatter"
%><%@ page import="net.htmlparser.jericho.*, java.util.*, java.io.*, javax.servlet.*, javax.servlet.http.*"
%><%!
	private static final String[] INDENT_TYPES=new String[] {"tab","1","2","4","8"};
	private static final String[] INDENT_STRINGS=new String[] {"\t"," ","  ","    ","        "};
	private static final String[] INDENT_DESCRIPTIONS=new String[] {"Tab character","1 space","2 spaces","4 spaces","8 spaces"};
	
	private static final String DEFAULT_INDENT_STRING=INDENT_STRINGS[0];

	private static final HashMap indentStringMap=initIndentStringMap();

	private static String getIndentString(String indentType) {
		String indentString=(String)indentStringMap.get(indentType);
		return indentString!=null ? indentString : DEFAULT_INDENT_STRING;
	}

	private static HashMap initIndentStringMap() {
		HashMap map=new HashMap();
		for (int i=0; i<INDENT_TYPES.length; i++) map.put(INDENT_TYPES[i],INDENT_STRINGS[i]);
		return map;
	}
%><%
	Writer responseWriter=response.getWriter();
	String output="";
	String parserLog="";
	String sourceText=request.getParameter("SourceText");
	boolean initialise=sourceText==null;
	String indentString;
	boolean tidyTags;
	boolean collapseWhiteSpace;
	boolean indentAllElements;
	if (initialise) {
		indentString=DEFAULT_INDENT_STRING;
		tidyTags=true;
		collapseWhiteSpace=true;
		indentAllElements=false;
	} else {
		indentString=getIndentString(request.getParameter("IndentType"));
		tidyTags=request.getParameter("TidyTags")!=null;
		collapseWhiteSpace=request.getParameter("CollapseWhiteSpace")!=null;
		indentAllElements=request.getParameter("IndentAllElements")!=null;
		Source source=new Source(sourceText);
	 	Writer logWriter=new StringWriter();
		source.setLogger(new WriterLogger(logWriter));
		String rawOutput=source.getSourceFormatter().setIndentString(indentString).setTidyTags(tidyTags).setCollapseWhiteSpace(collapseWhiteSpace).setIndentAllElements(indentAllElements).toString();
		output=CharacterReference.encode(rawOutput);
		parserLog=CharacterReference.encodeWithWhiteSpaceFormatting(logWriter.toString());
	}
%><html lang="en">
	<head>
		<meta charset="utf-8" />
		<title>Jericho HTML Parser - Source Formatter</title>
		<meta name="description" content="Indent HTML source to make it more readable" />
		<link rel="stylesheet" type="text/css" href="../css/jericho.css" />
	</head>
	<body>
		<form class="fullscreen" method="post" accept-charset="utf-8">
			<div class="fullscreen-content">
				<div>
					<a style="float: right" title="Jericho HTML Parser Homepage" href="http://jericho.htmlparser.net/">Jericho HTML Parser</a>
					<h1>Jericho HTML Parser - Source Formatter</h1>
					<div style="float: right"><a href="http://jericho.htmlparser.net/docs/javadoc/net/htmlparser/jericho/SourceFormatter.html">click here for documentation</a></div>
					<div>Enter HTML to format below:</div>
				</div>
				<div id="textarea-container" style="top:50px; bottom:180px">
					<textarea style="width:100%; height:100%" id="SourceText" name="SourceText" class="Shaded"><%=output%></textarea>
				</div>
				<div id="bottom-segment" style="height:170px">
					<table style="width:100%">
						<tr>
							<td>
								<table>
									<tr>
										<td class="LabelColumn">Indent String:</td>
										<td>
											<% for (int i=0; i<INDENT_TYPES.length; i++) { %>
												<span class="HorizontalCheckboxOption"><input type="radio" class="Button" name="IndentType" value="<%=INDENT_TYPES[i]%>" <%=(indentString==INDENT_STRINGS[i]) ? "checked=\"checked\"" : "" %> /> <%=INDENT_DESCRIPTIONS[i]%></span>
											<% } %>
										</td>
									</tr>
									<tr>
										<td class="LabelColumn">options:</td>
										<td>
											<span class="HorizontalCheckboxOption" title="tidy up each tag as described in the Tag.tidy() method"><input type="checkbox" class="Button" name="TidyTags" value="Y" <%=tidyTags ? "checked=\"checked\"" : "" %> /> Tidy tags</span>
											<span class="HorizontalCheckboxOption" title="collapse the white space in the text between the tags"><input type="checkbox" class="Button" name="CollapseWhiteSpace" value="Y" <%=collapseWhiteSpace ? "checked=\"checked\"" : "" %> /> Collapse white space</span>
											<span class="HorizontalCheckboxOption" title="indent all elements, including inline-level elements and those with preformatted contents"><input type="checkbox" class="Button" name="IndentAllElements" value="Y" <%=indentAllElements ? "checked=\"checked\"" : "" %> /> Indent all elements <img src="../images/warning.gif" border="0" width="16" height="15" style="vertical-align: middle" title="WARNING: using this option is likely to result in output that is no longer functionally equivalent to the source" /></span>
										</td>
									</tr>
								</table>
							</td>
							<td style="text-align: right; vertical-align: bottom"><input type="submit" class="Button" value="Process"/></td>
						</tr>
					</table>
					<fieldset id="ParserLogFieldset" style="margin-top: 8px; height: 106px">
						<legend>Parser Log:</legend>
						<div id="ParserLog" class="Shaded" style="height: 87%"><%=parserLog%></div>
					</fieldset>
				</div>
			</div>
		</form>
	</body>
</html>