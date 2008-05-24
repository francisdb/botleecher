/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.somatik.botleecher.tools;

import eu.somatik.botleecher.*;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author fdb
 */
public class DualOutputStream extends FilterOutputStream{

    private final PrintStream oldStream;
    private final TextWriter writer;

    public DualOutputStream(PrintStream oldStream, TextWriter textWriter) {
        super(new ByteArrayOutputStream());
        this.oldStream = oldStream;
        this.writer = textWriter ;   
    }

    @Override
    public void write(byte[] b) throws IOException {
        String aString = new String(b);
        writer.writeText(aString);
        oldStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        String aString = new String(b, off, len);
        writer.writeText(aString);
        oldStream.write(b, off, len);
    }
}
