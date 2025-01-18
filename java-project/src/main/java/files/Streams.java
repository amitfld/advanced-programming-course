package files;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Streams {
    /**
     * Read from an InputStream until a quote character (") is found, then read
     * until another quote character is found and return the bytes in between the two quotes.
     * If no quote character was found return null, if only one, return the bytes from the quote to the end of the stream.
     *
     * @param in
     * @return A list containing the bytes between the first occurrence of a quote character and the second.
     */
    public static List<Byte> getQuoted(InputStream in) throws IOException {
        List<Byte> byteList = new ArrayList<>();
        int data;
        // Read bytes until there aren't any
        while ((data = in.read()) != -1){
            // If found a quote character
            if ((char) data == '"'){
                // Read bytes until there aren't any and add them to the bytes list
                while ((data = in.read()) != -1){
                    // If encountered a quote character, break from the loop
                    if ((char) data == '"'){
                        break;
                    }
                    byteList.add((byte)data);
                }
                // Return the bytes list
                return byteList;
            }
        }
        return null;
    }


    /**
     * Read from the input until a specific string is read, return the string read up to (not including) the endMark.
     *
     * @param in      the Reader to read from
     * @param endMark the string indicating to stop reading.
     * @return The string read up to (not including) the endMark (if the endMark is not found, return up to the end of the stream).
     */
    public static String readUntil(Reader in, String endMark) throws IOException {
        StringBuilder sb = new StringBuilder();
        int currChar;
        int endMarkLen = endMark.length();
        int bufferIndex = 0;
        char[] buffer = new char[endMarkLen];

        while ((currChar = in.read()) != -1){
            sb.append((char) currChar);
            // If buffer is not full, update the buffer array with the current character
            if (bufferIndex < endMarkLen){
                buffer[bufferIndex] = (char) currChar;
                bufferIndex ++;
            } else {
                // else, check if the buffer equals to the endMark
                boolean matches = true;
                for (int i = 0; i < endMarkLen; i++){
                    if (buffer[i] != endMark.charAt(i)){
                        matches = false;
                        break;
                    }
                }
                // If so , return the required string until the endMark (not included)
                if (matches) {
                    return sb.substring(0, sb.length() - endMarkLen - 1);
                }
                // If not - update the buffer array
                for (int i = 0; i < endMarkLen - 1 ; i++){
                    buffer[i] = buffer[i + 1];
                }
                // Add the current char to the end of the buffer array
                buffer[endMarkLen - 1] = (char) currChar;
            }
        }
        return sb.toString();
    }


    /**
     * Copy bytes from input to output, ignoring all occurrences of badByte.
     *
     * @param in
     * @param out
     * @param badByte
     */
    public static void filterOut(InputStream in, OutputStream out, byte badByte) throws IOException {
        int data;
        int i=0;
        while ((data = in.read()) != -1) {
            if ((byte) data != badByte){
                out.write(data);
            }
        }
    }

    /**
     * Read a 40-bit (unsigned) integer from the stream and return it. The number is represented as five bytes,
     * with the most-significant byte first.
     * If the stream ends before 5 bytes are read, return -1.
     *
     * @param in
     * @return the number read from the stream
     */
    public static long readNumber(InputStream in) throws IOException {
        byte[] bArray = new byte[5];
        int bytesRead = in.read(bArray);
        long number = 0;
        // return -1 if the stream ends before 5 bytes are read.
        if (bytesRead < 5){
            return -1;
        }
        // Calculate the number
        for (int i = 0; i < 5; i++){
            // shift the current number 8 bits to the left and push the new byte to the number but unsigned
            number = (number << 8) | (bArray[i] & 0xFF);
        }
        return number;
    }
}
