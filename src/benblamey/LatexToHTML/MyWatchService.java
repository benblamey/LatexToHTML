package benblamey.LatexToHTML;

import java.io.IOException;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

public class MyWatchService implements WatchService {

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WatchKey poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WatchKey poll(long timeout, TimeUnit unit)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WatchKey take() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

}
