import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Assembler {
	//type file path here
	final String FILEPATH="src/Pong.asm";
	//type output file here
	final String OUTPUTFILE="src/Pong.hack";
	public Assembler(){
		ArrayList<String> lines=new ArrayList();
		ArrayList<String> lines2=new ArrayList();
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
		//Hard-coding hash map for all dest
		HashMap<String,String>destMap=new HashMap();
		//All dest keys and values (null for both)
		destMap.put("null","000");
		destMap.put("M", "001");
		destMap.put("D", "010");
		destMap.put("MD", "011");
		destMap.put("A", "100");
		destMap.put("AM", "101");
		destMap.put("AD", "110");
		destMap.put("AMD", "111");
		//All jump keys and values
		HashMap<String,String>jumpMap=new HashMap();
		jumpMap.put("null","000");
		jumpMap.put("JGT","001");
		jumpMap.put("JEQ","010");
		jumpMap.put("JGE","011");
		jumpMap.put("JLT","100");
		jumpMap.put("JNE","101");
		jumpMap.put("JLE","110");
		jumpMap.put("JMP","111");
		//All comp keys and values
		//7 numbers b/c we are including the a value (0 for A and 1 for M)
		HashMap<String,String>compMap=new HashMap();
		compMap.put("0", "0101010");
		compMap.put("1", "0111111");
		compMap.put("-1","0111010");
		compMap.put("D", "0001100");
		compMap.put("A", "0110000");
		compMap.put("M", "1110000");
		compMap.put("!D", "0001101");
		compMap.put("!A", "0110001");
		compMap.put("!M", "1110001");
		compMap.put("-D", "0001111");
		compMap.put("-A", "0110011");
		compMap.put("-M", "1110011");
		compMap.put("D+1", "0011111");
		compMap.put("A+1", "0110111");
		compMap.put("M+1", "1110111");
		compMap.put("D-1", "0001110");
		compMap.put("A-1", "0110010");
		compMap.put("M-1", "1110010");
		compMap.put("D+A", "0000010");
		compMap.put("D+M", "1000010");
		compMap.put("D-A", "0010011");
		compMap.put("D-M", "1010011");
		compMap.put("A-D", "0000111");
		compMap.put("M-D", "1000111");
		compMap.put("D&A", "0000000");
		compMap.put("D&M", "1000000");
		compMap.put("D|A", "0010101");
		compMap.put("D|M", "1010101");
		System.out.print("Reading file into program...");
		try {
			//Reads file and stores in an Array List, 1 element per line of text
			Scanner scanner=new Scanner(asmFile);
			while (scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
			scanner.close();
		} 
		catch (FileNotFoundException e) {
			System.out.println("File not found bro, check your file's path");
			e.printStackTrace();
		}
		System.out.println("Done");
		//For loop to get rid of comments
		System.out.print("Cleaning up comments and whitespace...");
		for(int i=0;i<lines.size();i++) {
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
		System.out.println("Done");
		//updating current size because we delete lines above
		currentSize=lines.size();
		//this loop removes each label from the file and places it in the symbol table
		System.out.print("Removing labels and regex variables...");
		for(int i=0;i<currentSize;i++) {
			String temp=lines.get(i);
			if(temp.startsWith("(")) {
				//gets rid off parentheses in symbol table
				symbols.put(temp.replace("(", "").replace(")",""), lines.indexOf(temp));
			}
			//if it is not a label we add it to our second Arraylist
			else {
				lines2.add(temp);
			}
		}
		//updating size again
		currentSize=lines2.size();
		//loop that replaces all @s with either things from symbol table or makes them new variables
		for(int i=0;i<currentSize;i++) {
			String temp=lines2.get(i);
			//for the else if, b/c temp gets monkeyed around with
			String elseIf=lines2.get(i);
			if(temp.startsWith("@")) {
				if(symbols.containsKey(temp=temp.replace("@", ""))) {
					//appends @ to beginning of each item, as values in symbols are integers
					String newLine="@"+symbols.get(temp).toString();
					lines2.set(i,newLine);
				}
				//for all remaining variables
				else if (!Character.isDigit(elseIf.charAt(1))){
					symbols.put(temp, newRegisterValue);//(for variables, starting at register 16)
					String newLine="@"+symbols.get(temp).toString();
					lines2.set(i,newLine);
					newRegisterValue++;
				}
			}
		}
		System.out.println("Done");
		//Let's get started with parsing this text
		System.out.print("Parsing remaining text into binary...");
		for(int i=0;i<currentSize;i++) {
			String temp=lines2.get(i);
			//For all C instructions
			if(!temp.startsWith("@")) {
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
				}
				//if there IS a semicolon, then there is a jump, and we will grab it
				else {
					String[]tempArray=temp.split(";");
					//takes string after semicolon, which should be three characters (JMP,JGT,JNE,etc)
					jump=tempArray[1];
					//in this case, comp is the part of the string before the semicolon
					comp=tempArray[0];
				}
				dest=destMap.get(dest);
				comp=compMap.get(comp);
				jump=jumpMap.get(jump);
				//(Hopefully) a cool 16-bit C instruction
				lines2.set(i,"111"+comp+dest+jump);
			}
			//else the line is an A instruction
			else {
				//getting rid of the @
				String[] tempArray=temp.split("@");
				temp=tempArray[1];
				Integer toInt=Integer.parseInt(temp);
				temp=Integer.toBinaryString(toInt);
				//now how do i make it 15 bits long
				temp=String.format("%16s", temp).replace(' ','0');
				lines2.set(i, temp);
			}
		}
		System.out.println("Done, outputting to text file");
		System.out.println(lines2);
		System.out.println(symbols);
		//creating the file output
		try {
			FileWriter writer=new FileWriter(OUTPUTFILE);
			for(String str: lines2) {
				writer.write(str+System.lineSeparator());
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new Assembler();
	}

}