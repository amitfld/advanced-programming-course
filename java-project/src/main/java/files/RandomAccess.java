package files;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccess {
    /**
     * Treat the file as an array of (unsigned) 8-bit values and sort them
     * in-place using a bubble-sort algorithm.
     * You may not read the whole file into memory!
     *
     * @param file
     */
    public static void sortBytes(RandomAccessFile file) throws IOException {
        long fileSize = file.length();
        if (fileSize <= 1) {
            // No need to sort if the file has 0 or 1 byte
            return;
        }
        for (long i = 0; i < fileSize - 1; i++) {
            for (long j = 0; j < fileSize - i - 1; j++) {
                // Go to location j in the file and read two unsigned bytes
                file.seek(j);
                int firstByte = file.readUnsignedByte();
                int secondByte = file.readUnsignedByte();
                // Swap the bytes if the first byte is bigger than the second
                if (firstByte > secondByte) {
                    file.seek(j);
                    file.writeByte(secondByte);
                    file.writeByte(firstByte);
                }
            }
        }
    }

    /**
     * Treat the file as an array of unsigned 24-bit values (stored MSB first) and sort
     * them in-place using a bubble-sort algorithm.
     * You may not read the whole file into memory!
     *
     * @param file
     * @throws IOException
     */
    public static void sortTriBytes(RandomAccessFile file) throws IOException {
        int byte1, byte2, byte3;
        long fileSize = file.length();
        if (fileSize <= 3) {
            // No need to sort if the file has 0 or 1 byte
            return;
        }

        // From instructions: It is ok to assume that the number of bytes in the file is divisible by 3.
        long numTriBytes = fileSize / 3;

        for (long i = 0; i < numTriBytes - 1; i++) {
            for (long j = 0; j < numTriBytes - i - 1; j++) {
                // Go to location j*3 in the file and read two unsigned bytes
                long pos = j * 3;
                file.seek(pos);

                byte1 = file.readUnsignedByte(); // Most significant byte
                byte2 = file.readUnsignedByte(); // Middle byte
                byte3 = file.readUnsignedByte(); // Least significant byte

                // Combine bytes into a single unsigned 24-bit integer
                int firstTriByte =  (byte1 << 16) | (byte2 << 8) | byte3;
                byte1 = file.readUnsignedByte();
                byte2 = file.readUnsignedByte();
                byte3 = file.readUnsignedByte();

                int secondTriByte =  (byte1 << 16) | (byte2 << 8) | byte3;

                // Swap the bytes if the first byte is bigger than the second
                if (firstTriByte > secondTriByte) {
                    file.seek(pos);

                    byte1 = (secondTriByte >> 16) & 0xFF; // Most significant byte
                    byte2 = (secondTriByte >> 8) & 0xFF;  // Middle byte
                    byte3 = secondTriByte & 0xFF;         // Least significant byte
                    file.writeByte(byte1);
                    file.writeByte(byte2);
                    file.writeByte(byte3);
                    byte1 = (firstTriByte >> 16) & 0xFF;
                    byte2 = (firstTriByte >> 8) & 0xFF;
                    byte3 = firstTriByte & 0xFF;
                    file.writeByte(byte1);
                    file.writeByte(byte2);
                    file.writeByte(byte3);
                }
            }
        }
    }
}
