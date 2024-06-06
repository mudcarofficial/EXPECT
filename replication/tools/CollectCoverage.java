import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectCoverage {
    public static void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFolder(file);
                }
            }
        }
        folder.delete();
    }

    public static void main(String[] args) throws IOException {
        List<List<String>> coverageinfo = new ArrayList<>();
        File file = new File("JACOCO REPORT PATH");
        File[] files = file.listFiles();
        BufferedReader reader;
        Pattern pat = Pattern.compile(".*<span\sclass=\"(.*?)\"\sid=\"L(.*?)\"");
        for(int i=0; i<files.length; i++){
            //Add condition judges to filter unnecessary coverage information
            if(files[i].isDirectory()){
                File[] covfiles = files[i].listFiles();
                for(int j=0; j<covfiles.length; j++){
                    String[] splits = covfiles[j].getName().split("\\.");
                    if(splits.length>1 && splits[1].equals("java")){
                        reader = new BufferedReader(new FileReader("JACOCO REPORT PATH/" + files[i].getName() + "/" + covfiles[j].getName()));
                        String line = reader.readLine();
                        while (line != null) {
                            Matcher matcher = pat.matcher(line);
                            if(matcher.find()){
                                List<String> singleline = new ArrayList<>();
                                singleline.add(splits[0]);
                                singleline.add(matcher.group(2));
                                if(matcher.group(1).equals("nc") || matcher.group(1).substring(0,1).equals("n")){
                                    singleline.add("0");
                                }
                                else {
                                    singleline.add("1");
                                }
                                coverageinfo.add(singleline);
                            }
                            line = reader.readLine();
                        }
                        reader.close();
                    }
                }
            }
        }
        try {
            File outputfile = new File("OUTPUT PATH\\" + args[0] + "_" + args[1] + ".txt");
            if (!outputfile.exists()) {
                outputfile.createNewFile();
            }
            FileWriter fw = new FileWriter(outputfile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for(int i=0; i<coverageinfo.size(); i++){
                for (int j=0; j<3; j++){
                    bw.write(coverageinfo.get(i).get(j));
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File folder = new File("JACOCO REPORT PATH");
        deleteFolder(folder);
        return;
    }
}