package yuanxz.uestc.datarecord;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import yuanxz.uestc.distancecalculatorschema.CalculateSchema;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class ExcelOperation {
	public static final int DEFAULT_COLUMN=1;
	public static final int DEFAULT_SHEET=0;
	public static final int COLUMN=20;
	
	private WritableWorkbook writableWorkbook;
	private WritableSheet writableSheet;
	private String targetFileName;
	
	private int[] rows=new int[COLUMN];//记录一列有多少行
	
	public void open(String fileName) {
		// TODO Auto-generated method stub
		targetFileName=fileName;
		SimpleDateFormat sdf=new SimpleDateFormat("MM-dd HH-mm-ss");
		targetFileName+="("+sdf.format(Calendar.getInstance().getTime())+")"+".xls";
		System.out.println("文件名："+targetFileName);
		try{
			if(fileIsExist(targetFileName)){
				File file=new File(targetFileName);
				if(file.delete()){
					System.out.println("删除之前存在的文件成功！");
				}else{
					System.out.println("删除之前存在的文件失败！");
				}
			}
			File targetFile=new File(targetFileName);
			writableWorkbook=Workbook.createWorkbook(targetFile);
			writableSheet=getDefaultSheet();
			return;
		}catch(IOException e){
			e.printStackTrace();
		} 
		System.out.println("打开工作薄失败！");
	}

	/**
	 * 打开存在的文件
	 * @param fileName
	 */
	public void appendExist(String fileName){
		System.out.println(fileName);
		if(!fileIsExist(fileName)){
			System.err.println("错误！文件不存在！");
		}else{
			try {
				Workbook workbook = Workbook.getWorkbook(new File(fileName));
				writableWorkbook=Workbook.createWorkbook(new File(fileName), workbook);
				writableSheet=getDefaultSheet();
				return ;
			} catch (BiffException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("打开已存在的文件失败！");
		}
	}
	
	public boolean fileIsExist(String fileName) {
		// TODO Auto-generated method stub
		File file=new File(fileName);
		if(file.exists()){
			return true;
		}else{
			return false;
		}
	}

	public String[] read() {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * 在最后一列添加内容
	 */
	public void write(String value) {
		// TODO Auto-generated method stub
		
	}
    
	
	/**
	 * 获取默认工作sheet
	 * @return
	 */
	private WritableSheet getDefaultSheet(){
		WritableSheet sheet;
		try{
			sheet=writableWorkbook.getSheet(DEFAULT_SHEET);
		}catch(IndexOutOfBoundsException e){
			System.out.println("工作薄当中没有内容，自动创建一个sheet.");
			sheet=writableWorkbook.createSheet("work_sheet_0", DEFAULT_SHEET);
		}
		return sheet;
	}
	
	
	public void write(String value,int row,int column){
		
	}
	
	
	public void write(float value) {
		// TODO Auto-generated method stub
		int row=rows[DEFAULT_COLUMN];
		int column=DEFAULT_COLUMN;
		write(value, row, column);
	}
	
	/**
	 * @param columnNum 列编号
	 * @param value
	 */
	public void write(int columnNum,float value){
		int row=rows[columnNum];
		write(value, row, columnNum);
	}
	/**
	 * @param columnNum 列编号
	 * @param value
	 */
	public void write(int columnNum,double value){
		int row=rows[columnNum];
		write(value, row, columnNum);
	}
	/**
	 * @param value
	 * @param row
	 * @param column
	 */
	public void write(float value,int row,int column){
		Number content=new Number(column, row, value);
		try {
			writableSheet.addCell(content);
			rows[column]+=1;//行数加1
			return ;
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("写入失败！");
	}
	/**
	 * @param value
	 * @param row
	 * @param column
	 */
	public void write(double value,int row,int column){
		Number content=new Number(column, row, value);
		try {
			writableSheet.addCell(content);
			rows[column]+=1;//行数加1
			return ;
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("写入失败！");
	}
	
	public void update(String value,int row,int column){
		write(value, row, column);
	}
	
	public void write(String[] values) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 执行一次写内容到excel中
	 */
	public void writeToExcel(){
		close();
		appendExist(targetFileName);
	}
	
	/**
	 * 获取指定列目前有多少行
	 * @param column
	 * @return
	 */
	public int getColumnCount(int column){
		return rows[column];
	}
	
	
	public void close() {
		// TODO Auto-generated method stub
		try {
			writableWorkbook.write();//将写推迟到最后，否则只能写一次
			writableWorkbook.close();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	
}
