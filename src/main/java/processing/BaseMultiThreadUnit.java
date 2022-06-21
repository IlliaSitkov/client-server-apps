package processing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.Utils;

public class BaseMultiThreadUnit {

	protected ExecutorService execService;
	
	protected BaseMultiThreadUnit() {
		this.execService = Executors.newFixedThreadPool(Utils.THREAD_NUMB);
	}
	
	public void terminate() {
		this.execService.shutdown();
	}
	
	
}
