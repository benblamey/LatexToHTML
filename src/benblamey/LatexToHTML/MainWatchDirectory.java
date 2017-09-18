package benblamey.LatexToHTML;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

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
public class MainWatchDirectory {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		
		final String dir = "C:\\work\\docs\\PHD_Work\\writing\\";
		
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {
				String file = listOfFiles[i].getName();
				
				if (file.endsWith(".tex")) {
					LatexToHTML.latexToHTML(dir + file, false);
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
	           for (WatchEvent<?> event : events) {
                    String filename = event.context().toString();
                    if (filename.endsWith(".tex")) {          
                    	LatexToHTML.latexToHTML(dir + filename, false);
                    }
	           }
	           
	           watckKey.reset();
           }
           Thread.sleep(500);

        } while (true);
        
	}

}
