import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	static int Numberofinst;
	static int[] MainMem = new int[2048];
	 static int fcount;
	 static int PC=0;
	 static int instruction;
	 static boolean fetching;
	 static int OPCODE;
	 static int r1;
	 static int r2;
	 static int r3;
	 static int Shift;
	 static int immediate;
	 static int address;
	 static int reg1;
	 static int reg2;
	 static int reg3;
	static int registersol;
	 static int dcount;
	static int[] RegisterFile = new int[32]; 
	static String BinaryInst = ""; 
	 static int temp;
	 static int PC2;
	 static String type;
	 static boolean pcrun;
	 static int finish;
	 static boolean decoding;
	 static boolean executing;
	 static boolean jump;
	 static int waitmem;
	 static int waitwrite;
	 static int execCount;
	//final int R0 = 0;
 	static int R1;
 	static int R2;
 	static int R3;
 	static int SHAMT;
 	static int ADDR;
 	static int IMM = 0;
	 static int solpipeline;
	 static int pcpipeline;
	 static int immediatepip;
	 static int reg2pip;
	 static int reg3pip;
	 static int opcodepip;
	 static int reg1pip;
	 static int r1Pipline;
	 static int opcodePipline;
	 static int shamtPipeline;
	 static int addressPip;
	 static int opcodePipline2;
	 static int r1Pipline2;
	 static boolean write;
	 static int waitDec;
	 static int waitexec;
	 static int memcount;
	private static int waitfetch;
	private static int pcpip;
	private static int pcpip2;
	private static int tmp;
	private static int jumper;
	private static int oppip;
	private static int pipreg1;
	private static int pipreg2;
	private static int pipreg3;
	private static int immpip;
	private static int addpip;
	private static int shpip;
    


	public static void ReadProgram(String Name) throws IOException {
		String path = "src/resources/" + Name + ".txt";
		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String[] s;
		Numberofinst = (int) br.lines().count();
		br.close();
		br = new BufferedReader(new FileReader(file));
		int m = 0;
		String str;
		String type = null;
		String BinaryInst = "";
		while ((str = br.readLine()) != null) {
			s = str.split(" ");
			if (s[0].equals("ADD")) {
				BinaryInst = "0000";
				type = "R";
			}
			if (s[0].equals("SUB")) {
				BinaryInst = "0001";
				type = "R";
			}
			if (s[0].equals("MULI")) {
				BinaryInst = "0010";
				type = "I";
			}
			if (s[0].equals("ADDI")) {
				BinaryInst = "0011";
				type = "I";
			}
			if (s[0].equals("BNE")) {
				BinaryInst = "0100";
				type = "I";
			}
			if (s[0].equals("ANDI")) {
				BinaryInst = "0101";
				type = "I";
			}
			if (s[0].equals("ORI")) {
				BinaryInst = "0110";
				type = "I";
			}
			if (s[0].equals("J")) {
				BinaryInst = "0111";
				type = "J";

			}
			if (s[0].equals("SLL")) {
				BinaryInst = "1000";
				type = "R";
			}
			if (s[0].equals("SRL")) {
				BinaryInst = "1001";
				type = "R";
			}
			if (s[0].equals("LW")) {
				BinaryInst = "1010";
				type = "I";
			}
			if (s[0].equals("SW")) {
				BinaryInst = "1011";
				type = "I";

			}

			if (!(type.equals("J"))) {
				R1 = Integer.parseInt(s[1].replace("R", ""));
				// 5bits fa 3ashan keda ben loop 5 marat
				for (int i = Integer.toBinaryString(R1).length(); i < 5; i++) {
					BinaryInst += "0";
				}
				BinaryInst += Integer.toBinaryString(R1);

				R2 = Integer.parseInt(s[2].replace("R", ""));
				for (int i = Integer.toBinaryString(R2).length(); i < 5; i++) {
					BinaryInst += "0";
				}
				BinaryInst += Integer.toBinaryString(R2);
				if (s[0].equals("ADD") || s[0].equals("SUB")) {
					R3 = Integer.parseInt(s[3].replace("R", ""));
					for (int i = Integer.toBinaryString(R3).length(); i < 5; i++) {
						BinaryInst += "0";
					}
					BinaryInst += Integer.toBinaryString(R3);
					BinaryInst += "0000000000000";
				}

				else if (s[0].equals("SRL") || s[0].equals("SLL")) {
					BinaryInst += "00000";
					SHAMT = Integer.parseInt(s[3]);

					for (int i = Integer.toBinaryString(SHAMT).length(); i < 13; i++) {
						BinaryInst += "0";
					}
					BinaryInst += Integer.toBinaryString(SHAMT);

				} else {
					IMM = Integer.parseInt(s[3]);
					if (IMM >= 0) {
						for (int i = Integer.toBinaryString(IMM).length(); i < 18; i++) {
							BinaryInst += "0";
						}
						BinaryInst += Integer.toBinaryString(IMM);
					} else
						BinaryInst += Integer.toBinaryString(IMM).substring(14);
				}

			} else {
				ADDR = Integer.parseInt(s[1]);
				for (int i = Integer.toBinaryString(ADDR).length(); i < 28; i++) {
					BinaryInst += "0";
				}
				BinaryInst += Integer.toBinaryString(ADDR);

			}


			MainMem[m++] = (int) Long.parseLong(BinaryInst, 2);
			System.out.println(BinaryInst);

		}

		//System.out.println("END PROGRAM");
		System.out.println("The Total number of instructions is : " + Numberofinst);

	}

	public static void pipeline() {
		int equation = 7 + ((Numberofinst - 1) * 2);
		int count=1;
		for (int clk=1; clk <= equation ;clk++) {
			int c=0;
			System.out.println("Cycle:  "+clk);

			if(clk >= 7 && clk % 2 != 0 && (waitwrite <= 0 || waitwrite == 6)) {
				System.out.println("writing:  "+clk);
				writeBack();

					if(jump && jumper--==0) {
						jump =false;
					}
			}
			waitwrite--;

			if(clk >= 6 && clk % 2 == 0 && waitmem <= 0) {
				if(jump) {
					PC =PC2-1;
                    waitDec = 1;
                    waitexec = 3;
                    waitmem = 6;
                    waitwrite = 6;
                    equation =equation+2;
				}
				System.out.println("memo:  "+clk);
				memory();
			}
			waitmem--;
			c++;	
					if(executing) {
				System.out.println("Instruction " + execCount + " is at the execute stage." + "\nINPUT: opcode = "
                        + opcodePipline + " PC = " + pcpip + " shamt= " + shamtPipeline + " immediate = "
                        + immediatepip + " address = " + addressPip + "\nData in r1 = " + reg1pip
                        + " Data in r2 = " + reg2pip + " Data in r3 = " + reg3pip);
				executing=false;
				//System.out.println(PC2);

			}
					
			if(clk >=4 && (clk%2==0) && waitexec <=0 && clk<= equation-2) {
				execute();
				System.out.println("Instruction " + execCount + " is at the execute stage." + "\nINPUT: opcode = "
                        + opcodePipline + " PC = " + pcpip + " shamt= " + shamtPipeline + " immediate = "
                        + immediatepip + " address = " + addressPip + "\nData in r1 = " + reg1pip
                        + " Data in r2 = " + reg2pip + " Data in r3 = " + reg3pip);
				System.out.println(PC2);

			}

			waitexec--;
			
			if(decoding) {
				 System.out.println("Instruction " + dcount + " is at the decode stage.\nINPUT: instruction = "
	                        + Integer.toBinaryString(instruction) + "\nOUTPUT: opcode = " + OPCODE + " r1 = " + r1
	                        + " r2 = " + r2 + " r3 = " + r3 + " shamt= " + SHAMT + " immediate = " + immediate
	                        + " address = " + address + "\nData in r1 = " + reg1 + " Data in r2 = " + reg2
	                        + " Data in r3 = " + reg3 + "\n");
				decoding=false;
			}
			if( clk%2==0 && waitDec <=0  && clk<= equation-4) {
				//decode();
				System.out.println("decoding at cycle:  "+clk);
				decode();
				 System.out.println("Instruction " + dcount + " is at the decode stage.\nINPUT: instruction = "
	                        + Integer.toBinaryString(instruction) + "\nOUTPUT: opcode = " + OPCODE + " r1 = " + r1
	                        + " r2 = " + r2 + " r3 = " + r3 + " shamt= " + SHAMT + " immediate = " + immediate
	                        + " address = " + address + "\nData in r1 = " + reg1 + " Data in r2 = " + reg2
	                        + " Data in r3 = " + reg3 + "\n");
	
				

			}
			waitDec--;
			

			//fetch
			if(clk%2 == 1 && PC <= Numberofinst - 1) {
					Fetch();
					if(jump==false) {
					System.out.println("Fetching at cycle : "+clk);
					System.out.println("Instruction " + fcount + " is at the fetch stage \n");
					waitfetch=7;

				}}
			System.out.println("\n\n");


	}
	}


	
	//mota2akedin menha
	public static void writeBack() {

		if (r1Pipline2 != 0) {
			if (opcodePipline2 != 4 && opcodePipline2 != 7 && opcodePipline2 != 11) {

				RegisterFile[r1Pipline2] = solpipeline;
		        System.out.println("Instruction " + memcount + " is at the write back stage.");
				System.out.println("INPUT: Destination Register = " + r1Pipline2 + " Write Back Value = " + solpipeline);
				System.out.println(
						"Register R" + r1Pipline2 + " was modified to contain " + solpipeline + " at the write back stage.");
			}
			else {
				 System.out.println("Instruction " + memcount + " don't need a the write back stage.");
			}
			
		}
		else {
	        System.out.println("Can't write in register R0");

		}
		System.out.println();
		write = true;
	}
	
	//mota2akedin menha
	public static void memory() {
        memcount = execCount;

		switch (opcodePipline) {
		case 10:
			registersol = MainMem[reg2pip + immediatepip];
			break;
		case 11:
			MainMem[reg2pip + immediatepip] = reg1pip;
			break;
			
			}
		System.out.println("Instruction " + memcount + " is at the memory stage." + "\nINPUT: opcode = "
                + opcodePipline + " immediate = " + immediatepip + "\nData in r1 = " + reg1pip
                + " Data in r2 = " + reg2pip);
			 if (opcodepip == 10) {
		            System.out.println("OUTPUT: Write Back Value = " + reg1);
		        }
			 
		        if (opcodepip == 11) {
		            System.out.println("Memory Position " + (reg2 + immediate) + " was modified to contain "
		                    + reg1 + " at the memory stage");
		        }
		        
		        System.out.println();
		        opcodePipline2 = opcodePipline;
		        r1Pipline2 = r1Pipline;
				solpipeline =registersol;


	}
	
	//mota2akedin menha
	public static void execute() {

		switch (oppip) {
		case 0 :
			registersol= pipreg2+pipreg3;
			break;
		case 1 :
			registersol=pipreg2-pipreg3;
			break;
		case 2 :
			registersol=pipreg2*immpip;
			break;
		case 3 :
			registersol=pipreg2+immpip;
			break;
		case 4 :
			if(pipreg1 != pipreg2) {
				PC2=pcpip+1+immpip;
				jump=true;
				jumper=1;
				}
			
			break;
		case 5 :
			registersol=pipreg2&immpip;
			break;
		case 6 :
			registersol=pipreg2|immpip;
			break;
		case 7 :
			int t= pcpip&0b1111000000000000000000000000;
			PC2= t  | addpip;
			jump=true;
			jumper=1;

			break;
		case 8 :
			registersol=pipreg2<<shpip;
			break;
		case 9 :
			registersol=pipreg2>>>shpip;
			break;

		}
		//System.out.println("sol:" + registersol);
		tmp=registersol;
		pcpipeline=PC2;
		immediatepip = immpip;
		reg1pip=pipreg1;
		reg2pip=pipreg2;
		reg3pip=pipreg3;
        execCount = dcount;
        r1Pipline = r1;
        addressPip = addpip;
        opcodePipline = oppip;
        shamtPipeline = shpip;
        pcpip2=pcpip;
        executing=true;

	}
	//mota2akedin menaha
	public static void decode() {

		OPCODE= instruction >>> 28; // 2 > walla 3
		r1 = (instruction & 0b1111100000000000000000000000 ) >>> 23;
		r2 = (instruction & 0b11111000000000000000000 ) >>> 18;
		r3 = (instruction & 0b111110000000000000) >>> 13;
		Shift = (instruction & 0b1111111111111);
        immediate = ((instruction & 0b00000000000000111111111111111111) << 14) >> 14;
		address = (instruction & 0b1111111111111111111111111111);
		reg1 = RegisterFile[r1];
		reg2 = RegisterFile[r2];
		reg3 = RegisterFile[r3];
		dcount = fcount;
		pcpip=PC;
		decoding=true;
		oppip=OPCODE;
		pipreg1=reg1;
		pipreg2=reg2;
		pipreg3=reg3;
		immpip=immediate;
		addpip=address;
		shpip=Shift;
//		System.out.println("op" + " " + OPCODE);
//		System.out.println("r1" + " " + r1);
//		System.out.println("r2" + " " + r2);
//		System.out.println("r3" + " " + r3);
//		System.out.println("shift" + " " + Shift);
//		System.out.println("immediate" + " " + immediate);
//		System.out.println("address" + " " + address);
//		System.out.println("reg1:" + " " + reg1);
//		System.out.println("reg2:" + " " + reg2);
//		System.out.println("reg3:" + " " + reg3);


	}
	
	//mota2akedin menha
	public static void Fetch() {

		instruction = MainMem[PC];
		fcount = PC + 1;
		fetching=true;
		if(jump==false) {
		System.out.println("PC:" + " " + PC);
		System.out.println("Fetched inst:" + " " + instruction);
}
		PC++;

	}
	

	public static String getBinaryinst(String s) {
		if (s.equals("ADD")) {
			BinaryInst = "0000";
			type = "R";
		}
		if (s.equals("SUB")) {
			BinaryInst = "0001";
			type = "R";
		}
		if (s.equals("MULTI")) {
			BinaryInst = "0010";
			type = "I";
		}
		if (s.equals("ADDI")) {
			BinaryInst = "0011";
			type = "I";
		}
		if (s.equals("BNE")) {
			BinaryInst = "0100";
			type = "I";
		}
		if (s.equals("ANDI")) {
			BinaryInst = "0101";
			type = "I";
		}
		if (s.equals("ORI")) {
			BinaryInst = "0110";
			type = "I";
		}
		if (s.equals("J")) {
			BinaryInst = "0111";
			type = "J";
		}
		if (s.equals("SLL")) {
			BinaryInst = "1000";
			type = "R";
		}
		if (s.equals("SRL")) {
			BinaryInst = "1001";
			type = "R";
		}
		if (s.equals("LW")) {
			BinaryInst = "1010";
			type = "I";
		}
		if (s.equals("SW")) {
			BinaryInst = "1011";
			type = "I";
		}
		return type;
	}
	public static int helpOpcode(int op) {
		switch (Integer.toBinaryString(op)) {
		case "0000":
			temp = 0;
			break;
		case "0001":
			temp = 1;
			break;
		case "0010":
			temp = 2;
			break;
		case "0011":
			temp = 3;
			break;
		case "0100":
			temp = 4;
			break;
		case "0101":
			temp = 5;
			break;
		case "0110":
			temp = 6;
			break;
		case "0111":
			temp = 7;
			break;
		case "1000":
			temp = 8;
			break;
		case "1001":
			temp = 9;
			break;
		case "1010":
			temp = 10;
			break;
		case "1011":
			temp = 11;
			break;
		}
		return temp;
	}
	public static boolean CheckPc() {

		 if(PC < Numberofinst -1)
            pcrun =true;
         if (PC >= Numberofinst - 1) 
             pcrun =false;
		return pcrun;
	}
	
	public static void main(String[] args) throws IOException {
		ReadProgram("Program");
//		Fetch();
//		decode();
//		execute();
//		writeBack();
//		memory();
		System.out.println("------------------------------------");
		
//		System.out.println(MainMem[0]);
//
//		System.out.println("------------------------------------");

		pipeline();
		// System.out.println("Program End");
		System.out.println("------------------------------------");
		//ben print kol haga fel registers bta3tna fe akher clock cycle
		for (int i = 0; i < RegisterFile.length; i++) {
			System.out.println("R" + i + ": " + RegisterFile[i]);
		}
		//ben print kol haga fel memory bta3tna fe akher clock cycle
		for (int i = 0; i < MainMem.length; i++) {
			System.out.println("Memory Position " + i + ": " + MainMem[i]);
		}

	}
				
}