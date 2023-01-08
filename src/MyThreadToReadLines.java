import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MyThreadToReadLines extends Thread {
    private String file_to_run_on;
    private static AtomicInteger num_of_lines_calculated_until_now = new AtomicInteger(0);
    //private static volatile int num_of_lines_calculated_until_now;


    public MyThreadToReadLines(String filename) {
        this.file_to_run_on = filename;
    }

    ///private synchronized void syncAdd(int num_of_lines_to_add){
        ///num_of_lines_calculated_until_now+=num_of_lines_to_add;
    ///}

    @Override
    public void run() {
        int num_of_lines_in_file = 0;
        File our_file = new File(this.file_to_run_on+".txt");
        FileReader reader;
        BufferedReader br = null;
        String line;
        try {
            reader = new FileReader(our_file);
            br = new BufferedReader(reader);
            try {
                line = br.readLine();
                while (line != null) {
                    num_of_lines_in_file++;
                    line = br.readLine();
                }
                num_of_lines_calculated_until_now.addAndGet(num_of_lines_in_file);
            } catch (IOException e) {
                System.out.println("An error occurred while reading the lines of file " + this.file_to_run_on);
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while opening file " + this.file_to_run_on + " - file not found");
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

    public static int getNum_of_lines_calculated_until_now(){
        return num_of_lines_calculated_until_now.intValue();
    }
}
