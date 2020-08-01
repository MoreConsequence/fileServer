import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author lhy
 **/
public class FileClient {

    public static void main(String[] args) throws IOException {
        FileOutputStream fos = new FileOutputStream("file.txt");
        FileInputStream fis = new FileInputStream("pom.xml");
        fis.getChannel().lock();
        fos.getChannel().lock();
        fos.getChannel().lock();
        System.out.println("client lock");
    }
}
