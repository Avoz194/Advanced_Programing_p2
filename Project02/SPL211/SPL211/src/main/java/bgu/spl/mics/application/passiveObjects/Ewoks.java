package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.MessageBusImpl;

import java.util.*;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static class SingletonHolder {
        private static Ewoks instance = new Ewoks();
    }
    private Vector<Ewok> ewokVector; //vector because it is tread safe

    // private constructor
    private Ewoks() {
        this.ewokVector = new Vector<Ewok>(); // vector of ewoks
    }
    public static  Ewoks getInstance() {
        return Ewoks.SingletonHolder.instance;
    }

    public synchronized void init(int numOfEwoks){
        if(ewokVector.size()!=0){
            throw new IllegalArgumentException("the vector should be empty in this moment");
        }
        for (int i = 0; i < numOfEwoks; i++) {
            this.ewokVector.add(new Ewok(i));
        }
    }

//    public synchronized static Ewoks getInstance(int numOfEwoks) { // singleton instance checker
//        //TODO:revise
//        if (instance == null) {
//            instance = new Ewoks(numOfEwoks);
//        }
//        return instance;
//    }


    public void acquire(int[] ewoks) {
        //TODO: to change the collection to ArrayList
        sort(ewoks, 0, ewoks.length - 1);
        for (int i = 0; i < ewoks.length; i++) {
            ewokVector.elementAt(ewoks[i]).acquire();
        }
    }

    public void release(int[] ewoks) {
        for (int i = 0; i < ewoks.length; i++) {
            ewokVector.elementAt(ewoks[i]).release();
        }
    }

    //merge-sort algorithm in order to avoid sync problem with acquiring ewoks TODO: is too fancy ?
    public void merge(int[] arr, int l, int m, int r) {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        /* Create temp arrays */
        int L[] = new int[n1];
        int R[] = new int[n2];

        /*Copy data to temp arrays*/
        for (int i = 0; i < n1; ++i)
            L[i] = arr[l + i];
        for (int j = 0; j < n2; ++j)
            R[j] = arr[m + 1 + j];

        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarry array
        int k = l;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    // Main function that sorts arr[l..r] using
    // merge()
    public void sort(int arr[], int l, int r) {
        if (l < r) {
            // Find the middle point
            int m = (l + r) / 2;

            // Sort first and second halves
            sort(arr, l, m);
            sort(arr, m + 1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }
}
