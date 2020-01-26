import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//merge sort with multithreading implementation
public class ConcurrentSorter {

	private ArrayList<int[]> sharedArr = new ArrayList<int[]>(0);		//shared pool
	private int n;
	private int numThreads;
    private Object mutex = new Object();

	//constructor
	public ConcurrentSorter(int[] arr, int n, int m) {
		this.n=n;
		this.numThreads=m;
		
		//filling the shared pool
		for(int i =0; i<n ; i++)
		{
			int temp[] = new int[1];
			temp[0] = arr[i];
			sharedArr.add(temp);	
		}
			
	}//end of constructor
	
	//sort method itself
    public synchronized void mergeSort() {

        int threadID=1;
        int[] arr1;
        int[] arr2;
        int rounds= (int) Math.ceil( Math.log(n) / Math.log(2 ) );		//here we can see we truly get O(nlogn) complexity, the rounds are logn and each round will cost n
        ArrayList<mergindThread> threadArrList = new ArrayList<mergindThread>(0);

        // A thread pool
        ExecutorService executors = Executors.newFixedThreadPool( Math.min(numThreads,n) ); //limits number of running threads to m or n if m is grater
		threadController tc; 
        
        System.out.println("-> starting Concurrent-Merge-Sort ");
        System.out.println("-> expecting "+rounds + " rounds");

        // sending the work to worker, taking 2 first arrays and adding the merge one at the end of the list to maintain sorted arrays sizes.
        for(int i=0; i<rounds ; i++)	{
        	
        	System.out.println("\n-> starting round " + (i+1) + ":");
        	tc = new threadController( sharedArr.size()/2);
            
        	//we mutex the shared pool so we dont mess it when multithreads are attampting add or remove from it
        	synchronized(mutex) {
            	while(sharedArr.size()>1)
	            {
	            	arr1 = sharedArr.remove(0);
	            	arr2 = sharedArr.remove(0);
	            	threadArrList.add( new mergindThread(arr1,arr2,threadID++, tc) );		//filling our thread list for this round
	            }
            }
	        	
        	//execute thread list with our thread-running-limit with the executer
	        while(!threadArrList.isEmpty())
	        	executors.execute(threadArrList.remove(0));
	        	
	            tc.waitForThreads();		 //wait() for this round thread-workers to finish with our costum thread contoller (gave up join() and chose to implement in low level for practice)
        }
        
 
        executors.shutdown();        // shutdown the thread pool.
		System.out.println("\n-> ending Concurrent-Merge-Sort\n");

    }//end of method
	
	//our thread(worker-thread) class
	private class mergindThread extends Thread {
		
		public int threadID=0;
		private threadController tController;
		private int[] arr1;
		private int[] arr2;

		//constructor
		public mergindThread(int[] arr1 , int[] arr2 , int threadID, threadController tController) {
			this.threadID = threadID;
			this.tController = tController;
			this.arr1 = arr1;
			this.arr2 = arr2;
		}
		
		@Override
		public void run() {
		
			tController.updateRunningThread();	
			System.out.println("-> executing" +"\t" + "thread " + threadID + "\t" + "running threads: " + tController.getCurrentRunning());	
			
			int[] temp = merge(arr1,arr2); 	//merge 2 sub arrays given return 1 array sorted
			
            synchronized(mutex) {
            	sharedArr.add( temp);	//adding new merged array to end of list (taking alyaws first 2 up in calling method so arrays will stay the same size as much as possible)
            }
			
			System.out.println("-> finished  " + "\t" + "thread "+ threadID);
			tController.threadFinished();		//updates that thread has finished and if all threads finished sends notify()
		}
		
		
	}//end of thread class
	
	

    // Basic algorithm: it merges two consecutives sorted fragments
    private static int[] merge(int[] arr1, int[] arr2) {
        int[] mergedArr = new int[arr1.length+arr2.length];
        int i = 0, j=0, k=0;
        
        //add to merged array the lowest of 2 first
        while (i < arr1.length && j < arr2.length) {
            if (arr1[i] <= arr2[j]) 
            	mergedArr[k++] = arr1[i++];
            else 
            	mergedArr[k++] = arr2[j++];
        }
        
        //if there are leftOvers (we can asume they are sorted after we divided the main array to a 1 fregment int and start from there)
        if (i < arr1.length)
            System.arraycopy(arr1, i, mergedArr, k, arr1.length - i);
        if (j < arr2.length)
            System.arraycopy(arr2, j, mergedArr, k, arr2.length - j);
        
        return mergedArr;
    }//end of method
    
    //return the sorted array or null if not yet finished
	public int[] geSortedArray() {
		if(sharedArr.size()>=1)
			return sharedArr.get(sharedArr.size()-1);
		else return null;
	}//end of method
    
}//end of class
