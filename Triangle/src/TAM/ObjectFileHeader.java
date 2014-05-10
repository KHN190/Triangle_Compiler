package TAM;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 *  This class represents the header of a TAM object file, which
 *
 */
public class ObjectFileHeader {

    // Magic number, so that on reading a file, we can make sure that
    // we're reading the right kind of file
    public int magicNumber;

    // Triangle source file name from which the object was created
    public String sourceFilename;

    // How many instructions are there in the data segment?
    public int instructionCount;

    // How many line numbers are there in the line# segment (note: in our
    // current design, this will *always* be equal to instructionCount)
    public int lineNumberCount;

    // How many entries are there in the symbol table? (This definitely may
    // be different than the instructionCount)
    public int symbolCount;

    /*
     * Constructor for making a header after we've parsed the source code
     */
    public ObjectFileHeader(String fileName, int instructions, int symbols) {
        magicNumber = triangleObjMagic;
        sourceFilename = fileName;
        instructionCount = lineNumberCount = instructions;
        symbolCount = symbols;
    }

    /*
     * Constructor for reading a header from an object file
     */
    public ObjectFileHeader(DataInputStream input) throws IOException {
        // Read the magic number and make sure it's right
        magicNumber = input.readInt ();
        if (magicNumber != triangleObjMagic) {
            throw new IOException("Not a triangle object file");
        }

        // Read the source filename, which is a fixed number of characters
        char[] characters = new char[stringFieldLength];
        for (int i = 0; i < stringFieldLength; i++) {
            char c = input.readChar();
            characters[i] = c;
        }
        sourceFilename = new String(characters);

        // This will pull off the extra spaces added when it was written
        sourceFilename = sourceFilename.trim();

        // Read the count values for the other segments
        instructionCount = input.readInt();
        lineNumberCount = input.readInt();
        symbolCount = input.readInt();
    }

    /*
     * Write this header to the given output stream
     */
    public void write(DataOutputStream output) throws IOException {
        // Simple, just write every field in order
        output.writeInt (magicNumber);
        // We use String.format to guarantee that the filename is the
        // correct length - this will pad it with spaces until it's
        // exactly stringFieldLenth characters long
        output.writeChars (String.format("%" + stringFieldLength + "." +
                stringFieldLength + "s", sourceFilename));
        output.writeInt(instructionCount);
        output.writeInt (lineNumberCount);
        output.writeInt (symbolCount);
      }

    /*
     * Constants - the standard magic number, and the length of the filename
     * field
     */
    public static final Integer triangleObjMagic = 17232;
    public static final Integer stringFieldLength = 64;
}