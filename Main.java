import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JOptionPane;

//nir nicole, mmn 15
//concurrent merge sort implementation
//main class.
public class Main{

	public static void main(String[] args) {
		
		int[] arr;
		int n=0;
		int m=0;

		//taking value as command line argument.
        Scanner in = new Scanner(System.in); 
        try {
	        System.out.println("Please enter array length:");
	        n = in.nextInt();
	        System.out.println("Please enter number of processes:");
	        m = in.nextInt();
        }catch(NumberFormatException e) { System.out.println("Error: \nincorrect syntax for input number."); }
		
		System.out.println("-> main \trandom array:");
		//create our random array
		arr = new int[n];
		for(int i=0; i<n; i++)
			{
			arr[i] = ThreadLocalRandom.current().nextInt(1, 100 + 1);
			System.out.print(arr[i] + "  ");
			}

		ConcurrentSorter concurrentMergeSorter = new ConcurrentSorter(arr,n,m);	//create sorter instance
		
		System.out.println("\n\n-> main \t->mergeSort");
		concurrentMergeSorter.mergeSort();
		
		System.out.println("-> main \tsorted array:");
		for(int i=0; i<concurrentMergeSorter.geSortedArray().length ; i++)
			System.out.print(concurrentMergeSorter.geSortedArray()[i]+ " ");		//showing the sorted array
		
	}//end of main

}//end of class
