package benblamey.LatexToHTML;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * 
 * Use in combination with these firefox extensions:
 * 
 * https://addons.mozilla.org/en-US/firefox/addon/auto-reload/
 * https://addons.mozilla.org/en-US/firefox/addon/headingsmap/
 * 
 * @author Ben
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		String source = "C:\\work\\docs\\PHD_Work\\writing\\thesis.tex";
		
		

//		Path dir =P 
//				
//				new Path("C:\\work\\docs\\PHD_Work\\writing\\");
//		
//		try {
//			
//			
//			
//		    WatchKey key = dir.register(watcher,
//		                           ENTRY_CREATE,
//		                           ENTRY_DELETE,
//		                           ENTRY_MODIFY);
//		} catch (IOException x) {
//		    System.err.println(x);
//		}
		
		DoIT(source);
	}

	private static void DoIT(String source) throws IOException {
		String latex = readFile(source, Charset.defaultCharset());
		
	
		String newLatex = "";
		
		boolean inPara = false;
		boolean inTable = false;
		boolean inTabular = false;
		boolean inFigure = false;
		boolean docStarted = false;
		
		final String headerCharacters = "^\\}";//"A-Za-z0-9 :\\(\\)\\+\\-\\.";
		
		for (String line : latex.split("([\\n\\r])")) {
			
			
			if (line.equals("\\begin{document}")) 
			{
				docStarted = true;
				continue;
			} else if (!docStarted) {
				continue;
			}
			
			
			if (line.contains("\\begin{table}") || line.contains("\\begin{longtable}")) {
				inTable = true;
				newLatex += "[Table Removed.]";
				continue;
			} else if (line.contains("\\end{table}")|| line.contains("\\end{longtable}")) {
				inTable = false;
				continue;
			} else if (inTable) {
				continue;
			}
			
			
			if (line.contains("\\begin{figure}")) {
				inFigure = true;
				newLatex += "[Figure Removed.]";
				continue;
			} else if (line.contains("\\end{figure}")) {
				inFigure = false;
				continue;
			} else if (inFigure) {
				continue;
			}
			
			if (line.contains("\\begin{tabular}")) {
				inTabular = true;
				newLatex += "[Tabular Env. Removed.]";
				continue;
			} else if (line.contains("\\end{tabular}")) {
				inTabular = false;
				continue;
			} else if (inTabular) {
				continue;
			}
			
			//System.out.println("Processing line: " + line);

			{
			Matcher comments = Pattern.compile(" %(.*)$").matcher(line);
			line = comments.replaceAll("");
			}
			{
				Matcher comments = Pattern.compile("^%(.*)$").matcher(line);
				line = comments.replaceAll("");
			}
			
			line = Pattern.compile("\\\\usepackage.*").matcher(line).replaceAll("");
			
			line = Pattern.compile("\\\\maketitle").matcher(line).replaceAll("");
			line = Pattern.compile("\\\\tableofcontents").matcher(line).replaceAll("");
			line = Pattern.compile("\\\\listoffigures").matcher(line).replaceAll("");
			line = Pattern.compile("\\\\date\\{\\\\today\\}").matcher(line).replaceAll("");
			
			line = Pattern.compile("\\clearpage").matcher(line).replaceAll("");
			line = Pattern.compile("\\\\date\\{\\\\today\\}").matcher(line).replaceAll("");
			line = Pattern.compile("\\\\date\\{\\\\today\\}").matcher(line).replaceAll("");
			line = Pattern.compile("\\\\date\\{\\\\today\\}").matcher(line).replaceAll("");
			line = Pattern.compile("\\\\date\\{\\\\today\\}").matcher(line).replaceAll("");
			
			
			line = line.replace("\\clearpage", "");
			line = line.replace("\\addcontentsline{toc}{chapter}{Bibliography}", "");
			line = line.replace("\\printbibliography", "");
			line = line.replace("\\end{document}", "");
			line = line.replace("\\begin{landscape}", "");
			line = line.replace("\\end{landscape}", "");
			

			Matcher title = Pattern.compile("\\\\title\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line = title.replaceAll("<h1>$1</h1>");
			
			Matcher author = Pattern.compile("\\\\author\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line = author.replaceAll("<p>by $1.</p>");
			
			
			Matcher chapter = Pattern.compile("\\\\chapter\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line = chapter.replaceAll("<h2>$1</h2>");

			Matcher section = Pattern.compile("\\\\section\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line  = section.replaceAll("<h3>$1</h3>");
			
			Matcher subsection = Pattern.compile("\\\\subsection\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line  = subsection.replaceAll("<h4>$1</h4>");
			
			Matcher subsubsection = Pattern.compile("\\\\subsubsection\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line  = subsubsection.replaceAll("<h4>$1</h4>");
			
			Matcher paragraph = Pattern.compile("\\\\paragraph\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line  = paragraph.replaceAll("<h5>$1</h5>");
			
			
			
			line = Pattern.compile("\\\\begin\\{description\\}\\[.*\\]").matcher(line).replaceAll("<ul>");
			line = Pattern.compile("\\\\end\\{description\\}").matcher(line).replaceAll("</ul>");
			
			
			line = Pattern.compile("\\\\begin\\{itemize\\}").matcher(line).replaceAll("<ul>");
			line = Pattern.compile("\\\\end\\{itemize\\}").matcher(line).replaceAll("</ul>");
			
			line = Pattern.compile("\\\\begin\\{enumerate\\}").matcher(line).replaceAll("<ol>");
			line = Pattern.compile("\\\\end\\{enumerate\\}").matcher(line).replaceAll("</ol>");
			
			
			line = Pattern.compile("\\\\item\\[([^\\]]*)\\]").matcher(line).replaceAll("<li>");
			
			line = Pattern.compile("\\\\item (.*)$").matcher(line).replaceAll("<li>$1</li>");
			
			line = Pattern.compile("\\\\hline").matcher(line).replaceAll("<hr/>");
			

			
			line = line.replace("\\begin{quote}", "<q>");
			line = line.replace("\\end{quote}", "</q>");
			
			
			line = Pattern.compile("\\\\nocite"
					+"\\{([^\\}]*)\\}").matcher(line).replaceAll("");
			
			
			line = Pattern.compile("\\\\citep?t?"
					+"\\[([^\\]])*\\]"
					+"\\{([^\\}]*)\\}").matcher(line).replaceAll("<b>[pp. $1, $2]</b>");
			
			line = Pattern.compile("\\\\citep?t?"
					+"\\{([^\\}]*)\\}").matcher(line).replaceAll("<b>[$1]</b>");
			
			// Last stage, HTML entities.
			line = line.replace("~", "&nbsp;");
			line = line.replace("\\%", "&#37;");
			line = line.replace("\\#", "#");
			line = line.replace("\\&", "&amp;");
			line = line.replace("\\$", "&#36;");
			
			
			line = line.replace("\\rightarrow", "&gt;");
			line = line.replace("\\leftarrow", "&lt;");
			
			line = line.replace("\\footnotesize", "");
			
			line = line.replace("\\begin{verbatim}", "<pre>");
			line = line.replace("\\end{verbatim}", "</pre>");
			
			line = line.replace(".\\", "."); // "Non-full-stop"
			

			line = Pattern.compile("\\\\textbf\\{([^\\}]*)\\}").matcher(line).replaceAll("<b>$1</b>");
			
			line = Pattern.compile("\\\\label\\{([^\\}]*)\\}").matcher(line).replaceAll("<a name=\"$1\" ></a>");
			
			line = Pattern.compile("\\\\\"\\{([a-zA-Z])\\}").matcher(line).replaceAll("&$1uml;");
			
			
			
			// Finally, turn any remaining latex commands into comments:
//			if (Pattern.compile("\\\\(.*)").matcher(line).find()) {
//				System.out.println("Giving up on line: " + line);
//			}
//			line = Pattern.compile("\\\\(.*)").matcher(line).replaceAll("<!-- \\$1 -->");
//
//			
//			line = line.replace("\\\\([^\\{]*)\\{([^\\}])*\\}", "<!-- $1 $2-->");
			
			
			//line  = subsubsection

			
			boolean IsRealText = Pattern.compile("^([a-zA-Z])").matcher(line).find();
			
			
			
			if (IsRealText && !inPara)
			{
				newLatex += "<p>\n";
			}
			else if (!IsRealText && inPara) {
				newLatex += "</p>\n";
			}
			
			inPara = IsRealText;
			
			if (inPara) {

			}
			
			//
			
			newLatex += line + "\n";
			
		}
		
		// Things that might be spread over multiple lines.
		
		newLatex = Pattern.compile("\\\\ref\\{([^\\}]+)\\}").matcher(newLatex).replaceAll("<a href=\"#$1\">$1</a>");
		
		newLatex = Pattern.compile("\\\\url\\{([^\\}]+)\\}").matcher(newLatex).replaceAll("<a href=\"$1\">$1</a>");
		
		newLatex = Pattern.compile("\\\\emph\\{([^\\}]+)\\}").matcher(newLatex).replaceAll("<i>$1</i>");
		
		newLatex = Pattern.compile("\\\\footnote\\{([^\\}]+)\\}").matcher(newLatex).replaceAll("<a href=\"javascript:alert('$1')\">&dagger;</a>");
		
		
		// Matches over multiple lines by default.
		newLatex = Pattern.compile("\\\\benbox\\{([^\\}]*)\\}").matcher(newLatex).replaceAll("<div class=\"benbox\">$1</div>");
		
		
		
		newLatex = Pattern.compile("").matcher(newLatex).replaceAll("");
		
		
		// replaceAll => Latex
		newLatex = newLatex.replaceAll("\\\\begin\\{table\\}.+\\\\end\\{table\\}", "[Table Removed.]");
		
		
		final String before ="<!DOCTYPE html>\n" +
				"<html>\n" +
				"<head>\n" +
				"</head>\n" +
				"<body>\n";
		final String after ="</body>\n</html>\n";			
		String output = before + newLatex + after;
					
		writeFile(output);
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return encoding.decode(ByteBuffer.wrap(encoded)).toString();
			}
	

	static void writeFile(String yourstring) throws IOException {
		BufferedWriter writer = null;
	    writer = new BufferedWriter( new FileWriter( "C:\\work\\docs\\PHD_Work\\writing\\thesis.html"));
	    writer.write( yourstring);
	    writer.close( );
	}

}
