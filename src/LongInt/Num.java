
// Starter code for lp1.
// Version 1.0 (8:00 PM, Wed, Sep 5).

// Change following line to your NetId
package LongInt;

public class Num  implements Comparable<Num> {

    static long defaultBase = 10;  // Change as needed
    long base = defaultBase;  // Change as needed
    long[] arr;  // array to store arbitrarily large integers
    boolean isNegative;  // boolean flag to represent negative numbers
    int len;  // actual number of elements of array that are used;  number is stored in arr[0..len-1]

    public Num(String s) {
        this.isNegative = s.charAt(0) == '-';
        int strLen = s.length();
        this.len = 0;
        this.len = this.isNegative ? strLen - 1 : strLen;
        this.arr = new long[len];
        for(int i = strLen - 1; i >= 0; i--)
            if(s.charAt(i) != '-')
                arr[strLen-1-i] = Long.parseLong(s.substring(i, i+1));
        /*for(int i=0; i < strLen; i++)
            System.out.print(arr[i] + " ");*/
    }

    public Num(long x) {
        this.isNegative = x < 0;
        int index = 0;
        this.len = 0;
        while((long) Math.pow(10, this.len) < x)
            this.len = this.len + 1;
        this.arr = new long[this.len];
        while(x > 0) {
            this.arr[index++] = x % base;
            x = x / base;
        }
        /*for(int i=0; i < arr.length; i++)
            System.out.print(arr[i] + " ");*/
    }

    public static Num add(Num a, Num b) {
        int aIndex = 0, bIndex = 0;
        int aLength = a.getLen(), bLength = b.getLen();
        int minLength=aLength < bLength ? aLength  : bLength ;
        int maxLength=aLength >= bLength ? aLength  : bLength;
        StringBuilder result=new StringBuilder();
        long carry = 0;
        int j=0;
        long []aArr=a.getArr();
        long []bArr=b.getArr();
        while(j<minLength) {
            result.append((carry + aArr[j] + bArr[j]) % a.base());
            carry=result.charAt(j)/a.base();
            //resultArray[j] = (carry + a.arr[j] + b.arr[j]) % a.base;
            //carry = resultArray[j] / a.base;
            j++;
        }
        if(aLength>bLength){
            while(j<maxLength) {
                result.append((aArr[j]+carry)%a.base());
                //resultArray[j]=(a.arr[j]+carry)%a.base;
                carry=result.charAt(j)/a.base();
                j++;
            }
        }
        if(aLength<bLength){
            while(j<maxLength) {
                result.append((bArr[j]+carry)%a.base());
                //resultArray[j]=(b.arr[j]+carry)%a.base;
                carry=result.charAt(j)/a.base();
                j++;
            }
        }
        if(carry>0) {
            result.append(carry);
            //resultArray[j]=carry;
        }
        Num resultNum=new Num(result.reverse().toString());
        return resultNum;
    }

    public static Num subtract(Num a, Num b) {
        StringBuilder result=new StringBuilder();
        if(a.getLen()==b.getLen() && b.compareTo(a)>0){
            Num res=subtract(b,a);
            res.makeNegative();
            return res;
        }
        long []aArr=a.getArr();
        long []bArr=b.getArr();
        boolean borrow=false;
        for(int i=0;i<b.getLen();i++) {
            if(borrow){
                if(aArr[i]-1<bArr[i]){
                    result.append(a.base()+aArr[i]-1-bArr[i]);
                }
                else{
                    result.append(aArr[i]-1-bArr[i]);
                    borrow=false;
                }
            }
            else if(aArr[i]<bArr[i]){
                result.append(a.base()+aArr[i]-bArr[i]);
                borrow=true;
            }
            else {
                result.append(aArr[i]-bArr[i]);
            }
        }
        if(borrow) {
            //handling cases of adding extra unnecessary leading zero(eg 100-9)
            if (a.getLen() > b.getLen() + 1 || aArr[b.getLen()] > 1) {
                if (aArr[b.getLen()] != 0) {
                    result.append(aArr[b.getLen()] - 1);
                } else {
                    result.append(a.base());
                    borrow = true;
                }
                for (int i = b.getLen() + 1; i < a.getLen(); i++) {
                    if (borrow) {
                        if (aArr[i] != 0) {
                            result.append(aArr[i] - 1);
                            borrow = false;
                        } else {
                            result.append(a.base());
                        }
                    } else {
                        result.append(aArr[i]);
                    }
                }
            }
        }
        return new Num(result.reverse().toString());
    }

    public static Num product(Num a, Num b) {
        long[] result=new long[a.getLen()+b.getLen()];
        int counter=0,index=0;
        long temp;
        long resultValue;
        long carry=0,addCarry=0;
        for(long x:b.getArr()){
            index=counter;
            carry=0;
            addCarry=0;
            for(long y:a.getArr()){
                temp=x*y+carry;//if the product exceeds the base? should implement addition with base as well as well?
                //temp=convertNumberBase(temp,a.base());//check if required
                resultValue=result[index];
                result[index]=(result[index]+(temp%a.base())+addCarry)%a.base();//does this addition causes overflow
                carry=temp/a.base();
                addCarry=(resultValue+(temp%a.base())+addCarry)/a.base();
                index++;
            }
            if(carry>0){
                result[index++]=(carry+addCarry)%a.base();
                addCarry=(carry+addCarry)/a.base();
            }
            if(addCarry>0){
                result[index]=addCarry;
            }
            counter++;
        }
        StringBuilder s=new StringBuilder();
        for(long x:result){
            s.append(x);
        }
        return new Num(s.toString());
    }

    // Use divide and conquer
    public static Num power(Num a, long n) {
        return null;
    }

    // Use binary search to calculate a/b
    public static Num divide(Num a, Num b) {
        return null;
    }

    // return a%b
    public static Num mod(Num a, Num b) {
        Num inter=divide(a,b);
        Num prod=product(inter,b);
        Num result=subtract(a,prod);
        return result;
    }

    // Use binary search
    public static Num squareRoot(Num a) {
        return null;
    }


    // Utility functions
    // compare "this" to "other": return +1 if this is greater, 0 if equal, -1 otherwise
    public int compareTo(Num other) {
        int result = 0;
        if(other == null && other.getLen() == 0)
            result = this.isNegative ? -1 : 1;
        if(this.isNegative && !other.isNegative)
            result = -1;
        else if(!this.isNegative && other.isNegative)
            result = 1;
        else if(this.getLen() < other.getLen())
            result = this.isNegative ? 1 : -1;
        else if(this.getLen() > other.getLen())
            result = this.isNegative ? -1 : 1;
        else {
            int i;
            for(i = this.getLen()-1; i >= 0; i--) {
                if(this.arr[i] < other.arr[i])
                    result = this.isNegative ? 1 : -1;
                else if(this.arr[i] > other.arr[i])
                    result = this.isNegative ? -1 : 1;
                else if(this.arr[i] == other.arr[i] && i == 0)
                    result = 0;
                else
                    continue;
            }
        }
        return result;
    }

    // Output using the format "base: elements of list ..."
    // For example, if base=100, and the number stored corresponds to 10965,
    // then the output is "100: 65 9 1"
    public void printList() {

    }

    // Return number to a string in base 10
    public String toString() {
        return null;
    }

    public long base() { return base; }

    // Return number equal to "this" number, in base=newBase
    public Num convertBase(int newBase) {
        return null;
    }

    public long[] getArr(){
        return arr;
    }

    // Divide by 2, for using in binary search
    /*Basically the code doesn't work yet but the idea is to divide each term of the array by 2
     * If a[i] < 2 then there will be a carry such that (carry*base)+a[i+1] is divided in the next cycle
     * The array is then returned in the Num type
     * i forgot to consider input array in reverse order*/
    public Num by2() {
    	StringBuilder result = new StringBuilder();
    	int i = 0;
    	long carry = 0;
    	while(i < arr.length) {
    		if(this.arr[i] > 2) {
    			result.append(this.arr[i]/2);
    			carry = this.arr[i] % 2;
    		}
    		else {
    			result.append((carry*base+arr[i])/2);
    		}
    	}
    	System.out.println(result);
        return new Num(result.reverse().toString());
    }

    // Evaluate an expression in postfix and return resulting number
    // Each string is one of: "*", "+", "-", "/", "%", "^", "0", or
    // a number: [1-9][0-9]*.  There is no unary minus operator.
    public static Num evaluatePostfix(String[] expr) {
        return null;
    }

    // Evaluate an expression in infix and return resulting number
    // Each string is one of: "*", "+", "-", "/", "%", "^", "(", ")", "0", or
    // a number: [1-9][0-9]*.  There is no unary minus operator.
    public static Num evaluateInfix(String[] expr) {
        return null;
    }

    public void makeNegative(){
        this.isNegative = true;
    }

    public int getLen(){
        return this.len;
    }


    public static void main(String[] args) {
        Num x = new Num(7);
        Num y = new Num("67");
 //       System.out.println(x.by2());
        Num f=product(x,y);
        for(long g:f.getArr()){
            System.out.println(g);
        }
//        System.out.println(y.by2());
        System.out.println(new Num("1").compareTo(new Num("-1")));
        Num z = Num.add(x, y);
        System.out.println(z);
        Num a = Num.power(x, 8);
        System.out.println(a);
        if(z != null) z.printList();
    }
}
