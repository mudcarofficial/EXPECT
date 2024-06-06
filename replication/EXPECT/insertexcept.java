import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class insertexcept {
    public static void insert(String path) throws IOException {File file = new File(path);
        File[] files = file.listFiles();
        for(int i=0; i<files.length; i++){
            if(files[i].isDirectory()){
                insert(path + "\\" + files[i].getName());
            }
            else{
                File inFile = new File(path + "\\" + files[i].getName());
                File outFile = File.createTempFile("name", ".tmp");
                FileInputStream fis = new FileInputStream(inFile);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                FileOutputStream fos = new FileOutputStream(outFile);
                PrintWriter out = new PrintWriter(fos);
                String lastLine = "";
                String thisLine;
                String cname = files[i].getName();
                int trynum = 1;
                Pattern p1 = Pattern.compile("try\s*\\{");
                Pattern p11 = Pattern.compile("try\s*\\(");
                Pattern p2 = Pattern.compile("catch.*\\{");
                Pattern p3 = Pattern.compile("finally\s*\\{");
                Pattern p4 = Pattern.compile("}");
                boolean intryblock = false;
                boolean incatchblock = false;
                boolean tryfound = false;
                boolean writefinally = false;
                boolean write = false;
                int count = 0;
                while ((thisLine = in.readLine()) != null) {
                    out.println(lastLine);
                    if(writefinally){
                        //Replace to other printing statements at your will
                        out.println("System.out.println(\"End:" + cname + "_" + trynum + "_\"+sss);");
                        trynum += 1;
                        tryfound = false;
                        writefinally = false;
                    }
                    if(write){
                        out.println("finally {");
                        //Replace to other printing statements at your will
                        out.println("System.out.println(\"End:" + cname + "_" + trynum + "_\"+sss);");
                        out.println("}");
                        trynum += 1;
                        tryfound = false;
                        write = false;
                    }
                    if(intryblock){
                        boolean useless1 = false;
                        boolean useless2 = false;
                        for(int index=0; index<thisLine.length(); index++){
                            char c = thisLine.charAt(index);
                            if(c == '\'' && useless1 == false){
                                useless1 = true;
                                continue;
                            }
                            if(c == '\"' && useless2 == false){
                                useless2 = true;
                                continue;
                            }
                            if(c == '\'' && useless1 == true){
                                useless1 = false;
                                continue;
                            }
                            if(c == '\"' && useless2 == true){
                                useless2 = false;
                                continue;
                            }
                            if(useless1 || useless2){
                                continue;
                            }
                            if(c == '{'){
                                count += 1;
                            }
                            else if(c == '}'){
                                count -= 1;
                                if(count == 0){
                                    intryblock = false;
                                    out.println("sss = \"0\";");
                                    break;
                                }
                            }
                        }
                        if(intryblock){
                            lastLine = thisLine;
                            continue;
                        }
                    }
                    else if(incatchblock){
                        boolean useless1 = false;
                        boolean useless2 = false;
                        for(int index=0; index<thisLine.length(); index++){
                            char c = thisLine.charAt(index);
                            if(c == '\'' && useless1 == false){
                                useless1 = true;
                                continue;
                            }
                            if(c == '\"' && useless2 == false){
                                useless2 = true;
                                continue;
                            }
                            if(c == '\'' && useless1 == true){
                                useless1 = false;
                                continue;
                            }
                            if(c == '\"' && useless2 == true){
                                useless2 = false;
                                continue;
                            }
                            if(useless1 || useless2){
                                continue;
                            }
                            if(c == '{'){
                                count += 1;
                            }
                            else if(c == '}'){
                                count -= 1;
                                if(count == 0){
                                    incatchblock = false;
                                    break;
                                }
                            }
                        }
                        if(incatchblock){
                            lastLine = thisLine;
                            continue;
                        }
                    }
                    Matcher m1 = p1.matcher(thisLine);
                    Matcher m11 = p11.matcher(thisLine);
                    Matcher m2 = p2.matcher(thisLine);
                    Matcher m3 = p3.matcher(thisLine);
                    if(!tryfound && m11.find()){
                        out.println("String sss = \"1\";");
                        //Replace to other printing statements at your will
                        out.println("System.out.println(\"Begin:" + cname + "_" + trynum + "\");");
                        int ko = 0;
                        boolean matchend = false;
                        while(true){
                            for(int index=0; index<thisLine.length(); index++){
                                char c = thisLine.charAt(index);
                                if(c == '('){
                                    ko += 1;
                                }
                                else if(c == ')'){
                                    ko -= 1;
                                    if(ko == 0){
                                        matchend = true;
                                        break;
                                    }
                                }
                            }
                            if(matchend == true){
                                Matcher m4 = p4.matcher(thisLine);
                                if(m4.find()){
                                    System.out.println(cname + "---------error");
                                }
                                intryblock = true;
                                count += 1;
                                tryfound = true;
                                break;
                            }
                            else{
                                lastLine = thisLine;
                                out.println(lastLine);
                                thisLine = in.readLine();
                            }
                        }
                    }
                    else if(!tryfound && m1.find()){
                        Matcher m4 = p4.matcher(thisLine);
                        if(!m4.find()){
                            out.println("String sss = \"1\";");
                            //Replace to other printing statements at your will
                            out.println("System.out.println(\"Begin:" + cname + "_" + trynum + "\");");
                            intryblock = true;
                            count += 1;
                            tryfound = true;
                        }
                    }
                    else if(tryfound && m2.find()){
                        incatchblock = true;
                        count += 1;
                    }
                    else if(tryfound && m3.find()){
                        writefinally = true;
                    }
                    else if(tryfound){
                        write = true;
                    }
                    lastLine = thisLine;
                }
                out.println(lastLine);
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