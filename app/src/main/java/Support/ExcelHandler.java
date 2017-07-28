package Support;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

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

    public ExcelHandler(Context context, List<LocalMonth> list){
       // if(list != null && !list.isEmpty()){
            this.list = list;
            createFileDirectory(context);
            if(!path.exists())
            {
                try
                {
                    createWorkBook();
                    createSheet();
                    createPages();
                }
                catch (Exception e)
                {

                }
            }
        //}
    }

    private void createFileDirectory(Context context){
        String fileName= "nfcExcel"+001;
        path=new File(context.getFilesDir(),fileName);
    }

    private void createWorkBook() throws IOException {
        m_workbook = Workbook.createWorkbook(path);
    }

    private void createSheet() throws IOException {
        sheet = m_workbook.createSheet("Schedule", 0);
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
        sheet.addCell(label1);

        Label label2 = new Label(1, 0, "hobbies");
        sheet.addCell(label2);

        Label m_idValue = new Label(0,1,"1");
        sheet.addCell(m_idValue);

        Label m_idValu2e = new Label(1,1,"2");
        sheet.addCell(m_idValue);
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
        sheet.addCell(label);
    }

}
