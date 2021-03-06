package jozwiak.marcin.mycrypto.fragments;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Marcin on 2015-06-03.
 */
public class AES256_CBC {
    private byte[] getKeyBytes(final byte[] key) throws Exception {
        byte[] keyBytes = new byte[16];
        System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, keyBytes.length));
        return keyBytes;
    }

    public Cipher getCipherEncrypt(final byte[] key) throws Exception {
        byte[] keyBytes = getKeyBytes(key);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher;
    }

    public Cipher getCipherDecrypt(byte[] key) throws Exception {
        byte[] keyBytes = getKeyBytes(key);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher;
    }

    public void encrypt(File inputFile, File outputFile, byte[] key) throws Exception {
        Cipher cipher = getCipherEncrypt(key);
        FileOutputStream fos = null;
        CipherOutputStream cos = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(inputFile);
            fos = new FileOutputStream(outputFile);
            cos = new CipherOutputStream(fos, cipher);
            fos = null;
            byte[] data = new byte[1024];
            int read = fis.read(data);
            while (read != -1) {
                cos.write(data, 0, read);
                read = fis.read(data);
                System.out.println(new String(data, "UTF-8").trim());
            }
            cos.flush();
        } finally {
            if (cos != null) {
                cos.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    public void decrypt(File inputFile, File outputFile, byte[] key) throws Exception {
        Cipher cipher = getCipherDecrypt(key);
        FileOutputStream fos = null;
        CipherInputStream cis = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(inputFile);
            cis = new CipherInputStream(fis, cipher);
            fos = new FileOutputStream(outputFile);
            byte[] data = new byte[1024];
            int read = cis.read(data);
            while (read != -1) {
                fos.write(data, 0, read);
                read = cis.read(data);
                System.out.println(new String(data, "UTF-8").trim());
            }
        } finally {
            fos.close();
            cis.close();
            fis.close();
        }
    }

    public static void decryptOnServer(File inputFile) throws Exception {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;

//        SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SocketFactory ssf = SocketFactory.getDefault();
        Socket s = ssf.createSocket("192.168.1.6", 8080);

        byte [] mybytearray  = new byte [(int)inputFile.length()];
        fis = new FileInputStream(inputFile);
        bis = new BufferedInputStream(fis);
        bis.read(mybytearray,0,mybytearray.length);
        os = s.getOutputStream();
        os.write(mybytearray, 0, mybytearray.length);
        os.flush();
        bis.close();
        os.close();
        s.close();

    }
}
