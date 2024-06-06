import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class insertprint {
    public static void insert(String path) throws IOException{
        File covFile = new File("THE PATH OF COVERAGE FILE EXTRACTED BY tools\\CollectCoverage.java");
        FileInputStream covfis = new FileInputStream(covFile);
        BufferedReader covin = new BufferedReader(new InputStreamReader(covfis));
        Map<String, List<Integer>> statements = new HashMap<String, List<Integer>>();
        String firstLine;
        int secondLine;
        while ((firstLine = covin.readLine()) != null) {
            secondLine = Integer.parseInt(covin.readLine());
            if(!statements.containsKey(firstLine)){
                List<Integer> c = new ArrayList<Integer>();
                c.add(secondLine);
                statements.put(firstLine, c);
            }
            else{
                statements.get(firstLine).add(secondLine);
            }
            covin.readLine();
        }
        File file = new File(path);
        File[] files = file.listFiles();
        Pattern p1 = Pattern.compile("\s+else\s+");
        Pattern p2 = Pattern.compile("\s+catch\s+");
        Pattern p3 = Pattern.compile("\s+finally\s+");
        for(int i=0; i<files.length; i++) {
            if (files[i].isDirectory()) {
                insert(path + "\\" + files[i].getName());
            }
            else{
                File inFile = new File(path + "\\" + files[i].getName());
                File outFile = File.createTempFile("name", ".tmp");
                FileInputStream fis = new FileInputStream(inFile);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                FileOutputStream fos = new FileOutputStream(outFile);
                PrintWriter out = new PrintWriter(fos);
                String thisLine;
                int linenum = 0;
                String[] splits = files[i].getName().split("\\.");
                String cname = splits[0];
                List<Integer> locations = statements.get(cname);
                if(locations == null){
                    System.out.println(cname);
                }
                int index = 0;
                while ((thisLine = in.readLine()) != null) {
                    linenum ++;
                    Matcher m1 = p1.matcher(thisLine);
                    Matcher m2 = p2.matcher(thisLine);
                    Matcher m3 = p3.matcher(thisLine);
                    if(m1.find() || m2.find() || m3.find()){
                        if(locations != null && linenum == locations.get(index)){
                            if(index < locations.size() - 1){
                                index += 1;
                            }
                        }
                        out.println(thisLine);
                        continue;
                    }
                    if(locations != null && linenum == locations.get(index)){
                        //Replace to other printing statements at your will
                        out.println("System.out.println(\"" + cname + "_" + linenum + "\");");
                        if(index < locations.size() - 1){
                            index += 1;
                        }
                    }
                    out.println(thisLine);
                }
                out.flush();
                out.close();
                in.close();
                inFile.delete();
                outFile.renameTo(inFile);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        insert("THE PATH OF SOURCE CODE TO BE INSTRUMENTED");
    }
}