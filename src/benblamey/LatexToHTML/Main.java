package benblamey.LatexToHTML;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		
		final String dir = "C:\\work\\docs\\PHD_Work\\thesis\\";
		
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {
				String file = listOfFiles[i].getName();
				
				if (file.endsWith(".tex")) {
					latexToHTML(dir + file);
				}
			}
		}
		
        //define a folder root
        Path myDir = Paths.get(dir);
        WatchService watcher = myDir.getFileSystem().newWatchService();
        myDir.register(watcher, ENTRY_MODIFY);

        do {

           WatchKey watckKey = watcher.poll();

           if (watckKey != null) {
	           List<WatchEvent<?>> events = watckKey.pollEvents();
	           for (WatchEvent event : events) {
                    String filename = event.context().toString();
                    if (filename.endsWith(".tex")) {          
                    	latexToHTML(dir + filename);
                    }
	           }
	           
	           watckKey.reset();
           }
           Thread.sleep(500);

        } while (true);
        
	}

	private static void latexToHTML(String sourcePath) throws IOException {
		
		System.out.println("Reading " + sourcePath + "...");
		
		String latex = readFile(sourcePath, Charset.defaultCharset());
	
		String newLatex = "";
		
		Text inPara = Text.NonPara;
		
		boolean inTable = false;
		boolean inTabular = false;
		boolean inFigure = false;
		boolean docStarted = false;
		
		final String headerCharacters = "^\\}";//"A-Za-z0-9 :\\(\\)\\+\\-\\.";
		
		for (String line : latex.split("([\\n\\r])")) {
			
			if (line.contains("The importance of reminiscence")) {
				"".toCharArray();
			}
			
			
			if (line.equals("\\begin{document}")) 
			{
				docStarted = true;
				continue;
			} else if (line.contains("\\chapter{") || line.contains("\\section{")) {
				docStarted = true;
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
			//line = comments.replaceAll("");
			}
			{
				Matcher comments = Pattern.compile("^%(.*)$").matcher(line);
				//comments.
				//line = comments.replaceAll("");
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
			line = author.replaceAll("<p class=\"author\">$1</p>");
			
			
			Matcher chapter = Pattern.compile("\\\\chapter\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line = chapter.replaceAll("<h2>$1</h2>");

			Matcher section = Pattern.compile("\\\\section\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line  = section.replaceAll("<h3>$1</h3>");
			
			Matcher subsection = Pattern.compile("\\\\subsection\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line  = subsection.replaceAll("<h4>$1</h4>");
			
			Matcher subsubsection = Pattern.compile("\\\\subsubsection\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line  = subsubsection.replaceAll("<h5>$1</h5>");
			
			Matcher paragraph = Pattern.compile("\\\\paragraph\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line  = paragraph.replaceAll("<h6>$1</h6>");
			
			
			
			line = Pattern.compile("\\\\begin\\{description\\}\\[.*\\]").matcher(line).replaceAll("<ul>");
			line = Pattern.compile("\\\\end\\{description\\}").matcher(line).replaceAll("</ul>");
			
			
			line = Pattern.compile("\\\\begin\\{itemize\\}").matcher(line).replaceAll("<ul>");
			Matcher endItemizeMatcher = Pattern.compile("\\\\end\\{itemize\\}").matcher(line);
			if (endItemizeMatcher.find()) {
				inPara = Text.NonPara;
			}
			line = endItemizeMatcher.replaceAll("</ul>");
			
			line = Pattern.compile("\\\\begin\\{enumerate\\}").matcher(line).replaceAll("<ol>");
			Matcher enumMatcher = Pattern.compile("\\\\end\\{enumerate\\}").matcher(line);
			if (enumMatcher.find()) {
				inPara = Text.NonPara;
			}
			line = enumMatcher.replaceAll("</ol>");
			
			
			Matcher itemMatcherAux = Pattern.compile("\\\\item\\[.*\\]").matcher(line);
			if (itemMatcherAux.find()) {
				inPara = Text.InPseudoPara;
			}
			line = itemMatcherAux.replaceAll("<li>");
			Matcher itemMatcher = Pattern.compile("\\\\item (.*)$").matcher(line);
			if (itemMatcher.find()) {
				inPara = Text.InPseudoPara;
			}
			line = itemMatcher.replaceAll("<li>$1</li>");
			
			
			line = Pattern.compile("\\\\hline").matcher(line).replaceAll("<hr/>");
			

			
			line = line.replace("\\begin{quote}", "<div class=\"blockquote\">");
			line = line.replace("\\end{quote}", "</div>");
			
			
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
			
			// I use these to mean "the reference in their bibliography".
			line = line.replace("{[}", "[ext:");
			line = line.replace("{]}", "]");
			
			
			
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

			
			
			// This needs some work, doesn't detect:
			//  \cite etc.
			//  escaped characters.
			boolean IsRealText = Pattern.compile("^[^\\\\\\{\\}< \\t]").matcher(line).find();
			
			
			switch (inPara) {
				case InNormalPara:
					if (!IsRealText) {
						newLatex += "</p>\n";
						inPara = Text.NonPara;
					}
					break;
				case InPseudoPara:
					//System.out.println("In Pseudo Para: " + line);
					// Do nothing.
					break;
				case NonPara:
					if (IsRealText)
					{
						//System.out.println("Starting Para: " + line);
						newLatex += "<p>\n";
						inPara = Text.InNormalPara;
					}
					break;
			}
			
						
			//
			
			newLatex += line + "\n";
			
		}
		
		// Things that might be spread over multiple lines.
		
		newLatex = Pattern.compile("\\\\ref\\{([^\\}]+)\\}").matcher(newLatex).replaceAll("<a href=\"#$1\">$1</a>");
		
		newLatex = Pattern.compile("\\\\url\\{([^\\}]+)\\}").matcher(newLatex).replaceAll("<a href=\"$1\">$1</a>");
		
		newLatex = Pattern.compile("\\\\emph\\{([^\\}]+)\\}").matcher(newLatex).replaceAll("<i>$1</i>");
		

		do {
			Matcher footnoteMatcher = Pattern.compile("\\\\footnote\\{([^\\}]+)\\}").matcher(newLatex);
			if (!footnoteMatcher.find()) {
				break;
			}
			
			String footnoteText = footnoteMatcher.group(1).toString();
			//footnoteText = EscapeJavascriptString(footnoteText);
			//System.out.println(footnoteText);
			
			newLatex = newLatex.substring(0, footnoteMatcher.start())
					
					+" (<i>Footnote: "+footnoteText+"</i>) "
					//+ "<a href=\"javascript:alert('"+footnoteText+"')\">&dagger;</a>"
					+ newLatex.substring(footnoteMatcher.end(), newLatex.length() );
		
			
			
		} while (true);
		
		
		// Matches over multiple lines by default.
		newLatex = Pattern.compile("\\\\benbox\\{([^\\}]*)\\}").matcher(newLatex).replaceAll("<div class=\"benbox\">$1</div>");
		
		
		
		newLatex = Pattern.compile("").matcher(newLatex).replaceAll("");
		
		
		// replaceAll => Latex
		newLatex = newLatex.replaceAll("\\\\begin\\{table\\}.+\\\\end\\{table\\}", "[Table Removed.]");
		
		
		final String before ="<!DOCTYPE html>\n" +
				"<html>\n" +
				"<head>\n" +
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"tex_styles.css\">\n"+
				"</head>\n" +
				"<body>\n";
		final String after ="</body>\n</html>\n";			
		String output = before + newLatex + after;
			
		writeFile(sourcePath.substring(0, sourcePath.length() - 4) + ".html", output);
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return encoding.decode(ByteBuffer.wrap(encoded)).toString();
			}
	

	static void writeFile(String path, String yourstring) throws IOException {
		BufferedWriter writer = null;
	    writer = new BufferedWriter( new FileWriter( path));
	    writer.write( yourstring);
	    writer.close( );
	    
	    System.out.println("Finished writing " + path);
	}
	
	static String EscapeJavascriptString(String s) {
		return s.replace("\"", "\\\"").replace("'", "\\\'");
	}

}
