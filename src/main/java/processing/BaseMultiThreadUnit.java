package processing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import utils.Utils;

public class BaseMultiThreadUnit {

	protected ExecutorService execService;
	
	protected BaseMultiThreadUnit() {
		this.execService = Executors.newFixedThreadPool(Utils.THREAD_NUMB);
	}
	
	public void terminate() {
		this.execService.shutdown();
		try {
			this.execService.awaitTermination(Utils.TERMINATION_TIMEOUT_DAYS, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}
