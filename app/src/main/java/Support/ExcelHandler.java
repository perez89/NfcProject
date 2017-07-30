package Support;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import static android.R.attr.path;


/**
 * Created by User on 17/07/2017.
 */

public class ExcelHandler{
    private WritableWorkbook m_workbook;
   private WritableSheet sheet;
    private File path;
    int row  = 0;
    int column = 0;
    private List<LocalMonth> list = new ArrayList<LocalMonth>();
    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;
    private String inputFile;

    public ExcelHandler(Context context, String fileName, List<LocalMonth> list){


       // if(list != null && !list.isEmpty()){
            this.list = list;
        createFileDirectory(context, fileName);
            if(!path.exists())
            {
                try
                {
                    write();
                   // createWorkBook();
                   // createSheet();
                   // createPages();
                }
                catch (Exception e)
                {

                }
            }
        //}
    }

    public ExcelHandler(Context context, List<LocalMonth> list){
        String fileName= "nfcExcel"+001;

        // if(list != null && !list.isEmpty()){
        createFileDirectory(context, fileName);
        this.list = list;
        if(!path.exists())
        {
            try
            {
                write();
            }
            catch (Exception e)
            {

            }
        }
        //}
    }

    public void write() throws IOException, WriteException {
        CreateWorkBook();
        WritableSheet sheet = CreateSheet("Schedule", 0);

        createPages(sheet);

        createLabel(excelSheet);
        createContent(excelSheet);
        WriteAndClose();
    }
    private void WriteAndClose() throws IOException {

        try {
            if(m_workbook!=null){
                m_workbook.write();
                m_workbook.close();
            }
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    private void createFileDirectory(Context context, String fileName){
        path=new File(context.getFilesDir(), fileName);
    }

    private void CreateWorkBook() throws IOException {
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        m_workbook = Workbook.createWorkbook(path ,wbSettings);
    }

    private WritableSheet CreateSheet(String sheetName, int pos) throws IOException {
        m_workbook.createSheet(sheetName, pos);
        WritableSheet excelSheet = m_workbook.getSheet(pos);
        return excelSheet;
    }

    private void createLabel(WritableSheet sheet)
            throws WriteException {
        // Lets create a times font
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);


        // create create a bold font with unterlines
        WritableFont times10ptBoldUnderline = new WritableFont(
                WritableFont.TIMES, 10, WritableFont.BOLD, false,
                UnderlineStyle.SINGLE);

        CellView cv = new CellView();

        cv.setAutosize(true);

        // Write a few headers
        addCaption(sheet, 0, 0, "Header 1");
        addCaption(sheet, 1, 0, "This is another header");


    }

    private void createContent(WritableSheet sheet) throws WriteException,
            RowsExceededException {
        // Write a few number
        for (int i = 1; i < 10; i++) {
            // First column
            addNumber(sheet, 0, i, i + 10);
            // Second column
            addNumber(sheet, 1, i, i * i);
        }
        // now a bit of text
        for (int i = 12; i < 20; i++) {
            // First column
            addLabel(sheet, 0, i, "Boring text " + i);
            // Second column
            addLabel(sheet, 1, i, "Another text");
        }
    }

    private void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException {
        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    private void addNumber(WritableSheet sheet, int column, int row,
                           Integer integer) throws WriteException, RowsExceededException {
        Number number;
        number = new Number(column, row, integer, times);
        sheet.addCell(number);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {
        Label label;
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }





    private void createPages() throws WriteException {
        // this will create new new sheet in workbook

        //new Month
        for (LocalMonth month: list
             ) {

            //new Week
            for (LocalWeek week: month.getListOfWeeks()
                    ) {

                //new Day
                for (LocalDay day: week.getListOfDays()
                        ) {
                   createRowEvents(day);
                }
            row++;
            }
            row++;
        }

        //LocalEvent ox = new LocalEvent();
       // if(ox.getData().getStartTime())
         // this will add label in excel sheet
        Label label1 = new Label(0, 0, "id");
        //sheet.addCell(label1);

        Label label2 = new Label(1, 0, "hobbies");
        //sheet.addCell(label2);

        Label m_idValue = new Label(0,1,"1");
       // sheet.addCell(m_idValue);

        Label m_idValu2e = new Label(1,1,"2");
     //   sheet.addCell(m_idValue);
    }

    private void createRowEvents(LocalDay day) throws WriteException {
        for (LocalEvent event : day.getListOfEvents())
        {
            Object olx = event.getData().getStartTime();
            createCell(olx+"");
            column++;
        }
    }

    private void createCell(String text) throws WriteException {
        Label label = new Label(column, row, text);
       // sheet.addCell(label);
    }

}
