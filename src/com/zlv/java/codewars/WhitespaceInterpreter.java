package com.zlv.java.codewars;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.*;
import java.util.stream.*;

public class WhitespaceInterpreter {
	
	/*
	 * true - print logs to console
	 * false - don't print logs
	 */
	private static final boolean DEBUG = false;

	/*
	 * IMPs: 
	 * [space]: 			Stack Manipulation 
	 * [tab][space]: 		Arithmetic 
	 * [tab][tab]: 			Heap Access 
	 * [tab][line-feed]: 	Input/Output 
	 * [line-feed]: 		Flow Control
	 */
	private static final String IMP_STACK = "s";
	private static final String IMP_Arithmetic = "ts";
	private static final String IMP_HEAP = "tt";
	private static final String IMP_IO = "tn";
	private static final String IMP_FLOW_CONTROL = "n";

	/*
	 * Numbers and Labels:
	 */
	private static final String REG_NUM = "[ts]+n";
	private static final String REG_ONLY_NUM = "^"+REG_NUM+"$";
	private static final String REG_LABLE = "[ts]*n";
	private static final String REG_ONLY_LABLE = "^"+REG_LABLE+"$";

	/*
	 * Stack Manipulation:
	 */
	private static final String REG_STACK_PUSH_NUM = "^"+IMP_STACK+"s"+"("+REG_NUM+")$";
	private static final String REG_STACK_DUPLICATE_NTH_NUM = "^"+IMP_STACK+"ts"+"("+REG_NUM+")$";
	private static final String REG_STACK_DISCARD_TOP_N_NUM = "^"+IMP_STACK+"tn"+"("+REG_NUM+")$";
	private static final String REG_STACK_DUPLICATE_TOP_NUM = "^"+IMP_STACK+"ns$";
	private static final String REG_STACK_SWAP_TOP_TWO_NUM = "^"+IMP_STACK+"nt$";
	private static final String REG_STACK_DISCARD_TOP_NUM = "^"+IMP_STACK+"nn$";
	private static final String[] STACK_REG_ARR = new String[] {REG_STACK_PUSH_NUM, 
			REG_STACK_DUPLICATE_NTH_NUM, 
			REG_STACK_DISCARD_TOP_N_NUM, 
			REG_STACK_DUPLICATE_TOP_NUM, 
			REG_STACK_SWAP_TOP_TWO_NUM,
			REG_STACK_DISCARD_TOP_NUM
	};

	/*
	 * Arithmetic:
	 */
	private static final String REG_ARITHMETIC_POP_2NUM_PUSH_SUM = "^"+IMP_Arithmetic+"ss$";
	private static final String REG_ARITHMETIC_POP_2NUM_PUSH_ABS = "^"+IMP_Arithmetic+"st$";
	private static final String REG_ARITHMETIC_POP_2NUM_PUSH_MUL = "^"+IMP_Arithmetic+"sn$";
	private static final String REG_ARITHMETIC_POP_2NUM_PUSH_FLOOR_DIV = "^"+IMP_Arithmetic+"ts$";
	private static final String REG_ARITHMETIC_POP_2NUM_PUSH_MOD = "^"+IMP_Arithmetic+"tt$";
	private static final String[] ARITHMETIC_REG_ARR = new String[] { REG_ARITHMETIC_POP_2NUM_PUSH_SUM,
			REG_ARITHMETIC_POP_2NUM_PUSH_ABS,
			REG_ARITHMETIC_POP_2NUM_PUSH_MUL,
			REG_ARITHMETIC_POP_2NUM_PUSH_FLOOR_DIV,
			REG_ARITHMETIC_POP_2NUM_PUSH_MOD
	};

	/*
	 * Heap Access:
	 */
	private static final String REG_HEAP_POP_2NUM_STORE_FIRST_AT_ADDR_SECOND = "^"+IMP_HEAP+"s$";
	private static final String REG_HEAP_POP_2NUM_STORE_FIRST_AT_ADDR_FIRST = "^"+IMP_HEAP+"t$";
	private static final String[] HEAP_REG_ARR = new String[] { REG_HEAP_POP_2NUM_STORE_FIRST_AT_ADDR_SECOND,
			REG_HEAP_POP_2NUM_STORE_FIRST_AT_ADDR_FIRST
	};

	/*
	 * Input/Output:
	 */
	private static final String REG_IO_POP_CHAR_2_OUTPUT = "^"+IMP_IO+"ss$";
	private static final String REG_IO_POP_NUM_2_OUTPUT = "^"+IMP_IO+"st$";
	private static final String REG_IO_GET_CHAR_FROM_INPUT_STORE_ASCII = "^"+IMP_IO+"ts$";
	private static final String REG_IO_GET_NUM_FROM_INPUT_STORE_ASCII = "^"+IMP_IO+"tt$";
	private static final String[] IO_REG_ARR = new String[] { REG_IO_POP_CHAR_2_OUTPUT,
			REG_IO_POP_NUM_2_OUTPUT,
			REG_IO_GET_CHAR_FROM_INPUT_STORE_ASCII,
			REG_IO_GET_NUM_FROM_INPUT_STORE_ASCII
	};

	/*
	 * Flow Control:
	 */
	private static final String REG_FLOW_CONTROL_MARK_FUNC = "^"+IMP_FLOW_CONTROL+"ss("+REG_LABLE+")$";
	private static final String REG_FLOW_CONTROL_CALL_FUNC = "^"+IMP_FLOW_CONTROL+"st("+REG_LABLE+")$";
	private static final String REG_FLOW_CONTROL_GO = "^"+IMP_FLOW_CONTROL+"sn("+REG_LABLE+")$";
	private static final String REG_FLOW_CONTROL_GO_ZERO = "^"+IMP_FLOW_CONTROL+"ts("+REG_LABLE+")$";
	private static final String REG_FLOW_CONTROL_GO_LESS_ZERO = "^"+IMP_FLOW_CONTROL+"tt("+REG_LABLE+")$";
	private static final String REG_FLOW_CONTROL_EXIT_FUNC = "^"+IMP_FLOW_CONTROL+"tn$";
	private static final String REG_FLOW_CONTROL_EXIT_ALL = "^"+IMP_FLOW_CONTROL+"nn$";
	private static final String[] FLOW_REG_ARR = new String[] { REG_FLOW_CONTROL_MARK_FUNC,
			REG_FLOW_CONTROL_CALL_FUNC,
			REG_FLOW_CONTROL_GO,
			REG_FLOW_CONTROL_GO_ZERO,
			REG_FLOW_CONTROL_GO_LESS_ZERO,
			REG_FLOW_CONTROL_EXIT_FUNC,
			REG_FLOW_CONTROL_EXIT_ALL,
	};
	
	/*
	 * Array which contains regular expressions:
	 */
	private static final String[][] REG_ARR = new String[][]{STACK_REG_ARR,
			ARITHMETIC_REG_ARR,
			HEAP_REG_ARR,
			IO_REG_ARR,
			FLOW_REG_ARR
	};
	
	/**
	 * Return regular expressions according to input String[][].
	 * @param arrs String[][]
	 * @return regular expressions - match whole code.
	 */
	private static String getWholeReg(String[][] arrs) {
		StringBuffer sf = new StringBuffer("");
		
		for(String[] arr : arrs) {
			for(String s : arr) {
				sf.append(String.format("(%s)|",s));
			}
		}
		
		return sf.substring(0,sf.length()-1).toString().replaceAll("\\^", "").replaceAll("\\$", "");
	}

	/**
	 * Return regular expressions according to input String[].
	 * @param arrs String[]
	 * @return regular expressions - match whole code.
	 */
	private static String getWholeReg(String[] arr) {
		StringBuffer sf = new StringBuffer("");

		for (String s : arr) {
			sf.append(String.format("(%s)|", s));
		}

		return sf.substring(0, sf.length() - 1).toString().replaceAll("\\^", "").replaceAll("\\$", "");
	}
	
	
	/**
	 * <p>Just verify if the input code is a valid code. and return it.</p>
	 * 
	 * <p>
	 * The following are the requirements for numbers in Whitespace system:
	 * <li>Numbers begin with a [sign] symbol. The sign symbol is either [tab] -> negative, or [space] -> positive.
	 * <li>Numbers end with a [terminal] symbol: [line-feed].
	 * <li>Between the sign symbol and the terminal symbol are binary digits [space] -> binary-0, or [tab] -> binary-1.
	 * <li>A number expression [sign][terminal] will be treated as zero.
	 * <li>The expression of just [terminal] should throw an error. (The Haskell implementation is inconsistent about this.)
	 * </p>
	 * @param code <b>Only the valid number format is allowed</b>
	 * @return will return an number in radix of 10.
	 * @throws RuntimeException when the input code is not a valid number format.
	 */
	private static int parsingNum(String code) {
		if (!code.matches(REG_ONLY_NUM))
			throw new RuntimeException("Parsing Number Error: the input code is " + code);

		int sign = ((code.charAt(0) == 't') ? -1 : 1);
		String num = code.substring(1, code.length() - 1)
				.replaceAll("t", "1")
				.replaceAll("s", "0");

		if (num.length() == 0)
			return 0;
		else
			return sign * Integer.parseInt(num, 2);
	}
	
	/**
	 * <p>Just verify if the input code is a valid label. and return it.</p>
	 * 
	 * <p>
	 * The following are the requirements for labels in Whitespace system:
	 * <li>Labels begin with any number of [tab] and [space] characters.
	 * <li>Labels end with a terminal symbol: [line-feed].
	 * <li>Unlike with numbers, the expression of just [terminal] is valid.
	 * <li>Labels must be unique.
	 * <li>A label may be declared either before or after a command that refers to it.
	 * </p>
	 * @param code <b>Only the valid Label format is allowed</b>
	 * @return will return an empty string if code's length is less than 2. for example "sl" or "l".
	 * @throws RuntimeException when the input code is not a valid label format.
	 */
	private static String parsingLabel(String code) {
		if (!code.matches(REG_ONLY_LABLE))
			throw new RuntimeException("Parsing Lable Error: the input code is " + code);
		if (code.length() <= 2)
			return "";
		else
			return code.substring(1, code.length() - 1);
	}
	
	/**
	 * <p>Read a character from input stream and return it.</p>
	 * 
	 * <p>
	 * The following are the requirements for Input in Whitespace system:
	 * <li>Reading a character involves simply taking a character from the input stream.
	 * <li>The Java implementations will use an InputStream instance for input. For InputStream use readLine if the program requests a number and read if the program expects a character.
	 * <li>An error should be thrown if the input ends before parsing is complete. 
	 * </p>
	 * @param is <b>the input stream</b>
	 * @return return a char.
	 * @throws IOException if an I/O error occurs when using is.read().
	 */
	private static char paringInputChars(InputStream is) throws IOException {
		return (char) is.read();
	}
	
	/**
	 * <p>Read a number from input stream and return it.</p>
	 * 
	 * <p>
	 * The following are the requirements for Input in Whitespace system:
	 * <li>Reading an integer involves parsing a decimal or hexadecimal number from the current position of the input stream, up to and terminated by a line-feed character.
	 * <li>The Java implementations will use an InputStream instance for input. For InputStream use readLine if the program requests a number and read if the program expects a character.
	 * <li>An error should be thrown if the input ends before parsing is complete. 
	 * </p>
	 * @param is <b>the input stream</b>
	 * @return return a integer.
	 * @throws IOException if an I/O error occurs when using is.read().
	 * @throws NumberFormatException if the input string doesn't contain a parsable integer.
	 */
	private static int paringInputNum(InputStream is) throws IOException {
		return Integer.parseInt(new BufferedReader(new InputStreamReader(is)).readLine());
	}
	
	/**
	 * <p>Write a number to output stream.</p>
	 * 
	 * <p>
	 * The following are the requirements for Output in Whitespace system:
	 * <li>For a number, append the output string with the number's string value.
	 * <li>The Java implementations will support an optional OutputStream for output. If an OutputStream is provided, it should be flushed before and after code execution and filled as code is executed. The output string should be returned in any case.
	 * </p>
	 * @param is <b>the output stream</b>
	 * @return null
	 */
	private static void output(OutputStream os, int num) {
		PrintWriter pw = new PrintWriter(os);
		pw.flush();
		pw.append(""+num).flush();
	}
	
	/**
	 * <p>Write a character to output stream.</p>
	 * 
	 * <p>
	 * The following are the requirements for Output in Whitespace system:
	 * <li>For a character, simply append the output string with the character.
	 * <li>The Java implementations will support an optional OutputStream for output. If an OutputStream is provided, it should be flushed before and after code execution and filled as code is executed. The output string should be returned in any case.
	 * </p>
	 * @param is <b>the output stream</b>
	 * @return null
	 */
	private static void output(OutputStream os, char c) {
		PrintWriter pw = new PrintWriter(os);
		pw.flush();
		pw.append(""+c).flush();
	}

	/**
	 * <p>Deal with stack related code.</p>
	 * 
	 * <p>
	 * The following are the requirements for stack manipulation in Whitespace system:
	 * <li>[space] (number): Push n onto the stack.
	 * <li>[tab][space] (number): Duplicate the nth value from the top of the stack.
	 * <li>[tab][line-feed] (number): Discard the top n values below the top of the stack from the stack. (For n<0 or n>=stack.length, remove everything but the top value.)
	 * <li>[line-feed][space]: Duplicate the top value on the stack.
	 * <li>[line-feed][tab]: Swap the top two value on the stack.
	 * <li>[line-feed][line-feed]: Discard the top value on the stack.
	 * </p>
	 * @param code the stack code.
	 * @param stack the operation stack.
	 * @return null
	 * @throws RuntimeException unknown commands
	 * @throws ArrayIndexOutOfBoundsException should be thrown if there are not enough items on the stack to complete an operation.
	 */
	private static void stack(String code, Stack<Integer> stack) {
		Matcher m = null;
		
		do {
			m = Pattern.compile(REG_STACK_PUSH_NUM).matcher(code);
			if(m.find()){
				stack.push(parsingNum(m.group(1)));
				break;
			}
			
			m = Pattern.compile(REG_STACK_DUPLICATE_NTH_NUM).matcher(code);
			if (m.find()) {
				stack.push(stack.get(stack.size() - 1 - parsingNum(m.group(1))));
				break;
			}
			
			m = Pattern.compile(REG_STACK_DISCARD_TOP_N_NUM).matcher(code);
			if (m.find()) {
				int index = parsingNum(m.group(1));
				if (index < 0 || index >= stack.size()){
					stack.clear();
				} else {
					int top = stack.pop();
					while(index-->0) stack.pop();
					stack.push(top);
				}
				break;
			}
			
			m = Pattern.compile(REG_STACK_DUPLICATE_TOP_NUM).matcher(code);
			if (m.find()) {
				stack.push(stack.get(stack.size()-1));
				break;
			}
			
			m = Pattern.compile(REG_STACK_SWAP_TOP_TWO_NUM).matcher(code);
			if (m.find()) {
				int t = stack.get(stack.size()-1);
				stack.set(stack.size()-1, stack.get(stack.size()-2));
				stack.set(stack.size()-2, t);
				break;
			}
			
			m = Pattern.compile(REG_STACK_DISCARD_TOP_NUM).matcher(code);
			if (m.find()) {
				stack.pop();
				break;
			}
			
			throw new RuntimeException("STACK: unclean termination");
		} while(false);
	}
	
	/**
	 * <p>Deal with arithmetic related code.</p>
	 * 
	 * <p>
	 * The following are the requirements for arithmetic in Whitespace system:
	 * <li>[space][space]: Pop a and b, then push b+a.
	 * <li>[space][tab]: Pop a and b, then push b-a.
	 * <li>[space][line-feed]: Pop a and b, then push b*a.
	 * <li>[tab][space]: Pop a and b, then push b/a*. If a is zero, throw an error. <b>*Note that the result is defined as the floor of the quotient.</b>
	 * <li>[tab][tab]: Pop a and b, then push b%a*. If a is zero, throw an error. <b>*Note that the result is defined as the remainder after division and sign (+/-) of the divisor (a).</b>
	 * </p>
	 * @param code the arithmetic code.
	 * @param stack the operation stack.
	 * @return null
	 * @throws RuntimeException unknown commands
	 * @throws ArrayIndexOutOfBoundsException should be thrown if there are not enough items on the stack to complete an operation.
	 * @throws EmptyStackException should be thrown if there are not enough items on the stack to complete an operation.
	 * @throws ArithmeticException if the divisor is zero. 
	 */
	private static void arithmetic(String code, Stack<Integer> stack) {
		Matcher m = null;
		
		do {
			m = Pattern.compile(REG_ARITHMETIC_POP_2NUM_PUSH_SUM).matcher(code);
			if(m.find()){
				int a = stack.pop();
				int b = stack.pop();
				stack.push(a+b);
				break;
			}
			
			m = Pattern.compile(REG_ARITHMETIC_POP_2NUM_PUSH_ABS).matcher(code);
			if (m.find()) {
				int a = stack.pop();
				int b = stack.pop();
				stack.push(b-a);
				break;
			}
			
			m = Pattern.compile(REG_ARITHMETIC_POP_2NUM_PUSH_MUL).matcher(code);
			if (m.find()) {
				int a = stack.pop();
				int b = stack.pop();
				stack.push(b*a);
				break;
			}
			
			m = Pattern.compile(REG_ARITHMETIC_POP_2NUM_PUSH_FLOOR_DIV).matcher(code);
			if (m.find()) {
				int a = stack.pop();
				int b = stack.pop();
				stack.push(Math.floorDiv(b, a));
				break;
			}
			
			m = Pattern.compile(REG_ARITHMETIC_POP_2NUM_PUSH_MOD).matcher(code);
			if (m.find()) {
				int a = stack.pop();
				int b = stack.pop();
				stack.push(Math.floorMod(b, a));
				break;
			}
			
			throw new RuntimeException("ARITHMETIC: unclean termination");
		} while(false);
	}
	
	/**
	 * <p>Deal with heap related code.</p>
	 * 
	 * <p>
	 * The following are the requirements for heap in Whitespace system:
	 * <li>[space]: Pop a and b, then store a at heap address b.
	 * <li>[tab]: Pop a and then push the value at heap address a onto the stack.
	 * </p>
	 * @param code the heap code.
	 * @param stack the operation stack.
	 * @param heap the operation heap.
	 * @return null
	 * @throws RuntimeException unknown commands
	 * @throws EmptyStackException should be thrown if there are not enough items on the stack to complete an operation.
	 */
	private static void heap(String code, Stack<Integer> stack, Map<Integer, Integer> heap) {
		Matcher m = null;
		
		do {
			m = Pattern.compile(REG_HEAP_POP_2NUM_STORE_FIRST_AT_ADDR_SECOND).matcher(code);
			if(m.find()){
				int a = stack.pop();
				int b = stack.pop();
				heap.put(b, a);
				break;
			}
			
			m = Pattern.compile(REG_HEAP_POP_2NUM_STORE_FIRST_AT_ADDR_FIRST).matcher(code);
			if (m.find()) {
				int a = stack.pop();
				heap.put(a, a);
				break;
			}
			
			throw new RuntimeException("ARITHMETIC: unclean termination");
		} while(false);
	}

	/**
	 * <p>Deal with IO related code.</p>
	 * 
	 * <p>
	 * The following are the requirements for IO in Whitespace system:
	 * <li>[space][space]: Pop a value off the stack and output it as a character.
	 * <li>[space][tab]: Pop a value off the stack and output it as a number.
	 * <li>[tab][space]: Read a character from input, a, Pop a value off the stack, b, then store the ASCII value of a at heap address b.
	 * <li>[tab][tab]: Read a number from input, a, Pop a value off the stack, b, then store a at heap address b.
	 * </p>
	 * @param code the heap code.
	 * @param stack the operation stack.
	 * @param heap the operation heap.
	 * @param in the input stream.
	 * @param out the output stream.
	 * @return null
	 * @throws RuntimeException unknown commands or couldn't convert input to num/char
	 * @throws EmptyStackException should be thrown if there are not enough items on the stack to complete an operation.
	 */
	private static void io(String code, Stack<Integer> stack, Map<Integer, Integer> heap, InputStream in, OutputStream out) {
		Matcher m = null;
		
		do {
			m = Pattern.compile(REG_IO_POP_CHAR_2_OUTPUT).matcher(code);
			if(m.find()){
				int a = stack.pop();
				output(out, (char)a);
				break;
			}
			
			m = Pattern.compile(REG_IO_POP_NUM_2_OUTPUT).matcher(code);
			if (m.find()) {
				output(out,stack.pop());
				break;
			}
			
			try{
				m = Pattern.compile(REG_IO_GET_CHAR_FROM_INPUT_STORE_ASCII).matcher(code);
				if(m.find()){
					char a = paringInputChars(in);
					int b = stack.pop();
					heap.put(b, (int)a);
					break;
				}
				
				m = Pattern.compile(REG_IO_GET_NUM_FROM_INPUT_STORE_ASCII).matcher(code);
				if (m.find()) {
					int a = paringInputNum(in);
					int b = stack.pop();
					heap.put(b, a);
					break;
				}
			} catch (IOException e) {
				throw new RuntimeException("ARITHMETIC: couldn't convert input to num/char");
			}
			
			throw new RuntimeException("ARITHMETIC: unclean termination");
		} while(false);
	}

	// transforms space characters to ['s','t','n'] chars;
	public static String unbleach(String code) {
		return code != null ? code.replaceAll("[^ \t\n]*", "").replace(' ', 's').replace('\t', 't')
				.replace('\n', 'n') : null;
	}


	// solution 1
	public static String execute(String code, InputStream input) {
		// ... you code ...
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		return execute(code, input, out);
	}
	
	// solution 2
	public static String execute(String code, InputStream input, OutputStream output) {
		Stack<Integer> stack = new Stack<>();
		Map<Integer, Integer> heap = new HashMap<>();
		Map<String, Integer> nameSpace = new HashMap<>();
		List<String> codeSection = new ArrayList<String>();	//code section should be in order.
		
		// output stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		// convert code to readable format.
		code = unbleach(code);
		println("Input string after unbleach: " + code);

		// Scan code and fill code section and name space for program.
		if(code.matches(String.format("^(%s)+$",getWholeReg(REG_ARR)))) {
			Matcher m = Pattern.compile(getWholeReg(REG_ARR)).matcher(code);
			
			int addr = 0;
			while (m.find()) {
				String s = m.group();
				
				//  Mark locations in the program with all labels. 
				Matcher mt = Pattern.compile(REG_FLOW_CONTROL_MARK_FUNC).matcher(s);
				if(mt.find()) {
					String name = mt.group(1);
					nameSpace.put(name, addr);
				} else {
					// if don't match REG_FLOW_CONTROL_MARK_FUNC, just arrange a addresses for every expression in order.
					codeSection.add(s);
					addr++;
				}
			}
			
		} else {
			throw new RuntimeException("The input code format error.");
		}
		
		executeCode(codeSection, nameSpace, stack, heap, input, baos);

		// convert ByteArrayOutputStream to OutputStream.
	    try {
			byte[] arr = baos.toByteArray();
			output.write(arr);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} 
	    
		return baos.toString();
	}
	
	private static void executeCode(List<String> codeSection, Map<String, Integer> nameSpace,
			Stack<Integer> stack, Map<Integer, Integer> heap, InputStream input, OutputStream output) {
		// TODO Auto-generated method stub
		Stack<Integer> progStack = new Stack<Integer>();
		int pointer = 0;
		
		while(pointer<codeSection.size() && pointer >= 0) {
			String code = codeSection.get(pointer++);
			
			if(code.matches(getWholeReg(STACK_REG_ARR))) {
				println("\nStart to deal stack :" + code);
				stack(code,stack);
			} else if(code.matches(getWholeReg(ARITHMETIC_REG_ARR))) {
				println("\nStart to deal arithmetic :" + code);
				arithmetic(code,stack);
			} else if(code.matches(getWholeReg(HEAP_REG_ARR))) {
				println("\nStart to deal heap :" + code);
				heap(code,stack,heap);
			} else if(code.matches(getWholeReg(IO_REG_ARR))) {
				println("\nStart to deal io :" + code);
				io(code,stack,heap,input,output);
			} else if(code.matches(getWholeReg(FLOW_REG_ARR))){
				println("\nStart to deal flow control :" + code);
				
				Matcher mt = Pattern.compile(REG_FLOW_CONTROL_EXIT_ALL).matcher(code);
				if(mt.find()) {
					break;
				}
				
				// have been deal before call this function.
				mt = Pattern.compile(REG_FLOW_CONTROL_MARK_FUNC).matcher(code);
				if(mt.find()) {
					throw new RuntimeException("Shouldn't have REG_FLOW_CONTROL_MARK_FUNC code");
				}
				
				mt = Pattern.compile(REG_FLOW_CONTROL_CALL_FUNC).matcher(code);
				if(mt.find()) {
					progStack.push(pointer);
					String label = parsingLabel(mt.group(1));
					pointer = nameSpace.get(label);
				}
				
				mt = Pattern.compile(REG_FLOW_CONTROL_EXIT_FUNC).matcher(code);
				if(mt.find()) {
					pointer = progStack.pop();
				}
				
				mt = Pattern.compile(REG_FLOW_CONTROL_GO).matcher(code);
				if(mt.find()) {
					String label = parsingLabel(mt.group(1));
					pointer = nameSpace.get(label);
				}
				
				mt = Pattern.compile(REG_FLOW_CONTROL_GO_ZERO).matcher(code);
				if(mt.find()) {
					if(stack.pop() == 0) {
						String label = parsingLabel(mt.group(1));
						pointer = nameSpace.get(label);
					}
				}
				
				mt = Pattern.compile(REG_FLOW_CONTROL_GO_LESS_ZERO).matcher(code);
				if(mt.find()) {
					if(stack.pop() < 0) {
						String label = parsingLabel(mt.group(1));
						pointer = nameSpace.get(label);
					}
				}
			}
		}
		
	}

	private static void print(String s) {
		if(DEBUG) System.out.print(s);
	}

	private static void println(String s) {
		if(DEBUG) System.out.println(s);
	}
	
	public static void main(String[] args) {
		String[][] tests = { 
				{ "   \t     \t\n\t\n  \n\n\n", "A" }, 
				{ "   \t    \t \n\t\n  \n\n\n", "B" },
				{ "   \t    \t\t\n\t\n  \n\n\n", "C" }, 
			    {"blahhhh   \targgggghhh     \t\n\t\n  \n\n\n", "A"},
			    {" I heart \t  cats  \t \n\t\n  \n\n\n", "B"},
			    {"   \t  welcome  \t\t\n\t\n to the\nnew\nworld\n", "C"},
			    {"   \t\t\n   \t\t\n\t\n \t\t\n \t\n\n\n", "33"},
			    {"   \t\t\n \n \t\n \t\t\n \t\n\n\n", "33"},
			    {"   \t\n   \t \n   \t\t\n \t  \t \n\t\n \t\n\n\n", "1"},
			    {"   \t\n   \t \n   \t\t\n \t  \t\n\t\n \t\n\n\n", "2"},
			    {"   \t\n   \t \n   \t\t\n \t   \n\t\n \t\n\n\n", "3"},
			    {"   \t\t\n   \t \n \n\t\t\n \t\t\n \t\n\n\n", "32"},
			    {"   \t\t\n   \t \n \n\t \n\n\t\n \t\n\n\n", "2"},
			    {"   \t\t\n   \t \n   \t\n   \t  \n   \t\t \n   \t \t\n   \t\t\t\n \n\t \t\n \t\t\n\t\n \t\t\n \t\t\n \t\t\n \t\n\n\n", "5123"},
			    {"  \t\t\n\t\n \t\n\n\n", "-1"},
			    {"  \t\t \n\t\n \t\n\n\n", "-2"},
			    {"  \t\t\t\n\t\n \t\n\n\n", "-3"},
			    {"   \t\n\t\n \t\n\n\n", "1"},
			    {"   \t \n\t\n \t\n\n\n", "2"},
			    {"   \t\t\n\t\n \t\n\n\n", "3"},
			    {"    \n\t\n \t\n\n\n", "0"}
		};
		
		for (String[] test : tests) {
			System.out.println(WhitespaceInterpreter.execute(test[0], null));
		}
	}

  
}
