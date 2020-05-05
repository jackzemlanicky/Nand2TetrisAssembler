import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Assembler {
	//type file path here
	final String FILEPATH="src/Rect.asm";
	public Assembler(){
		ArrayList<String> lines=new ArrayList();
		int currentSize;
		int newRegisterValue=16;
		String dest="";
		String jump="";
		String comp="";
		File asmFile=new File(FILEPATH);
		//Hard-coding registers into symbol table
		HashMap<String,Integer>symbols=new HashMap();
		symbols.put("R0", 0);
		symbols.put("R1", 1);
		symbols.put("R2", 2);
		symbols.put("R3", 3);
		symbols.put("R4", 4);
		symbols.put("R5", 5);
		symbols.put("R6", 6);
		symbols.put("R7", 7);
		symbols.put("R8", 8);
		symbols.put("R9", 9);
		symbols.put("R10", 10);
		symbols.put("R11", 11);
		symbols.put("R12", 12);
		symbols.put("R13", 13);
		symbols.put("R14", 14);
		symbols.put("R15", 15);
		symbols.put("SCREEN", 16384);
		symbols.put("KBD", 24576);
		symbols.put("SP", 0);
		symbols.put("LCL", 1);
		symbols.put("ARG", 2);
		symbols.put("THIS", 3);
		symbols.put("THAT", 4);
		//Hard-coding hash map for all dest and jump bits
		HashMap<String,String>dj=new HashMap();
		//All dest keys and values (null for both)
		dj.put("null","000");
		dj.put("M", "001");
		dj.put("D", "010");
		dj.put("MD", "011");
		dj.put("A", "100");
		dj.put("AM", "101");
		dj.put("AD", "110");
		dj.put("AMD", "111");
		//All jump keys and values
		dj.put("JGT","001");
		dj.put("JEQ","010");
		dj.put("JGE","011");
		dj.put("JLT","100");
		dj.put("JNE","101");
		dj.put("JLE","110");
		dj.put("JMP","111");
		

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
			String[]tempArray=temp.split("//");
			lines.set(i,tempArray[0]);
		}
		//called here before we start deleting elements
		currentSize=lines.size();
		//gets rid of whitespace, leaving empty cells in Array List
		//also removes elements that are empty
		for(int i=currentSize-1;i>=0;i--) {
			String temp=lines.get(i);
			temp=temp.replaceAll("\\s","");
			lines.set(i,temp);
			if(lines.get(i).isEmpty())
				lines.remove(i);
		}
		//updating current size because we delete lines above
		currentSize=lines.size();
		//this loop removes each label from the file and places it in the symbol table
		for(int i=0;i<currentSize;i++) {
			String temp=lines.get(i);
			if(temp.startsWith("(")) {
				//gets rid off parentheses in symbol table
				symbols.put(temp.replace("(", "").replace(")",""), lines.indexOf(temp));
				lines.remove(i);
				//Since we are removing lines within the loop, voila!
				currentSize--;
			}
		}
		//updating size again
		currentSize=lines.size();
		//loop that replaces all @s with either things from symbol table or makes them new variables
		for(int i=0;i<currentSize;i++) {
			String temp=lines.get(i);
			//for the else if, b/c temp gets monkeyed around with
			String elseIf=lines.get(i);
			if(temp.startsWith("@")) {
				if(symbols.containsKey(temp=temp.replace("@", ""))) {
					//appends @ to beginning of each item, as values in symbols are integers
					String newLine="@"+symbols.get(temp).toString();
					lines.set(i,newLine);
				}
				//for all remaining variables
				else if (!Character.isDigit(elseIf.charAt(1))){
					symbols.put(temp, newRegisterValue);//(for variables, starting at register 16)
					String newLine="@"+symbols.get(temp).toString();
					lines.set(i,newLine);
					newRegisterValue++;
				}
			}
		}
		//Let's get started with parsing this text
		for(int i=0;i<currentSize;i++) {
			String temp=lines.get(i);
			//checks for equal sign, which tells us if there is a destination
			if(temp.indexOf("=")==-1) {
				dest="null";
			}
			//if there IS an equal sign, there is a dest, and we will grab it
			else {
				String[]tempArray = temp.split("=");
				//takes the string before the equal sign, which should be one or two characters (its destination)
				dest=tempArray[0];
				//temp becomes the other part of the line (everything minus the "DEST=")
				temp=tempArray[1];
			}
			//checks for semicolon, which tells us if there is a jump
			if(temp.indexOf(";")==-1) {
				jump="null";
				//if NO jump, then all that is left is the comp (dest taken out in else statement above)
				comp=temp;
				System.out.println(comp);
				
			}
			//if there IS a semicolon, then there is a jump, and we will grab it
			else {
				String[]tempArray=temp.split(";");
				//takes string after semicolon, which should be three characters (JMP,JGT,JNE,etc)
				jump=tempArray[1];
				//in this case, comp is the part of the string before the semicolon
				comp=tempArray[0];
			}
		}
		//TODO: Step 1) Make HashMap with all labels in program (things that start with a  "(" ) and each register R0-R15
		//      		-Do this by running thru lines and grabbing each line that fits this description
		//				-Delete each parenthesis line, not needed
		//				-Add labels into hash map in one pass, then second pass to replace them with values and add any other variables that did not have labels
		//		DONE
		// 		Step 2) Create our CodeParser that takes a string (line of code) and gets each field of the current command/code
		System.out.println(lines);
		System.out.println(currentSize);
		System.out.println(lines.size());
		System.out.println(symbols);
	}

	public static void main(String[] args) {
		new Assembler();
	}

}