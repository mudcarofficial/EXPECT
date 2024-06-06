import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class SurefireReportReader {

    public static void main(String[] args) throws Exception {
        String reportPath = "SETTING THE SUREFIRE REPORT PATH HERE";
        String outputFilePath = "SETTING THE OUTPUT PATH HERE";
        List<TestResult> testResults = readSurefireReport(reportPath);
        writeTestResultsToExcel(testResults, outputFilePath);
    }

    private static List<TestResult> readSurefireReport(String reportPath) throws Exception {
        List<TestResult> testResults = new ArrayList<>();
        File dir = new File(reportPath);
        File[] reports = dir.listFiles();
        for (File report : reports) {
            if (report.isFile() && report.getName().endsWith(".xml")) {
                SAXReader reader = new SAXReader();
                Document document = reader.read(report);
                Element root = document.getRootElement();
                String className = root.attributeValue("name");
                List<Element> testCases = root.elements("testcase");
                for (Element testCase : testCases) {
                    String caseName = testCase.attributeValue("name");
                    List<Element> failures = testCase.elements("failure");
                    List<Element> errors = testCase.elements("error");
                    if (!failures.isEmpty() || !errors.isEmpty()) {
                        testResults.add(new TestResult(className, caseName, "1"));
                    } else {
                        testResults.add(new TestResult(className, caseName, "0"));
                    }
                }
            }
        }
        return testResults;
    }

    private static void writeTestResultsToExcel(List<TestResult> testResults, String outputFilePath) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Test Results");
        int rownum = 0;
        for (TestResult testResult : testResults) {
            Row row = sheet.createRow(rownum++);
            Cell classCell = row.createCell(0);
            classCell.setCellValue(testResult.getClassName());
            Cell caseCell = row.createCell(1);
            caseCell.setCellValue(testResult.getCaseName());
            Cell resultCell = row.createCell(2);
            resultCell.setCellValue(testResult.getResult());
        }
        FileOutputStream outputStream = new FileOutputStream(outputFilePath);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    private static class TestResult {
        private String className;
        private String caseName;
        private String result;

        public TestResult(String className, String caseName, String result) {
            this.className = className;
            this.caseName = caseName;
            this.result = result;
        }

        public String getClassName() {
            return className;
        }

        public String getCaseName() {
            return caseName;
        }

        public String getResult() {
            return result;
        }
    }

}
