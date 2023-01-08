import java.io.*;
import java.util.Random;
import java.util.concurrent.*;

public class Ex2_1 {
    public static String[] createTextFiles(int n, int seed, int bound) {
        Random new_random = new Random(seed);
        int num_of_lines_in_file;
        File cur_file;
        String file_name;
        FileWriter writer;
        BufferedWriter bw = null;
        String[] filenames = new String[n];
        for (int i = 0; i < n; i++) {
            file_name = "file_" + (i + 1);
            num_of_lines_in_file = new_random.nextInt(bound);
            try {
                cur_file = new File(file_name + ".txt");
                writer = new FileWriter(cur_file);
                if (num_of_lines_in_file == 0) {
                    bw=null;
                    filenames[i] = file_name;
                    continue;
                }
                bw = new BufferedWriter(writer);
                bw.write("This is line number 1 out of " + num_of_lines_in_file);
                for (int j = 1; j < num_of_lines_in_file; j++) {
                    bw.newLine();
                    bw.write("This is line number " + (j + 1) + " out of " + num_of_lines_in_file);
                }
                filenames[i] = file_name;
            } catch (IOException e) {
                System.out.println("An error occurred while crating file number " + i);
                filenames[i] = null;
                e.printStackTrace();
            }
            finally {
                try {
                    if (bw!=null){
                        bw.flush();
                        bw.close();
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred while closing BufferedWriter, for file "+i);
                    e.printStackTrace();
                }
            }
        }
        return filenames;
    }

    public static int getNumOfLines(String[] fileNames) {
        int num_of_lines = 0;
        File cur_file;
        FileReader reader;
        BufferedReader br=null;
        String line;
        for (int i = 0; i < fileNames.length; i++) {
            try {
                cur_file = new File(fileNames[i]+".txt");
                reader = new FileReader(cur_file);
                br = new BufferedReader(reader);
                try {
                    line = br.readLine();
                    while (line != null) {
                        num_of_lines++;
                        line = br.readLine();
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred while reading the lines of file " + fileNames[i]);
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred while opening file " + fileNames[i] + " - file not found");
                e.printStackTrace();
            }
            finally {
                try {
                    if (br!=null){
                        br.close();
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred while closing BufferedReader");
                }
            }
        }

        return num_of_lines;
    }

    public int getNumOfLinesThreads(String[] fileNames){
        MyThreadToReadLines[] threadsarr = new MyThreadToReadLines[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
           MyThreadToReadLines tempthread = new MyThreadToReadLines(fileNames[i]);
           threadsarr[i] = tempthread;
           tempthread.start();
        }
        for (int i = 0; i < threadsarr.length; i++) {
            if (threadsarr[i].isAlive()) {
                try {
                    threadsarr[i].join();
                } catch (InterruptedException e) {
                    System.out.println("Tried to join thread, but it's already finished");
                }
            }
        }
        return MyThreadToReadLines.getNum_of_lines_calculated_until_now();
    }

    public int getNumOfLinesThreadPool(String[] fileNames){
        ExecutorService pool = Executors.newFixedThreadPool(fileNames.length);
        Future<Integer>[] our_future = new Future[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            our_future[i] = pool.submit(new CallableThreadToReadLines(fileNames[i]));
        }
        pool.shutdown();
        int num_of_lines=0;
        for (int i = 0; i < our_future.length; i++) {
            try {
                num_of_lines+=our_future[i].get();
            } catch (InterruptedException e) {
                System.out.println("Didn't manage to get num of lines of file "+fileNames[i]+" - got interrupted");
                e.printStackTrace();
            } catch (ExecutionException e) {
                System.out.println("Didn't manage to get num of lines of file "+fileNames[i]+" - ExecutionException");
                e.printStackTrace();
            }
        }
        return num_of_lines;
    }
}
