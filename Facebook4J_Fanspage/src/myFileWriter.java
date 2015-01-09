

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import Jama.Matrix;

public class myFileWriter {

	public void matrix_to_csv(Matrix matrix,String path) throws IOException
	{
		File f = new File(path);
		
		if(!f.exists())
		{
			if (path.contains("/"))
			{
				f.getParentFile().mkdir();
				f.createNewFile();
			}
			else
			{
				f.createNewFile();
			}
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		
		for(int i = 0 ; i < matrix.getRowDimension() ; i++)
		{
			String line = "";
			line = matrix.get(i, 0)+"";
			for(int j = 1 ; j < matrix.getColumnDimension() ; j++)
			{
				line += ","+matrix.get(i, j);
			}
			writer.write(line);
			writer.newLine();
		}
		
		writer.close();
	}
	
	public void matrix_to_link_list(Matrix matrix,String path) throws IOException
	{
		File f = new File(path);
		
		if(!f.exists())
		{
			if (path.contains("/"))
			{
				f.getParentFile().mkdir();
				f.createNewFile();
			}
			else
			{
				f.createNewFile();
			}
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		
		for(int i = 0 ; i < matrix.getRowDimension() ; i++)
		{
			String line = "";
			for(int j = i ; j < matrix.getColumnDimension() ; j++)
			{
				if(matrix.get(i, j)!=0)
				{
					line = i + " " + j + " " + matrix.get(i, j);
					writer.write(line);
					writer.newLine();
				}
				
			}
			
		}
		
		writer.close();
	}
	
	public void map_to_csv(Map<Integer,String> map,String path) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		
		for(int i = 0 ; i < map.size() ; i++)
		{
			String line = "";
			line = i + "," + map.get(i);
			writer.write(line);
			writer.newLine();
		}
		
		writer.close();
	}
	
	public void map_matrix_to_csv(Map<Integer,String> map,Matrix matrix,String path) throws IOException
	{
		File f = new File(path);
		
		if(!f.exists())
		{
			if (path.contains("/"))
			{
				f.getParentFile().mkdir();
				f.createNewFile();
			}
			else
			{
				f.createNewFile();
			}
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		
		for(int i = 0 ; i < matrix.getRowDimension() ; i++)
		{
			String line = map.get(i);
			for(int j = 0 ; j < matrix.getColumnDimension() ; j++)
			{
				line += ","+matrix.get(i, j);
			}
			writer.write(line);
			writer.newLine();
		}
		
		writer.close();
	}
}
