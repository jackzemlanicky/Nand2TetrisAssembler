import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Assembler {
	//type file path here
	final String FILEPATH="src/Rect.asm";
	public Assembler(){
		ArrayList<String> lines=new ArrayList();
		int originalSize;
		File asmFile=new File(FILEPATH);
		try {
			//Reads file and stores in an Array List, 1 element per line of text
			Scanner scanner=new Scanner(asmFile);
			while (scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
		} 
		catch (FileNotFoundException e) {
			System.out.println("File not found bro, check your file's path");
			e.printStackTrace();
		}
		//For loop to get rid of comments
		for(int i=0;i<lines.size()-1;i++) {
			String temp=lines.get(i);
			String[]tempArray=temp.split("/");
			lines.set(i,tempArray[0]);
		}
		//called here before we start deleting elements
		originalSize=lines.size();
		//gets rid of whitespace, leaving empty cells in Array List
		//also removes elements that are empty
		for(int i=originalSize-1;i>=0;i--) {
			String temp=lines.get(i);
			temp=temp.replaceAll("\\s","");
			lines.set(i,temp);
			if(lines.get(i).isEmpty())
				lines.remove(i);
		}
		System.out.println(lines);
		System.out.println(lines.size());
		System.out.println(originalSize);
	}

	public static void main(String[] args) {
		new Assembler();
	}

}
