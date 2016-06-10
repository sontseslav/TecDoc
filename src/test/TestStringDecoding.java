/*
 * 
 */
package test;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author user
 */
public class TestStringDecoding {
    public static Map<Integer,Character> Dict = new Hashtable<>();
    public static byte[] array = {0x4D,0x0,0x5A,0x0,0x20,0x31,0x39,0x20,0x40,0x04,0x39,0x04};
    
    public static void main(String[] args) throws UnsupportedEncodingException {
        byteArayProcessing(array);
        String s = new String(array,"Windows-1251");
        s = s.replace("\u0000", "");
        System.out.println(s);
    }
    
    private static void byteArayProcessing(byte[] arr){
        Dict.put(0x40, 'р');
        Dict.put(0x39, 'й');
        
    }
}
