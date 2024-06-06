import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class collect {
    public static boolean startsWithAny(String input, HashSet<String> set) {
        Pattern p = Pattern.compile("_[0-9]+");
        Matcher m = p.matcher(input);
        boolean find = m.find();
        for (String str : set) {
            if (input.startsWith(str) && find) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        File covFile = new File("THE PATH OF COVERAGE FILE EXTRACTED BY tools\\CollectCoverage.java");
        FileInputStream covfis = new FileInputStream(covFile);
        BufferedReader covin = new BufferedReader(new InputStreamReader(covfis));
        HashSet<String> c = new HashSet<String>();
        String firstLine;
        while ((firstLine = covin.readLine()) != null) {
            c.add(firstLine);
            covin.readLine();
            covin.readLine();
        }

        File f = new File("PATH TO SAVE THE EXCEPTION TRIGGER STREAM");
        if(!f.exists()){
            f.mkdir();
        }
        String sourceFile = "TEMPORARY PATH TO SAVE THE LOG FILE";
        String targetFile = "PATH TO SAVE THE EXCEPTION TRIGGER STREAM\\"  + args[0] + "_" + args[1] + ".txt";
        try {
            FileInputStream fis = new FileInputStream(sourceFile);
            InputStreamReader isr=new InputStreamReader(fis, "UTF-16le");
            BufferedReader br = new BufferedReader(isr);
            FileOutputStream fos=new FileOutputStream(new File(targetFile), true);
            OutputStreamWriter osw=new OutputStreamWriter(fos, "UTF-16le");
            BufferedWriter  bw=new BufferedWriter(osw);
            String line;
            while ((line = br.readLine()) != null) {
                Pattern p1 = Pattern.compile("^Begin:");
                Pattern p2 = Pattern.compile("^End:");
                Matcher m1 = p1.matcher(line);
                Matcher m2 = p2.matcher(line);
                if (m1.find() || m2.find() || startsWithAny(line, c)) {
                    bw.write(line);
                    bw.newLine();
                }
            }
            bw.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
