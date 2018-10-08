// Starter code for lp1.
// Version 1.0 (8:00 PM, Wed, Sep 5).

/**
 * @author Nihal Abdulla PT - nxp171730
 * @author Amal Mohan - axm179030
 * @author Mihir Hindocha - mxh170027
 * Implementing Integer Arithmetic with arbitrarily large numbers. The main operations are:
 * Num(String s): Constructor for Num class; takes a string s as parameter, with a number in decimal, and creates the Num object representing that number in the chosen base. Note that, the string s is in base 10, even if the chosen base is not 10. The string s can have arbitrary length.
 * Num(long x): Constructor for Num class.
 * String toString(): convert the Num class object into its equivalent string (in decimal). There should be no leading zeroes in the string.
 * Num add(Num a, Num b): sum of two numbers a+b stored as Num.
 * Num subtract(Num a, Num b): a-b
 * Num product(Num a, Num b): product of two numbers a*b.
 * Num power(Num x, long n): given an Num x, and n, returns the Num corresponding to x^n (x to the power n). Assume that n is a nonnegative number. Use divide-and-conquer to implement power using O(log n) calls to product and add.
 * printList(): Print the base + ":" + elements of the list, separated by spaces.
 * Num divide(Num a, Num b): Integer division a/b. Use divide-and-conquer or division algorithm. Return null if b=0.
 * Num mod(Num a, Num b): remainder you get when a is divided by b (a%b). Assume that a is non-negative, and b > 0. Return null if b=0.
 * Num squareRoot(Num a): return the square root of a (truncated). Use binary search. Assume that a is non-negative. Return null if b < 0.
 */

// Change following line to your NetId
package LongInt;

import java.util.*;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Num implements Comparable<Num> {

    static long defaultBase = 1000000000L; // Change as needed
    long base = 1000000000L; // Change as needed

    /**
	 * Array to store arbitrarily large integers
	 */
	long[] arr;

	/**
	 * Boolean flag to represent negative numbers
	 */
	boolean isNegative;

	/**
	 * Actual number of elements of array that are used; number is stored in
	 * arr[0..len-1]
	 */
	int len;

	/**
	 * HashMap to store the precedence values of the different operators.
	 * Used for evaluation of expressions
	 */
	static HashMap<String, Integer> precedenceMap = new HashMap<>() {
		{
			put("^", 1);
			put("*", 2);
			put("/", 2);
			put("%", 2);
			put("+", 3);
			put("-", 3);
			put("(", 4);
		}
	};

	/**
	 * This constructor creates a Num object for a decimal number passed as a
	 * string. The String is converted to Num object in defaultbase.
	 *
	 * @ throws: NoSuchElementException
	 * 		when string is null or is empty or contains alphabet
	 * @param s
	 *            Base of String Decimal is base 10
	 */
	public Num(String s) {
		if (s == null || s.equals(""))
			throw new NoSuchElementException();
		//pattern to check for alphabets if any in input string
		String pattern = "[a-zA-Z]";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(s);
		if (m.find())
			throw new NoSuchElementException();
		s = s.trim();
		this.isNegative = s.charAt(0) == '-';
		//handle for negative numbers.
		s = this.isNegative ? s.substring(1) : s;

		//setting array to zero if input contains only zeros
		if (s.matches("0*")) {
			this.len = 1;
			this.arr = new long[this.len];
			this.arr[0] = 0;
			return;
		}

		//eliminating leading zeros from the string
		while (true) {
			if (s.charAt(0) == '0' && !s.equals("0"))
				s = s.substring(1);
			else
				break;
		}

		int strLen = s.length();
		long[] tempArr = new long[strLen];
		//populating object array with entries from string
		for (int i = strLen - 1; i >= 0 && strLen - 1 - i >= 0; i--)
			tempArr[strLen - 1 - i] = Long.parseLong(s.substring(i, i + 1));
		this.arr = tempArr;
		this.len = tempArr.length;
		//once array is populated converting the array to default base of the Num class
		Num x = convertBase(defaultBase);
		this.arr = x.getArr();
		this.len = x.getLen();
	}

	/**
	 * This constructor creates a Num object for a decimal number passed as a long.
	 * The long is converted to Num object in defaultbase.
	 *
	 * @param x
	 *            long variable
	 */
	public Num(long x) {
		this(x,defaultBase);
	}

	/**
	 * This constructor creates a Num object for a array of long. The array is
	 * converted to Num object.
	 *
	 * @param x
	 *            long array of a number
	 */
	public Num(long[] x) {
		this.arr = x;
		this.len = x.length;
	}

	/**
	 * This constructor creates a Num object for an array. The array is converted to
	 * Num object in given base.
	 *
	 * @param x
	 *            long array of number
	 * @param base
	 *            resultant base needed
	 */
	public Num(long[] x, long base) {
		this(x);
		this.base = base;
	}

	/**
	 * This constructor creates a Num object for a number passed as a long. The long
	 * is converted to Num object in the base newBase.
	 *
	 * @param x
	 *            long number
	 * @param newBase
	 *            resultant base needed
	 */
	public Num(long x, long newBase) {
		this.isNegative = x < 0;
		//storing number as positive number and setting isnegative flag as true if number is negative
		x = this.isNegative ? 0 - x : x;
		this.base = newBase;
		this.len = 0;
		if (x != 0) {
			int index = 0;
			//finding length of array required in default base
			while ((long) Math.pow(newBase, this.len) <= Math.abs(x))
				this.len = this.len + 1;
			this.arr = new long[this.len];
			//converting number in base 10 to default base
			while (x != 0) {
				this.arr[index++] = x % newBase;
				x /= newBase;
			}
		} else {
			//when input number is zero
			this.len = 1;
			this.arr = new long[this.len];
			this.arr[0] = 0;
		}
	}

	/**
	 * A static method to add the given two Num objects passed as arguments. Both
	 * the arguments are expected to be in the same base.
	 *
	 * @param a
	 *            first number to be added
	 * @param b
	 *            second number to be added
	 * @return Num object pertaining to the sum of input objects
	 */
	public static Num add(Num a, Num b) {
		int aLength = a.getLen(), bLength = b.getLen();
		int minLength = Math.min(aLength, bLength);
		int maxLength = Math.max(aLength, bLength);
		long[] resultArr = new long[maxLength + 1];
		//Using subtract to compute the answer when on of the numbers is negative
		if (a.isNegative && !b.isNegative) {
			Num c = new Num(a.getArr(),a.base());
			c.isNegative = false;
			return subtract(b, c);
		}
		//using add of positive numbers and swapping sign when both numbers are negative
		else if (a.isNegative) {
			Num c = new Num(a.getArr(),a.base());
			c.isNegative = false;
			Num d = new Num(b.getArr(),b.base());
			d.isNegative = false;
			Num res = add(c, d);
			res.makeNegative();
			return res;
		}
		else if (b.isNegative) {
			Num c = new Num(b.getArr(),b.base());
			c.isNegative = false;
			return subtract(a, c);
		}
		long carry = 0;
		int j = 0;
		long[] aArr = a.getArr();
		long[] bArr = b.getArr();
		//addition till minimum length of the both the arrays is reached
		// using carry variable to handle overflow
		while (j < minLength) {
			resultArr[j] = (carry + aArr[j] + bArr[j]) % a.base();
			carry = (carry + aArr[j] + bArr[j]) / a.base();
			j++;
		}
		//adding carry and copying rest of the numbers from longer array to the result
		if (aLength > bLength) {
			while (j < maxLength) {
				resultArr[j] = (a.getArr()[j] + carry) % a.base();
				carry = (aArr[j] + carry) / a.base();
				j++;
			}
		}
		if (aLength < bLength) {
			while (j < maxLength) {
				resultArr[j] = (b.getArr()[j] + carry) % a.base();
				carry = (bArr[j] + carry) / b.base();
				j++;
			}
		}
		//if there is a carry after all the addition it is added to the resultant array
		if (carry > 0) {
			resultArr[j] = carry;
		}
		//handling leading zeros in result
		resultArr = a.trimLeadingZeros(resultArr);
		Num resultNum = new Num(resultArr, a.base());
		if (a.isNegative && b.isNegative) {
			resultNum.makeNegative();
		}
		return resultNum;
	}

	/**
	 * A static method to subtract the given two Num objects passed as arguments.
	 * Both the arguments are expected to be in the same base.
	 *
	 * @param a
	 *            first number in subtraction
	 * @param b
	 *            second number in subtraction
	 * @return Num object pertaining to the difference of input objects
	 */
	public static Num subtract(Num a, Num b) {
		int maxLength = Math.max(a.getLen(), b.getLen());
		long[] resultArr = new long[maxLength];
		//if both numbers: a,b(a-b) are negative, signs of numbers are swapped and b-a is performed
		if (a.isNegative && b.isNegative) {
			Num c = new Num(b.getArr(),b.base());
			c.isNegative = false;
			Num d = new Num(a.getArr(),a.base());
			d.isNegative = false;
			return subtract(c, d);
		}
		//if only one of the numbers is negative, an is performed
		if (b.isNegative) {
			Num c = new Num(b.getArr(),b.base());
			c.isNegative = false;
			return add(a, c);
		}
		if (a.isNegative) {
			Num c = new Num(b.getArr(),b.base());
			c.isNegative = true;
			return add(a, c);
		}
		//if in (a-b), b is greater than a, b-a is performed and the sign is made negative
		if (b.compareTo(a) > 0) {
			Num res = subtract(b, a);
			res.makeNegative();
			return res;
		}
		long[] aArr = a.getArr();
		long[] bArr = b.getArr();
		//using borrow to track borrow from current digit to a lesser significant digit
		boolean borrow = false;
		int k = 0;
		for (int i = 0; i < b.getLen(); i++) {
			//In a-b, if borrow exists it is subtracted from the current digit in a
			if (borrow) {
				//checking if borrow is required from previous digit
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
		//in a-b, copying rest of the digits from a to result after handling borrow from previous digit
		for (int i = b.getLen(); i < a.getLen(); i++) {
			if (borrow) {
				if (aArr[i] != 0) {
					resultArr[k++] = aArr[i] - 1;
					borrow = false;
				} else {
					resultArr[k++] = a.base() - 1;
				}
			} else {
				resultArr[k++] = aArr[i];
			}
		}
		resultArr = a.trimLeadingZeros(resultArr);
		return new Num(resultArr, a.base());
	}

	/**
	 * A static method to multiply the given two Num objects passed as arguments.
	 * Both the arguments are expected to be in the same base.
	 *
	 * @param a
	 *            first number in multiplication
	 * @param b
	 *            second number in multiplication
	 * @return Num object pertaining to the product of input objects
	 */
	public static Num product(Num a, Num b) {
		long[] result = new long[a.getLen() + b.getLen()];
		//counter to shift addition scope as digits needed to be shifted in subsequent steps of multiplication
		//eg 45 *32
		//   90
		// 128  +
		int counter = 0, index;
		long temp;
		long resultValue;
		//carry to handle multiplication carry for example in 45*32, first operation is 5*2 and last digit is 5*2%10 and carry would be 5*2/10=1 to add with result of next multiply operation
		//add carry to handle addition carry during add step in multiplication of multidigit numbers
		long carry, addCarry;
		for (long x : b.getArr()) {
			index = counter;
			carry = 0;
			addCarry = 0;
			for (long y : a.getArr()) {
				temp = x * y + carry;
				resultValue = result[index];
				//generating digit of result based on product and modulas with base
				result[index] = (result[index] + (temp % a.base()) + addCarry) % a.base();
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
		Num res = new Num(result, a.base());
		if (!(a.isNegative&&b.isNegative)&&(a.isNegative || b.isNegative)) {
			res.makeNegative();
		}
		return res;
	}

	/**
	 * A static method to calculate the power of the given Num object to the value
	 * n, given as parameters. The function uses the divide and conquer technique to
	 * achieve the same.
	 *
	 * @param a
	 *            Number whose power is to be found
	 * @param n
	 *            the power to which it should be raised
	 * @return Num object providing the resultant power a^n
	 */
	public static Num power(Num a, long n) {
		if (n == 0)
			return new Num(1);
		Num intermediate = power(a, n / 2);
		//if n is even power is a^n/2 * a^n/2 else it is a^n/2 * a^n/2 * a
		if (n % 2 == 0)
			return product(intermediate, intermediate);
		else
			return product(a, product(intermediate, intermediate));
	}

	/**
	 * A static method to divide the given two Num objects passed as arguments. Both
	 * the arguments are expected to be in the same base. The program uses binary
	 * search to calculate a/b.
	 *
	 * @throws IllegalArgumentException
	 *             when a/0 occurs
	 * @param a
	 *            first number in the division (dividend)
	 * @param b
	 *            second number in the division (divisor)
	 * @return Num object returning the quotient
	 */
	public static Num divide(Num a, Num b) {
	    boolean asign=false,bsign=false;
	    //making positive copies of inputs(a,b) if they are negative and sign is handled at the end
	    if(a.isNegative){
            Num c=new Num(a.getArr(),a.base());
            c.isNegative=false;
            a=c;
            asign=true;
        }
        if(b.isNegative){
            Num d=new Num(b.getArr(),b.base());
            d.isNegative=false;
            b=d;
            bsign=true;
        }
        //division by zero
		if (b.compareTo(new Num(0,b.base())) == 0)
			throw new IllegalArgumentException("Division by zero");
		//using low and high set to 0 and number respectively and finding mid which is half of the number.
		//low and high is moved based on mid*b
		Num low = new Num(0,a.base());
		Num high = new Num(a.getArr(),a.base());
		Num mid;
		while (low.compareTo(high) < 0) {
			mid = add(low, high).by2();
			Num c = product(b, mid);
			int compareToRes = c.compareTo(a);
			if (compareToRes == 0){
                if (!(asign&&bsign)&&(asign || bsign)) {
                    mid.makeNegative();
                }
                return mid;
            }
			else if (compareToRes > 0)
				high = mid;
			//if difference between b*mid and a is lesser than b then mid is the quotient of the division
			else if (subtract(a, c).compareTo(b) < 0){

                if (!(asign&&bsign)&&(asign || bsign)) {
                    mid.makeNegative();
                }
                return mid;
			}
			else
				low = mid;
		}
        if (!(asign&&bsign)&&(asign || bsign)) {
            low.makeNegative();
        }
        return low;
	}

	/**
	 * A static method to perform the modulo operation on the given two Num objects
	 * passed as arguments. Both the arguments are expected to be in the same base.
	 *
	 * @param a
	 *            first number which is the dividend
	 * @param b
	 *            second number which is the divisor
	 * @return Num object returning the remainder
	 */
	public static Num mod(Num a, Num b) {
		//using concept of a%b=floor(a/b)*b-a
		Num inter = divide(a, b);
		Num prod = product(inter, b);
		return subtract(a, prod);
	}

	/**
	 * A static method to find squareroot for the given Num object passed as
	 * argument. The program uses binary search to calculate squareroot.
	 *
	 * @param a
	 *            input number to find square root of
	 * @return Num object returning the square root
	 */
	public static Num squareRoot(Num a) {
		//using low and high set to 0 and number respectively and finding mid which is half of the number.
		//low and high is moved based on mid*mid
		Num low = new Num(0,a.base());
		Num high = new Num(a.getArr());
		Num res = new Num(0,a.base());
		Num midSquare;
		Num mid;
		while (low.compareTo(high) < 0) {
			mid = add(low, high).by2();
			//if mid does not change in an iteration(found best value for square root)
			if (mid.compareTo(low) == 0) {
				break;
			}
			midSquare = product(mid, mid);
			int compareResult = midSquare.compareTo(a);
			if (compareResult == 0) {
				return mid;
			}
			else if (compareResult < 0) {
				low = mid;
				res = mid;
			}
			else {
				high = mid;
			}
		}
		return res;
	}


	/**
	 * A helper method defined to compare the values of "this" to "other" and return
	 * an integer value deciding which number is greater than the other or if it is
	 * equal
	 *
	 * @param other
	 *            the Num object "this" will be compared to
	 * @return +1 if this is greater, 0 if both are equal and -1 otherwise
	 */
	public int compareTo(Num other) {
		int result = 0;
		//direct cases where both numbers are of opposite signs
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
			//if numbers are of same length and of same sign individual digits are compared to find the result
			for (i = this.getLen() - 1; i >= 0; i--) {
				if (this.arr[i] < other.arr[i]) {
					result = this.isNegative ? 1 : -1;
					break;
				} else if (this.arr[i] > other.arr[i]) {
					result = this.isNegative ? -1 : 1;
					break;
				} else if (this.arr[i] == other.arr[i] && i == 0) {
					result = 0;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Helper method to print the output using the format "base: elements of list
	 * ..." For example, if base=100, and the number stored corresponds to 10965,
	 * then the output is "100: 65 9 1"
	 */
	public void printList() {
		System.out.print(this.base + ": ");
		if (isNegative)
			System.out.print("- ");
		for (long x : this.arr)
			System.out.print(x + " ");
	}

	/**
	 * Helper method to convert the number in this object to a string in base 10
	 *
	 * @return string containing the number in base 10
	 */
    public String toString() {
    	//initially number is converted to default base
        Num decimalNumber = this.convertToDefaultBase();
        StringBuilder sbResult = new StringBuilder();
        long temp;
        int digits = (int) Math.log10(base());
        long count;
        long zeros;
        //extracting each digit and appending to result by adding necessary zeros to from
        for (int i=decimalNumber.len-1;i>=0;i--){
            //first digit not not be checked to add leading zeros
        	if (i == len - 1) {
                sbResult.append(decimalNumber.arr[i]);
                continue;
            }
            count = 0;
        	temp = arr[i];
            while (temp > 0) {
                temp = temp / 10;
                count++;
            }
            //total maximum digits-count will give the number of zeros to append
            zeros = digits - count;
            for(int j=0;j<zeros;j++){
                sbResult.append("0");
            }
            sbResult.append(decimalNumber.arr[i]);
        }
        return this.toString(sbResult.toString());
    }

	/**
	 * Helper method to convert given string to string without leading zeroes
	 *
	 * @param val
	 *            the input string
	 * @return -ve or +ve number as a string without leading zeroes
	 */
	private String toString(String val) {
		val = val.charAt(0) == '0' ? val.substring(1) : val;
		return this.isNegative ? "-" + val : val;
	}

	/**
	 * Helper method to return the base value of type long
	 */
	public long base() {
		return base;
	}

    /**
     * Helper method to trin the leading zeroes from the input array
     *
     * @param lngArr
     *            array of long which where leading zeroes are to be removed
     * @return resultant long array without the leading zeroes
     */
    private long[] trimLeadingZeros(long[] lngArr) {
		long[] result;
		int leadingNonZeroIndex = lngArr.length - 1;
		//finding index of array without leading zeros
		while (leadingNonZeroIndex >= 0 && lngArr[leadingNonZeroIndex] == 0)
			leadingNonZeroIndex--;
		//copying current array to result without the leading zeros
		if (leadingNonZeroIndex >= 0) {
			result = new long[leadingNonZeroIndex + 1];
			int index = 0;
			while (index <= leadingNonZeroIndex)
				result[index] = lngArr[index++];
		} else
			result = new long[] { 0 };
		return result;
	}

	/**
	 * Method to convert the Num object's base from 10 to the new base
	 *
	 * @param newBase
	 *            the value of the newBase
	 * @return Num object with value in the newBase
	 */
	public Num convertBase(long newBase) {
		Num sum = new Num(0);
		sum.base = newBase;
		Num baseInNewBase = new Num(10, newBase);
		for (int i = this.getLen() - 1; i >= 0; i--) {
			Num prodElm = product(sum, baseInNewBase);
			prodElm.base = newBase;
			Num arrElm = new Num(this.arr[i], newBase);
			arrElm.base = newBase;
			sum = add(prodElm, arrElm);
		}
		sum.isNegative = this.isNegative;
		return sum;
	}

	/**
	 * Method to convert the Num object's base from 10 to the new base
	 *
	 * @param newBase
	 *            the value of the newBase
	 * @return Number equal to "this" number, in base = newBase
	 */
	public Num convertBase(int newBase) {
		Num sum = new Num(0);
		sum.base = newBase;
		Num baseInNewBase = new Num(base, newBase);
		baseInNewBase.base = newBase;
		for (int i = getLen() - 1; i >= 0; i--) {
			Num prodElm = product(sum, baseInNewBase);
			prodElm.base = newBase;
			Num arrElm = new Num(this.arr[i], newBase);
			arrElm.base = newBase;
			sum = add(prodElm, arrElm);
		}
		sum.isNegative = this.isNegative;
		return sum;
	}

    /**
     * Method to convert the Num object's base to default base
     *
     * @return Number equal to "this" number, in base = 1000000000L
     */
    public Num convertToDefaultBase() {
		if(this.base==defaultBase){
			return this;
		}
		Num sum = new Num(0);
		long newBase=1000000000L;
		sum.base = newBase;
		Num baseInNewBase = new Num(base, newBase);
		baseInNewBase.base = newBase;
		for (int i = getLen() - 1; i >= 0; i--) {
			Num prodElm = product(sum, baseInNewBase);
			prodElm.base = newBase;
			Num arrElm = new Num(this.arr[i], newBase);
			arrElm.base = newBase;
			sum = add(prodElm, arrElm);
		}
		sum.isNegative = this.isNegative;
		return sum;
    }

	/**
	 * Helper method to return the array element of the Num Object
	 */
	public long[] getArr() {
		return arr;
	}

	/**
	 * Helper method to calculate the Number/2 in the base the number already is in.
	 * The method is made for use in binary search
	 *
	 * @return resultant Num object with original number/2
	 */
	public Num by2() {
		long carry = 0;
		long[] by2Arr = new long[this.len];
		//iteratively calculating by2 by going through elements of array
		for (int i = this.getLen() - 1; i >= 0; i--) {
			by2Arr[i] = ((carry * this.base()) + arr[i]) / 2;
			carry = ((carry * this.base()) + arr[i]) % 2;
		}
		by2Arr = trimLeadingZeros(by2Arr);
		return new Num(by2Arr, this.base());
	}

	/**
	 * Evaluate an expression in postfix notation and return resulting number. Each
	 * string is one of: "*", "+", "-", "/", "%", "^", "0", or a number:
	 * [1-9][0-9]*. There is no unary minus operator.
	 *
	 * @param expr
	 *            string array that contains the postfix expression to be evaluated
	 * @return resultant Num from the computation and null if the expression is not
	 *         valid
	 */
	public static Num evaluatePostfix(String[] expr) {
		//stack to store operands and result
		Stack<String> operands = new Stack<>();
		for (String op : expr) {
			if (precedenceMap.containsKey(op)) {
				if (operands.isEmpty())
					return null;
				String n2 = operands.pop();
				if (operands.isEmpty())
					return null;
				String n1 = operands.pop();
				//whenever operator is encountered operands from stack is removed evaluated for the operator
				//the result is pushed to stack again
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
			//pushing operands to stack
			else if (op != null && !op.equals(""))
				operands.push(op);
		}
		//if stack has more than one entry in the end the expression has errors
		if (operands.size() > 1)
			return null;
		return new Num(operands.pop());
	}

	/**
	 * Evaluate an expression in infix notation and return resulting number Each
	 * string is one of: "*", "+", "-", "/", "%", "^", "(", ")", "0", or a number:
	 * [1-9][0-9]*. There is no unary minus operator.
	 *
	 * converts expression to postfix and evaluate postfix expression
	 *
	 * @param expr
	 *            string array that contains the infix expression to be evaluated
	 * @return resultant Num from the computation
	 */
	public static Num evaluateInfix(String[] expr) {
		//stack to push operators
		Stack<String> stack = new Stack<>();
		//queue to push result of conversion to postfix
		String[] queue = new String[expr.length];
		int i = 0;
		for (String op : expr) {
			//operator is pushed to queue
			if (op.matches("-?\\d+"))
				queue[i++] = op;
			else {
				if (stack.isEmpty() || op.equals("("))
					stack.push(op);
				//when closed parenthesis is encountered the stack between open and closed parenthesis has sub expression.Hence stack is popped till open parenthesis is foung
				else if (op.equals(")")) {
					String tos = stack.pop();
					while (!tos.equals("(")) {
						queue[i++] = tos;
						if (stack.isEmpty())
							return null;
						tos = stack.pop();
					}
				}
				else {
					//if a lower precedence operator is encountered in input, higher precedence operators in stack is removed and pushed to result
					while (isHigherPrecedence(op, stack.peek())) {
						String tos = stack.pop();
						queue[i++] = tos;
						if (stack.isEmpty())
							break;
					}
					stack.push(op);
				}
			}
		}
		while (!stack.isEmpty())
			queue[i++] = stack.pop();
		return evaluatePostfix(queue);
	}

	/**
	 * Helper method to decide the precedence of the 2 operators in the input
	 *
	 * @param a
	 *            the first operator
	 * @param b
	 *            the second operator
	 * @return true id operator a has higher precedence, false otherwise
	 */
	private static boolean isHigherPrecedence(String a, String b) {
		return precedenceMap.get(a) > precedenceMap.get(b);
	}

	/**
	 * Helper method to make the value of input Num object negative
	 */
	public void makeNegative() {
		this.isNegative = true;
	}

	/**
	 * Helper method to return the length of the Num object
	 */
	public int getLen() {
		return this.len;
	}

}