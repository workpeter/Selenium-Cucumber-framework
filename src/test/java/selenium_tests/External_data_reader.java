package selenium_tests;

import java.io.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

public class External_data_reader{

	File file;
	FileInputStream fis;
	FileOutputStream fos;
	Workbook wb;
	Sheet ws;
	Row row;
	Cell cell;

	//TO-DO
	//Add code to create backup of xls file incase it gets corrupt.

	public External_data_reader(String filePath, String tabName) throws IOException {

		file = new File(filePath);
		fis = new FileInputStream(file);
		wb = new HSSFWorkbook(fis);
		ws = wb.getSheet(tabName);

	}

	public int getNumberOfRows() {

		return ws.getPhysicalNumberOfRows();

	}

	public int getNumberOfColumns() {

		return ws.getRow(0).getPhysicalNumberOfCells();

	}	

	public Object[][] extractDataProvider(int startingRow, int startingCol){

		System.out.println("(@extractDataProvider) " + "Total Row:" + String.valueOf(getNumberOfRows()) +  " Total Columns:" + String.valueOf(getNumberOfColumns()));

		//Create multiDimensional array object to return to data provider. -1 row to not include title row in data provider loop count
		Object[][] data= new Object [getNumberOfRows()-1][getNumberOfColumns()];

		for (int i =startingRow;i<=getNumberOfRows();i++) {
			for (int y =startingCol;y<=getNumberOfColumns();y++) {

				data[i-startingRow][y-startingCol] = readData(i,y);

			}
		}

		return data;
	}

	public String readData(int rowNumber,int columnNumber ) {


		//prevent null pointer exception if row doesnt exist, create it.
		if (rowNumber> getNumberOfRows()) {
			ws.createRow(rowNumber-1);
		}

		//allow calling method to use actual cell number as if starting from 1, rather than 0
		rowNumber--;
		columnNumber--;

		row = ws.getRow(rowNumber);		
		cell = row.getCell(columnNumber);	

		//Get value of cell and format to string if its numeric
		DataFormatter formatter = new DataFormatter();
		String value = formatter.formatCellValue(cell);

		//get cell value if not empty
		if (cell == null) {

			String warning = "The referenced cell cell(" + ++columnNumber + "," + ++rowNumber + ") has no data";
			System.out.println(warning);

			return null;

		}

		return value;

	}

	public void writeData(int rowNumber,int columnNumber, String value ) throws IOException {

		fos = new FileOutputStream(file);

		//prevent null pointer exception if row doesnt exist, create it.
		if (rowNumber> getNumberOfRows()) {
			ws.createRow(rowNumber-1);

		}

		//allow calling method to use actual cell number as if starting from 1, rather than 0
		rowNumber--;
		columnNumber--;

		row = ws.getRow(rowNumber);
		cell = row.getCell(columnNumber);

		if (cell == null){
			cell = row.createCell(columnNumber);
		}

		cell.setCellValue(value);		

		wb.write(fos);

	}

	public int findDataRow(String value){

		System.out.println("Searching for data in: "+ file.getName());

		for (int col =1;col<=getNumberOfColumns();col++) {
			for (int row =1;row<=getNumberOfRows();row++) {


				if (value.equals(readData(row,col))){

					System.out.println("Found on row: " + row + " in file: " + file.getName() );
					return row;

				}

			}
		}

		System.out.println("Data cound not be found in: " + file.getName());
		return 0;

	}
	
	public int findDataCol(String value){

		System.out.println("Searching for data in: "+ file.getName());

		for (int row =1;row<=getNumberOfRows();row++) {
			for (int col =1;col<=getNumberOfColumns();col++) {


				if (value.equals(readData(row,col))){

					System.out.println("Found on col: " + col + " in file: " + file.getName() );
					return col;

				}

			}
		}

		System.out.println("Data cound not be found in: " + file.getName());
		return 0;

	}


	public void closeFile() throws IOException {

		fos.close();
	}	

}
