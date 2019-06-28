package Eliza;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/** Command-line demo */
@SuppressWarnings("nls")
public class ElizaDemo
{
    public static void main(String args[]) throws Exception
    {
        ElizaMain eliza = new ElizaMain();
        eliza.readScript(ElizaMain.class.getResourceAsStream("/Eliza/script"));

        final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        String s = "Hello";
        while (! eliza.finished)
        {
            System.out.println(">> " + s);
            String reply = eliza.processInput(s);
            System.out.println(reply);
            System.out.print(">> ");
            s = input.readLine();
            if (s == null)
                break;
        }
    }
}
