import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class Main {

    private static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private static void execute(MD5Provider provider, String name) {
        try {
            byte[] hash = provider.getMD5(Paths.get(name));
            System.out.println(bytesToHex(hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something went wrong during input/output.");
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Pass name of the file or directory as an argument.");
            return;
        }
        if (args.length > 1) {
            System.out.println("Too many arguments.");
            return;
        }
        MD5Provider providerSingle = new MD5SingleThreaded();
        execute(providerSingle, args[0]);
        MD5Provider providerFJP = new MD5ForkJoin();
        execute(providerFJP, args[0]);
    }
}
