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
			
			//System.out.println("Processing line: " + line);

			{
			Matcher comments = Pattern.compile(" %(.*)$").matcher(line);
			line = comments.replaceAll("<!-- $1 -->");
			}
			{
				Matcher comments = Pattern.compile("^%(.*)$").matcher(line);
				line = comments.replaceAll("<!-- $1 -->");
			}
			
			line = Pattern.compile("\\\\usepackage.*").matcher(line).replaceAll("");
			
			line = Pattern.compile("\\\\maketitle").matcher(line).replaceAll("");
			line = Pattern.compile("\\\\tableofcontents").matcher(line).replaceAll("");
			line = Pattern.compile("\\\\listoffigures").matcher(line).replaceAll("");
			line = Pattern.compile("\\\\date\\{\\\\today\\}").matcher(line).replaceAll("");
			
			
			

			Matcher title = Pattern.compile("\\\\title\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line = title.replaceAll("<h1>$1</h1>");
			
			Matcher author = Pattern.compile("\\\\author\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line = author.replaceAll("<p>$1</p>");
			
			
			Matcher chapter = Pattern.compile("\\\\chapter\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line = chapter.replaceAll("<h2>$1</h2>");

			Matcher section = Pattern.compile("\\\\section\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line  = section.replaceAll("<h3>$1</h3>");
			
			Matcher subsection = Pattern.compile("\\\\subsection\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line  = subsection.replaceAll("<h4>$1</h4>");
			
			Matcher subsubsection = Pattern.compile("\\\\subsubsection\\{(["+headerCharacters+"]+)\\}").matcher(line);
			line  = subsubsection.replaceAll("<h4>$1</h4>");
			
			
			
					line = Pattern.compile("\\\\begin\\{description\\}\\[.*\\]").matcher(line).replaceAll("<ul>");
					line = Pattern.compile("\\\\end\\{description\\}").matcher(line).replaceAll("</ul>");
			
			
			line = Pattern.compile("\\\\begin\\{itemize\\}").matcher(line).replaceAll("<ul>");
			line = Pattern.compile("\\\\end\\{itemize\\}").matcher(line).replaceAll("</ul>");
			
			line = Pattern.compile("\\\\begin\\{enumerate\\}").matcher(line).replaceAll("<ol>");
			line = Pattern.compile("\\\\end\\{enumerate\\}").matcher(line).replaceAll("</ol>");
			
			
			line = Pattern.compile("\\\\item\\[([^\\]]*)\\]$").matcher(line).replaceAll("<li>$1</li>");
			
			line = Pattern.compile("\\\\item (.*)$").matcher(line).replaceAll("<li>$1</li>");
			
			line = Pattern.compile("\\\\hline").matcher(line).replaceAll("<hr/>");
			
			
			
			
			line = Pattern.compile("\\\\citep?t?"
					+"\\[([^\\]])*\\]"
					+"\\{([^\\}]*)\\}").matcher(line).replaceAll("<b>[pp. $1, $2]</b>");
			
			line = Pattern.compile("\\\\citep?t?"
					+"\\{([^\\}]*)\\}").matcher(line).replaceAll("<b>[$1]</b>");
			
			// Last stage, HTML entities.
			line = line.replace("~", "&nbsp;");
			line = line.replace("\\%", "&#37;");
			

			line = Pattern.compile("\\\\textbf\\{([^\\}]*)\\}").matcher(line).replaceAll("<b>$1</b>");
			
			line = Pattern.compile("\\\\label\\{([^\\}]*)\\}").matcher(line).replaceAll("<a name=\"$1\" ></a>");
			
			
			
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
		
		
		// Matches over multiple lines by default.
		newLatex = Pattern.compile("\\\\benbox\\{([^\\}]*)\\}").matcher(newLatex).replaceAll("<div class=\"benbox\">$1</div>");
		
		
		final String before ="<!DOCTYPE html>" +
				"<html>" +
				"<head>" +
				"</head>" +
				"<body>";
		final String after ="</body></html>";			
		String output = before + newLatex + after;
					
		writeFile(output);
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return encoding.decode(ByteBuffer.wrap(encoded)).toString();
			}
	
	

	static void writeFile(String yourstring) {
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter( new FileWriter( "C:\\work\\docs\\PHD_Work\\writing\\thesis_html.html"));
		    writer.write( yourstring);

		}
		catch ( IOException e)
		{
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		    }
		    catch ( IOException e)
		    {
		    }
		}
	}

}
