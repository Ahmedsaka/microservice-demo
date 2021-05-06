package jasypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;

public class TestJasypt {

    public static void main(String[] args) {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        standardPBEStringEncryptor.setPassword("Alpha.02$");
        standardPBEStringEncryptor.setIvGenerator(new RandomIvGenerator());
        standardPBEStringEncryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");

        String result = standardPBEStringEncryptor.encrypt("test");
        System.out.println(result);
        System.out.println(standardPBEStringEncryptor.decrypt(result));
    }
}
