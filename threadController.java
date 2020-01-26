//thread controller class
//allow you to wait for all threads to finish (low level implement of join() )
public class threadController {
	
	private int expectedNumOfThreads;
	private int threadsFinished=0;
	private int currentRunning=0;
	
	//constructor
	public threadController(int expectedNumOfThreads) {
	this.expectedNumOfThreads = expectedNumOfThreads;
	}
	
	//updating running threads count safely
	public synchronized void updateRunningThread()	 {currentRunning++;}
	
	//thread has finished, update count
	public synchronized void threadFinished() {
		threadsFinished++;
		currentRunning--;
		if(threadsFinished>=expectedNumOfThreads)
			notify();							//notify that im done with all threads and "mainthread" can wake up
	}
	
	//wait untill all expected threads has finished
	public synchronized void waitForThreads() {
		while(threadsFinished<expectedNumOfThreads)
			try {
				wait();		//"mainthread" will wait for other threads to finish - in practice its the calling thread,so milling in our case will wait.
			}catch(InterruptedException e) 	{System.out.println("thread controller interupted while waiting.");}
	}
	
	//getter
	public synchronized int getCurrentRunning() 	{return currentRunning;}

	public int getThreadsFinished() {
		return threadsFinished;
	}
	
}//end of class