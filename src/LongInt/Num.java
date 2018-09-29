// Starter code for lp1.
// Version 1.0 (8:00 PM, Wed, Sep 5).

// Change following line to your NetId
/*
Nihal Abdulla PT - nxp171730
Amal Mohan - axm179030
Mihir Hindocha - mxh170027
 */
package LongInt;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.NoSuchElementException;

public class Num implements Comparable<Num> {

	static long defaultBase = 10; // Change as needed
	long base = 10; // Change as needed
	long[] arr; // array to store arbitrarily large integers
	boolean isNegative; // boolean flag to represent negative numbers
	int len; // actual number of elements of array that are used; number is stored in
				// arr[0..len-1]

	//Constructor takes string as argument and convert to array of long in chosen base.
	//The constructor checks if any letters are present in the input string
	//Trims the string to remove any white spaces
	//Checks if only zeros are present in the string. If so, returns an array with only one element, 0
	//Stores the decimal value in a temp array. Calls the convertBase method to get the value in chosen base
	public Num(String s) {
		if(s == null || s == "")
			throw new NoSuchElementException();
		String pattern = "[a-zA-Z]";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(s);
		if(m.find())
			throw new NoSuchElementException();
		s = s.trim();
		this.isNegative = s.charAt(0) == '-';
		s = this.isNegative ? s.substring(1) : s;
		if(s.matches("0*")) {
			this.len = 1;
			this.arr = new long[this.len];
			this.arr[0] = 0;
			return;
		}
		while (true) {
			if (s.charAt(0) == '0' && !s.equals("0"))
				s = s.substring(1);
			else
				break;
		}
		int strLen = s.length();
		long[] tempArr = new long[strLen];
		for(int i = strLen - 1; i >= 0 && strLen-1-i >=0 ; i--)
			tempArr[strLen - 1 - i] = Long.parseLong(s.substring(i, i + 1));
		this.arr = tempArr;
		this.len = tempArr.length;
		Num x = convertBaseFirst((int)this.base());
		this.arr = x.getArr();
		this.len = x.getLen();
	}

	//Constructor takes a long integer as argument and gives the object in chosen base.
	//For 0 it outputs array with only one element, 0.
	public Num(long x) {
		this.isNegative = x < 0;
		x = this.isNegative ? 0 - x : x;
		this.len = 0;
		this.base = 10;
		if(x != 0) {
			int index = 0;
			while ((long) Math.pow(this.base(), this.len) <= Math.abs(x))
				this.len = this.len + 1;
			this.arr = new long[this.len];
			while (x != 0) {
				this.arr[index++] = x % base;
				x /= base;
			}
		}
		else {
			this.len = 1;
			this.arr = new long[this.len];
			this.arr[0] = 0;
		}
	}

	public Num(long[] x,long base,int len){
		arr=x;
		this.base=base;
		this.len=len;
	}

	//Constructor takes long integer and base as arguments and gives the object corresponding to the given base
	//in the given base.
	public Num(long x, long newBase) {
		this.isNegative = x < 0;
		x = this.isNegative ? 0 - x : x;
		this.base = newBase;
		this.len = 0;
		if(x != 0) {
			int index = 0;
			while ((long) Math.pow(newBase, this.len) <= Math.abs(x))
				this.len = this.len + 1;
			this.arr = new long[this.len];
			while (x != 0) {
				this.arr[index++] = x % newBase;
				x /= newBase;
			}
		}
		else {
			this.len = 1;
			this.arr = new long[this.len];
			this.arr[0] = 0;
		}
	}

	//add method: static. takes two Num objects as arguments
	//expects both the objects to be in the same base
	//finds the sum of two numbers in the same base as that of operands
	public static Num add(Num a, Num b) {
		int aLength = a.getLen(), bLength = b.getLen();
		int minLength = aLength < bLength ? aLength : bLength;
		int maxLength = aLength >= bLength ? aLength : bLength;
		long [] resultArr = new long[maxLength+1];
		StringBuilder result = new StringBuilder();
		if(a.isNegative && !b.isNegative){
			Num c = new Num(a.toString());
			c.isNegative = false;
			return subtract(b,c);
		}
		else if(b.isNegative){
			Num c=new Num(b.toString());
			c.isNegative=false;
			return subtract(a,c);
		}
		long carry = 0;
		int j = 0;
		long[] aArr = a.getArr();
		long[] bArr = b.getArr();
		while (j < minLength) {
			resultArr[j] = (carry + aArr[j] + bArr[j]) % a.base();
			carry = (carry + aArr[j] + bArr[j]) / a.base();
			j++;
		}
		if (aLength > bLength) {
			while (j < maxLength) {
				resultArr[j] = (a.arr[j]+carry)%a.base;
				carry = ((aArr[j] + carry) % a.base()) / a.base();
				j++;
			}
		}
		if (aLength < bLength) {
			while (j < maxLength) {
				resultArr[j] = (b.arr[j]+carry)%a.base;
				carry = ((bArr[j] + carry) % a.base()) / a.base();
				j++;
			}
		}
		if (carry > 0) {
			resultArr[j] = carry;
		}
		if(a.isNegative && b.isNegative) {
			result.append("-");
		}
        resultArr = a.trimLeadingZeros(resultArr);
		int length = 0;
		for(long x: resultArr){
		    length++;
        }
		Num resultNum = new Num(resultArr, a.base(),length);
		if(a.isNegative && b.isNegative) {
			resultNum.makeNegative();
		}
		return resultNum;
	}

	public static Num subtract(Num a, Num b) {
		int maxLength = a.getArr().length > b.getArr().length ? a.getArr().length : b.getArr().length;
		long[] resultArr = new long[maxLength];
		if(a.isNegative && b.isNegative){
			Num c = new Num(b.toString());
			c.isNegative = false;
			Num d = new Num(a.toString());
			d.isNegative = false;
			subtract(c,d);
		}
		if(b.isNegative){
			Num c = new Num(b.toString());
			c.isNegative = false;
			return add(a,c);
		}
		if(a.isNegative){
			Num c = new Num(b.toString());
			c.isNegative = true;
			return add(a,c);
		}
		if (b.compareTo(a) > 0) {
			Num res = subtract(b, a);
			res.makeNegative();
			return res;
		}
		long[] aArr = a.getArr();
		long[] bArr = b.getArr();
		boolean borrow = false;
		int k = 0;
		for (int i = 0; i < b.getLen(); i++) {
			if (borrow) {
				if (aArr[i] - 1 < bArr[i]) {
					resultArr[k++] = a.base() + aArr[i] - 1 - bArr[i];
				}
				else {
					resultArr[k++] = aArr[i] - 1 - bArr[i];
					borrow = false;
				}
			}
			else if (aArr[i] < bArr[i]) {
				resultArr[k++] = a.base() + aArr[i] - bArr[i];
				borrow = true;
			}
			else {
				resultArr[k++] = aArr[i] - bArr[i];
			}
		}
//		if (borrow) {
//			// handling cases of adding extra unnecessary leading zero(eg 100-9)
//			if (a.getLen() > b.getLen() + 1 || aArr[b.getLen()] > 1) {
//				if (aArr[b.getLen()] != 0) {
//					resultArr[k++] = aArr[b.getLen()] - 1;
//				} else {
//					resultArr[k++] = a.base();
//					borrow = true;
//				}
				for (int i = b.getLen(); i < a.getLen(); i++) {
					if (borrow) {
						if (aArr[i] != 0) {
							resultArr[k++] = aArr[i] - 1;
							borrow = false;
						} else {
							resultArr[k++] = a.base()-1;
						}
					} else {
						resultArr[k++] = aArr[i];
					}
				}
//			}
	//	}
//		else
  //      {
    //
     //   }
        resultArr = a.trimLeadingZeros(resultArr);
        int length=0;
        for(long x: resultArr){
            length++;
        }
        Num res=new Num(resultArr, a.base(),length);
		return res;
	}

	private int calculateLength(long[] lngArray) {
        int length=0;
        for(long x: lngArray){
            length++;
        }
        return length;
    }

	public static Num product(Num a, Num b) {
		long[] result = new long[a.getLen() + b.getLen()];
		int counter = 0, index = 0;
		long temp;
		long resultValue;
		long carry = 0, addCarry = 0;
		for (long x : b.getArr()) {
			index = counter;
			carry = 0;
			addCarry = 0;
			for (long y : a.getArr()) {
				temp = x * y + carry;// if the product exceeds the base? should implement addition with base as well
										// as well?
				resultValue = result[index];
				result[index] = (result[index] + (temp % a.base()) + addCarry) % a.base();// does this addition causes
																							// overflow
				carry = temp / a.base();
				addCarry = (resultValue + (temp % a.base()) + addCarry) / a.base();
				index++;
			}
			if (carry > 0) {
				result[index++] = (carry + addCarry) % a.base();
				addCarry = (carry + addCarry) / a.base();
			}
			if (addCarry > 0) {
				result[index] = addCarry;
			}
			counter++;
		}
        result = a.trimLeadingZeros(result);
        int length = 0;
        for(long x: result){
            length++;
        }
		Num res = new Num(result, a.base(),length);
		if(a.isNegative != b.isNegative){
			res.makeNegative();
		}
		return res;
	}

	// Use divide and conquer
	public static Num power(Num a, long n) {
		if (n == 0) {
			return new Num(1);
		}
		Num intermediate = power(a, n / 2);
		if (n % 2 == 0) {
			return product(intermediate, intermediate);
		} else {
			return product(a, product(intermediate, intermediate));
		}
	}

	// compare "this" to "other": return +1 if this is greater, 0 if equal, -1
	// otherwise
	// Use binary search to calculate a/b
	public static Num divide(Num a, Num b) {
	    if(b.compareTo(new Num(0)) == 0)
	        throw new IllegalArgumentException("Division by zero");
        Num low = new Num("0");
        Num high = new Num(a.toString());
        Num mid;
        while (low.compareTo(high) < 0) {
            mid = add(low, high).by2();
            Num c = product(b, mid);
            int compareToRes = c.compareTo(a);
            if (compareToRes == 0) {
                return mid;
            } else if (compareToRes == 1) {
                high = mid;
            } else if (subtract(a, c).compareTo(b) == -1) {
                return mid;
            } else {
                low = mid;
            }
        }
        return low;
    }

	// return a%b
	public static Num mod(Num a, Num b) {
		Num inter = divide(a, b);//implement throw exception in divide by zero
		Num prod = product(inter, b);
		Num result = subtract(a, prod);
		return result;
	}

	// Use binary search
	public static Num squareRoot(Num a) {
		Num low = new Num("0");
		//1912991252611125159670459142926165956543555293
		Num  high = new Num(a.toString());
		Num res = new Num("0");
		Num midSquare;
		Num mid;
		while (low.compareTo(high)<0){
			mid = add(low,high).by2();
			if(mid.compareTo(low) == 0){
				break;
			}
			midSquare = product(mid,mid);
			int compareResult = midSquare.compareTo(a);
			if(compareResult == 0){
				return mid;
			}
			else if(compareResult < 0){
				low = mid;
				res = mid;
			}
			else{
				high = mid;
			}
		}
		return res;
	}

	// Utility functions
	// compare "this" to "other": return +1 if this is greater, 0 if equal, -1
	// otherwise
	public int compareTo(Num other) {
		int result = 0;
		if (other == null && other.getLen() == 0)
			result = this.isNegative ? -1 : 1;
		if (this.isNegative && !other.isNegative)
			result = -1;
		else if (!this.isNegative && other.isNegative)
			result = 1;
		else if (this.getLen() < other.getLen())
			result = this.isNegative ? 1 : -1;
		else if (this.getLen() > other.getLen())
			result = this.isNegative ? -1 : 1;
		else {
			int i;
			for (i = this.getLen() - 1; i >= 0; i--) {
				if (this.arr[i] < other.arr[i]) {
					result = this.isNegative ? 1 : -1;
					break;
				}
				else if (this.arr[i] > other.arr[i]) {
					result = this.isNegative ? -1 : 1;
					break;
				}
				else if (this.arr[i] == other.arr[i] && i == 0) {
					result = 0;
					break;
				}
			}
		}
		return result;
	}

	// Output using the format "base: elements of list ..."
	// For example, if base=100, and the number stored corresponds to 10965,
	// then the output is "100: 65 9 1"
	public void printList() {
		System.out.print(this.base + ": ");
		if(isNegative){
			System.out.print("- ");
		}
		for (long x: this.arr){
			System.out.print(x+" ");
		}
	}

	// Return number to a string in base 10
	public String toString() {
		StringBuilder sbResult = new StringBuilder();
		for (long i : this.arr) {
			sbResult = sbResult.append(i);
		}
		String result = this.toString(sbResult.reverse().toString());
		return result;
	}

	private String toString(String val) {
		val = val.charAt(0) == '0' ? val.substring(1) : val;
		return this.isNegative ? "-" + val : val;
	}

	public long base() {
		return base;
	}

	private long[] trimLeadingZeros(long[] lngArr) {
		long[] result;
		int leadingNonZeroIndex = lngArr.length-1;
		for(; leadingNonZeroIndex >= 0; leadingNonZeroIndex--) {
			if(lngArr[leadingNonZeroIndex] != 0)
				break;
		}
		if(leadingNonZeroIndex >= 0) {
			result = new long[leadingNonZeroIndex + 1];
			for(int i=0;i<=leadingNonZeroIndex;i++) {
				result[i] = lngArr[i];
			}
		}
		else
			result = new long[] {0};
		return result;
	}

	public Num convertBaseFirst(int newBase) {
		Num sum = new Num(0);
		sum.base = newBase;
		Num baseInNewBase = new Num(10, newBase);
		for(int i=this.getLen()-1; i>=0; i--) {
			Num prodElm = product(sum, baseInNewBase);
			Num arrElm = new Num(this.arr[i], newBase);
			sum = add(prodElm, arrElm);
		}
		sum.isNegative = this.isNegative;
		return sum;
	}

	// Return number equal to "this" number, in base=newBase
	public Num convertBase(int newBase) {
		Num sum = new Num("0");
		sum.base = newBase;
		Num baseInNewBase = new Num(this.base(), newBase);
		for(int i=this.getLen()-1; i>=0; i--) {
			Num prodElm = product(sum, baseInNewBase);
			Num arrElm = new Num(this.arr[i], newBase);
			sum = add(prodElm, arrElm);
		}
        sum.isNegative = this.isNegative;
		return sum;
	}

	public long[] getArr() {
		return arr;
	}

	// Divide by 2, for using in binary search

	public Num by2() {
		StringBuilder sbResult = new StringBuilder();
		long carry = 0;
		for (int i = this.getLen() - 1; i >= 0; i--) {
			sbResult.append(((carry * this.base()) + arr[i]) / 2);
			carry = arr[i] % 2;
		}
		String strResult = this.toString(sbResult.toString());
		return new Num(strResult);
	}

	// Evaluate an expression in postfix and return resulting number
	// Each string is one of: "*", "+", "-", "/", "%", "^", "0", or
	// a number: [1-9][0-9]*. There is no unary minus operator.
	public static Num evaluatePostfix(String[] expr) {
		HashSet<String> operator = new HashSet<>(Arrays.asList("+", "*", "-", "/", "%", "^"));
		Stack<String> operands = new Stack<>();
		for (String op : expr) {
			if (operator.contains(op)) {
				if (operands.isEmpty()) {
					return null;
				}
				String n2 = operands.pop();
				if (operands.isEmpty()) {
					return null;
				}
				String n1 = operands.pop();
				switch (op) {
				case "+":
					operands.push(add(new Num(n1), new Num(n2)).toString());
					break;
				case "*":
					operands.push(product(new Num(n1), new Num(n2)).toString());
					break;
				case "-":
					operands.push(subtract(new Num(n1), new Num(n2)).toString());
					break;
				case "/":
					operands.push(divide(new Num(n1), new Num(n2)).toString());
					break;
				case "%":
					operands.push(mod(new Num(n1), new Num(n2)).toString());
					break;
				case "^":
					operands.push(power(new Num(n1), Long.parseLong(n2)).toString());
					break;
				}
			}
			else {
				operands.push(op);
			}
		}
		if(operands.size() > 1) {
			return null;
		}
		return new Num(operands.pop());
	}

	// Evaluate an expression in infix and return resulting number
	// Each string is one of: "*", "+", "-", "/", "%", "^", "(", ")", "0", or
	// a number: [1-9][0-9]*. There is no unary minus operator.
	public static Num evaluateInfix(String[] expr) {
		Stack<String> stack=new Stack<>();
		String[] queue=new String[expr.length];
		HashMap<String,Integer> precedenceMap=new HashMap<String, Integer>();
		precedenceMap.put("^",1);
		precedenceMap.put("*",2);
		precedenceMap.put("/",2);
		precedenceMap.put("%",2);
		precedenceMap.put("+",3);
		precedenceMap.put("-",3);

		int i=0;
		for(String op:expr) {
			if (op.matches("-?\\d+")) {
				queue[i++] = op;
			} else {
				if (stack.isEmpty() || op.equals("(")) {
					stack.push(op);
				} else if (op.equals(")")) {
					String tos = stack.pop();
					while (tos.equals('(') == false) {
						queue[i++] = tos;
						if (stack.isEmpty()) {
							return null;
						}
						tos = stack.pop();
					}
				} else {
					while (isHigherPrecedence(op, stack.peek(),precedenceMap)) {
						String tos = stack.pop();
						queue[i++] = tos;
						if (stack.isEmpty()) {
							break;
						}
					}
					stack.push(op);
				}
			}
		}
		while(!stack.isEmpty()){
			queue[i++]=stack.pop();
		}
		return evaluatePostfix(queue);
	}

	private static boolean isHigherPrecedence(String a, String b,HashMap<String,Integer> precedenceMap) {
		if(precedenceMap.get(a)>precedenceMap.get(b)){
			return true;
		}
		return false;
	}


	public void makeNegative() {
		this.isNegative = true;
	}

	public int getLen() {
		return this.len;
	}

	public void printMethod() {
		/*Num x = new Num(" -0001000000000000000000");
		Num y = new Num(1000000000);
		System.out.println("Compare x and y");
		System.out.println(x.compareTo(y));
		System.out.println("ToString x and y");
		System.out.println(x.toString());
		System.out.println(y.toString());
		System.out.println("By2 of x and y");*/
		Num z = new Num("1500");
		System.out.println(z.convertBase(7).toString());
		//x.by2();
		//y.by2();
	}

	public static void main(String[] args) {
		//Num x = new Num(36);
		//Num y = new Num(5);
		//Num z = divide(x, y);
		//System.out.println(z);
		Num l2=new Num("3659535532566681673026857047264590495633096120170316011130546064934049533282760410899967541");
		Num l3=l2.by2();
		Num l4=product(l3,l3);
		for(int i=l4.getLen()-1;i>=0;i--){
			System.out.print(l4.arr[i]);
		}

		Num k2=squareRoot(l2);
				//1912991252611125159670459142926165956543555293

		Num l=new Num("934687900098779");
		Num k=new Num("937656432400056789");
		Num s=k.convertBase(8);
		Num p=l.convertBase(8);
        Num addi=subtract(k.convertBase(8),l.convertBase(8) );
        addi.printList();

		/*
		 * Num x = new Num(2000); Num y = new Num("-67"); System.out.println(new
		 * Num("1").compareTo(new Num("-1"))); Num z = Num.add(x, y);
		 * System.out.println(z); if (z != null) z.printList();
		 */
		// new Num("123").printMethod();
		// System.out.println("ds");
		// System.out.println(new Num(25).compareTo(new Num(81)));
		// product(new Num(40),new Num(40)).printMethod();
		// Num res=new Num(0);
		// squareRoot(new Num(82));
		//long [] a={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4};
		//long []b={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4};
		//Num s=product(new Num(a,5),new Num(b,5));
        Num lng = new Num(-111111111);
        System.out.println(lng);
        System.out.println(lng.convertBase(10));
		Num str = new Num("-10007821738123");
		System.out.println(str.convertBase(10).toString());
		System.out.println(str.toString());
		Num str1 = new Num("1213500");
		System.out.println(str1.toString());
		System.out.println(add(str, str1).toString());
		System.out.println(product(str, str1).toString());
		/*System.out.println(new Num("10000000000000000000").toString());
		System.out.println()*/
		/*System.out.println("ds");
		System.out.println(new Num(25).compareTo(new Num(81)));
		product(new Num(40),new Num(40)).printMethod();
		Num res=new Num(0);
		squareRoot(new Num(82));*/
	}
}
