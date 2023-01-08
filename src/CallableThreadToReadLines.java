import java.io.*;
import java.util.concurrent.Callable;

public class CallableThreadToReadLines implements Callable {
    private String file_to_run_on;

    public CallableThreadToReadLines(String filename){
        this.file_to_run_on = filename;
    }

    @Override
    public Object call() throws Exception {
        int num_of_lines_in_file = 0;
        File our_file = new File(this.file_to_run_on + ".txt");
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
            } catch (IOException e) {
                System.out.println("An error occurred while reading the lines of file " + this.file_to_run_on);
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while opening file " + this.file_to_run_on + " - file not found");
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                System.out.println("An error occurred while closing BufferedReader");
            }
        }
        return num_of_lines_in_file;
    }

}
