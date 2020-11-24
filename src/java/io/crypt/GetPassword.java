/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.crypt;


/**
 *
 * @author SuMario
 */
public class GetPassword {
    
     /** 
     * Minimum length for a decent password 
     */  
    public static final int MIN_LENGTH = 8;
    public static final int MIN_STRONG_LENGTH = 12;
    
    private static PasswordTyp pwtyp;
   
    /** 
     * The random number generator. 
     */  
    protected static java.util.Random r = new java.util.Random();  
   
    /* Set of characters that is valid. Must be printable, memorable, 
     * and "won't break HTML" (i.e., not '<', '>', '&', '=', ...). 
     * or break shell commands (i.e., not '<', '>', '$', '!', ...). 
     * I, L and O are good to leave out, as are numeric zero and one. 
     */  
    protected static char[] goodChar1 = {  
        // Comment out next two lines to make upper-case-only, then  
        // use String toUpper() on the user's input before validating.  
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n',  
        'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',  
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N',  
        'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',  
        '2', '3', '4', '5', '6', '7', '8', '9',  
        '+', '-', '@',  
    };  

    protected static final char[] countChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    protected static final char[] smallChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    protected static final char[] bigChar   = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    protected static final char[] extraChar = { '+', '-', '@', '!', '$', '%', '/', '#', '*', ',', ';', ':', '_' };

    //protected static final char[] goodChar  = { (char[]) countChar, smallChar, bigChar, extraChar };
    
    /* Generate a Password object with a random password. */  
    public static String getNext() { return getNext(MIN_LENGTH); }  
   
    /* Generate a Password object with a random password. */  
    public static String getNext(int length) {  
        if (length < 1) {  
            throw new IllegalArgumentException( "Ridiculous password length " + length);  
        }  
        StringBuilder sb = new StringBuilder();  
        for (int i = 0; i < length; i++) { 
            char[] goodChar = { countChar[r.nextInt(countChar.length)], 
                                smallChar[r.nextInt(smallChar.length)], 
                                  bigChar[r.nextInt(  bigChar.length)], 
                                extraChar[r.nextInt(extraChar.length)]  };
            sb.append(goodChar[r.nextInt(goodChar.length)] );  
        }  
        return sb.toString();  
    } 
    
    private static String getNext(int length, PasswordTyp typ ) {
        
        if      ( typ.equals(PasswordTyp.MEDIUM) && length<MIN_LENGTH        ) { length=MIN_LENGTH; }
        else if ( typ.equals(PasswordTyp.STRONG) && length<MIN_STRONG_LENGTH ) { length=MIN_STRONG_LENGTH; }
        
        StringBuilder sb = new StringBuilder();
        char[] goodChar;
        boolean found = false;
        for (int i = 0; i < length; i++) { 
                if (        typ.equals(pwtyp.EASY)   ) { 
                       char[] ch = { smallChar[r.nextInt(smallChar.length)], 
                                     bigChar[r.nextInt(  bigChar.length)] };
                       goodChar=ch;
                        
                } else if ( typ.equals(pwtyp.MEDIUM) ) { 
                        char[] ch = {   countChar[r.nextInt(countChar.length)], 
                                        smallChar[r.nextInt(smallChar.length)], 
                                        bigChar[r.nextInt(  bigChar.length)]}; 
                        goodChar=ch;                
                } else {  
                     if ( found || i+2 < length ) {  
                       char[] ch = { countChar[r.nextInt(countChar.length)], 
                                    smallChar[r.nextInt(smallChar.length)], 
                                      bigChar[r.nextInt(  bigChar.length)], 
                                    extraChar[r.nextInt(extraChar.length)]  };
                       
                       goodChar=ch;
                     } else { 
                       char[] ch = { extraChar[r.nextInt(extraChar.length)] };
                       goodChar=ch;
                     }  
                }
                char c = goodChar[r.nextInt(goodChar.length)];
                
                if ( !found && typ.equals(pwtyp.STRONG) ) {
                    for ( int j=0 ; j<extraChar.length; j++ ) {
                        if ( extraChar[j] == c ) { found=true; j=extraChar.length; }
                    }
                    
                }
                sb.append(c);
        }       
        
        return sb.toString();
    } 
    
    public static String getStringPassword(int len) {  return getNext( (len<MIN_STRONG_LENGTH)?MIN_STRONG_LENGTH:len, PasswordTyp.STRONG);  }
    public static String getStrongPassword() { return getStringPassword(MIN_STRONG_LENGTH); }
    public static String getMediumPassword() { return getNext(MIN_LENGTH,PasswordTyp.MEDIUM); }
    public static String getEasyPassword()   { return getNext(6,PasswordTyp.EASY);}
    public static String getPassword(int length, PasswordTyp typ) { return getNext(length, typ); }
    
    public static String getPassword(PasswordTyp typ) { return getNext(MIN_LENGTH, typ); }
    
    public static void setPasswordTyp (PasswordTyp typ) { pwtyp = typ; }
    
    static {
        pwtyp = PasswordTyp.STRONG;
    }

    public static void main(String[] args){
	for(int i=0; i<GetPassword.MIN_LENGTH;i++) 
		System.out.println(i+1+". password : "+getPassword( PasswordTyp.STRONG )+" :" 
                                                  +" "+getPassword( PasswordTyp.MEDIUM) +" :"
                                                  +" "+getPassword( PasswordTyp.EASY )+" :"
                                                  +" "+getPassword(   3, PasswordTyp.STRONG )+" :"
                                                  +" "+getPassword(  64, PasswordTyp.STRONG )+" :"
                                                  
                                                
                        );  
    }

    
    
}
