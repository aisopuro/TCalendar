package tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import org.junit.*;


public class IOTest {
    @Test
    public void testReading() {
    try {
        Reader reader = new FileReader("test.ics");
        BufferedReader buffReader = new BufferedReader(reader);
        String line = buffReader.readLine();
        
        System.out.println(line);
        line = buffReader.readLine();
        System.out.println(line);
    } catch (FileNotFoundException notFound) {
        System.out.println("Not found");
    } catch (IOException ioException) {
        System.out.println(ioException.getMessage());
    }
}

}
