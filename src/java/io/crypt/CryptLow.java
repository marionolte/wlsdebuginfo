/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.crypt;

import general.Version;
import static general.Version.getVersion;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.Character.digit;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author SuMario
 */
class CryptLow extends Version {
    final private UUID uuid;
    final private String pass;
    
    CryptLow(UUID uuid) {
        this.uuid=uuid;
        pass=getPass(uuid.toString());
        init();
    }

    private CryptLow(){   this( UUID.fromString("5fa4a40a-53b4-4f7a-b132-61bd19b79a8e"));  }
    
    private String getPass(String info){
        String ret="blank"+getVersion();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(  (info.getBytes()));
            byte[] mdbytes = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
              sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString(); //(sb.length()<16)?sb.toString():sb.substring(0, 15);
        } catch(Exception e){}
        return ret;
    }
    
    private SecretKeySpec key ;
    private IvParameterSpec iv ;
    
    private Cipher cipher=null;
    private Field field=null;
    private void init() { 
        final String func=getFunc("init()");
        try {
            field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
            field.setAccessible(true);
            field.set(null, java.lang.Boolean.FALSE);
        } catch (Exception ex) {
            printf(func,1,"strength isRestricted set error : "+ex.getMessage(),ex);
        }
        try { 
                cipher= Cipher.getInstance("DES/CBC/PKCS5Padding");
            }
            catch(NoSuchAlgorithmException nsa) { cipher=null;}
            catch(NoSuchPaddingException   nse) { cipher=null;}
        
        printf(func,2,"cipher are:"+cipher);
        
        
        DESKeySpec dkey;
        try {
            dkey = new DESKeySpec(this.updateLength(this.uuid.toString(),32).getBytes());
             key = new SecretKeySpec(dkey.getKey(), "DES");
              iv = new IvParameterSpec(this.pass.getBytes("UTF-8"));
        } catch (InvalidKeyException ex) {
           log("init()",1,"cipher key init error :"+ex.getMessage(),ex); 
        } catch (UnsupportedEncodingException ue) {
           log("init()",1,"cipher key encode init error :"+ue.getMessage(),ue); 
        }
         
    }
    
    
    public String getCrypted(String txt) {
        try {
          byte[] b=this.encrypt(txt);
          //byte[] b=encrypt(txt,uuid.toString(),pass);
          //byte[] b=encrypt(txt,Ukey,pass);
          if ( b==null ) { return ""; }
          return new String(Base64.encode(b));
        } catch(Exception e) {
          return new String(Base64.encode(txt.getBytes())); 
        } 
    }
    
    public String getUnCrypted(String info) {
        final String func="getUnCrypted(String info)";
        byte[] b=Base64.decodeBase64(info);
        try { 
            return this.decrypt(b);
        } catch (Exception e) {
            return new String(b);
        }    
        
    }
    
    public byte[] getUnCryptedByte(String info) {
        final String func="getUnCrypted(String info)";
        byte[] b=Base64.decodeBase64(info);
        try { 
            return this.decryptByte(b);
        } catch (Exception e) {
            return b;
        }    
        
    }
    
    //private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
    private  final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };

    
    private byte[] encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key =  keyFactory.generateSecret(new PBEKeySpec(this.pass.toCharArray())); //keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return enBase64(pbeCipher.doFinal(property.getBytes("UTF-8")));
    }

    /*private static String base64Encode(byte[] bytes) {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Encoder().encode(bytes);
    }*/

    private String decrypt(byte[] b) throws GeneralSecurityException, IOException {
        return decrypt(new String(b));
    }
    private String decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(this.pass.toCharArray())); // keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(deBase64(property)), "UTF-8");
    }
    
    private byte[] decryptByte(byte[] b) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(this.pass.toCharArray())); // keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return pbeCipher.doFinal(b);
    }

/*    private static byte[] base64Decode(String property) throws IOException {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Decoder().decodeBuffer(property);
    }*/

    
/*    sample 

// wrap key data in Key/IV specs to pass to cipher
SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
// create the cipher with the algorithm you choose
// see javadoc for Cipher class for more info, e.g.
Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

Encryption would go like this:

cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
byte[] encrypted= new byte[cipher.getOutputSize(input.length)];
int enc_len = cipher.update(input, 0, input.length, encrypted, 0);
enc_len += cipher.doFinal(encrypted, enc_len);

And decryption like this:

cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
byte[] decrypted = new byte[cipher.getOutputSize(enc_len)];
int dec_len = cipher.update(encrypted, 0, enc_len, decrypted, 0);
dec_len += cipher.doFinal(decrypted, dec_len); 

*/
    
    
    private byte[] enBase64(byte[] b   ) { return Base64.encodeBase64(b); }
    private byte[] encrypt(String plainText, String enkey, String pw)  {
          final String func="encrypt(String plainText, String encryptionKey, String pass)";
          
	  try {	
                log(func,1,"enKey:"+enkey.length()+"(64)   pw:"+pw.length()+"(16)");
                // key = new SecretKeySpec(enkey.getBytes(), "DES");  //new SecretKeySpec(getBytes(updateLength(enkey,64)), "AES");
                //  iv = new IvParameterSpec(pw.getBytes()); // IvParameterSpec(getBytes(updateLength(pw, 16)));
                //log(func,1, key.
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                // AES stye
                //log(func, 1, "encrption text length="+plainText.length()+"  mod="+(plainText.length()%16));
		//return cipher.doFinal(updateLength(plainText,64).getBytes("UTF-8"));
                byte[] encrypted= new byte[cipher.getOutputSize(plainText.length())];
                int enc_len = cipher.update(plainText.getBytes(), 0, plainText.length(), encrypted, 0);
                    enc_len += cipher.doFinal(encrypted, enc_len);
                return  encrypted;   
          } catch (Exception e) {
              log(func, 1, "encrption error limited strength - "+e.getMessage(),e);
              
          }  
          
          return plainText.getBytes();
          //return null;
    }
    
    private String updateLength(String str, int len){
        final String func="updateLength(String str, int len)";
        int size=(str.length() % len);
        if ( size == 0 ) { return str; }
        if ( str.length() > len ) { return str.substring(0, len-1); }
        StringBuilder sw=new StringBuilder(); sw.append(str);
        log(func, 3, "size:"+size+" base are :"+len);
        int c=0;
        if (size != 0 ) {
            while( (size+c) < len ) {
                sw.append("\0"); c++;
            }
        }    
        log(func, 3, "new size "+sw.length()+" test:"+(sw.length()%len)+" have added "+c+" null" );
        return sw.toString();
    }
    
    private static byte[] getBytes(String input) {
        int length = input.length();
        byte[] output = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            output[i / 2] = (byte) ((digit(input.charAt(i), 16) << 4) | digit(input.charAt(i+1), 16));
        }
        return output;
    }
    
    private byte[] deBase64(String text) { return  Base64.decodeBase64(text); }
    private String decrypt(byte[] cipherText, String encryptionKey, String pass) {
         final String func="decrypt(byte[] cipherText, String encryptionKey, String pass)";
         SecretKeySpec key;IvParameterSpec iv;
         try {
              
		 key = new SecretKeySpec(getBytes(updateLength(encryptionKey,64)), "DES");
                  iv = new IvParameterSpec(getBytes(updateLength(pass, 16)));
		cipher.init(Cipher.DECRYPT_MODE, key,iv);
		return new String(cipher.doFinal(cipherText),"UTF-8");
         } catch (Exception e) {
             log(func, 1, "encrption error (with limited size)- "+e.getMessage(),e);
         }
         
         return null;       
    } 
    
    public static void main(String[] args) throws Exception {
         CryptLow c = new CryptLow();
         for(String s: args) {
            if ( s.matches("\\-d") ){ c.debug++; } else { 
              String en = c.getCrypted(s);
              String de = c.getUnCrypted(en);
              String ma = ( s.equals(de) )?"YES":"NO";
              c.log("main(String[] args)",0,"TESTING:"+s+":\nENCODED :"+en+":\nDECODED :"+de+":\nMATCHING:"+ma+"\n");
            }  
         }
    }
    
    
    private void log(String func, int level, String msg) {
        println(level, func+" - "+msg);
    }
    private void log(String func, int level, String msg, Exception e) {
         log(func,level,msg);
         log(func,level, "Exception trown with "+e.getMessage());
         e.printStackTrace();
    }
}
