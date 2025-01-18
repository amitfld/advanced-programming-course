package files;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class StreamsTest {
    public final static int NUM_EXTENDED = 32; // number of extended tests
    public final static int EXPECTED_QUOTES = 2; // number of expected quotes
    public final static int MAX_LEN = 200; // maximum array (stream) length
    public final static int MIN_LEN = 0; // maximum array (stream) length

    Random rnd;

    @Before
    public void setup() {
        rnd = new Random(9); // Fixed seed so tests will be repeatable.
    }

    @Test
    public void testGetQuoted() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(
                "this is irrelevant \"return this substring\" this is irrelevant"
                        .getBytes());
        byte[] rawExpected = "return this substring".getBytes();
        List<Byte> expected = new ArrayList<Byte>(rawExpected.length);
        for (byte b : rawExpected)
            expected.add(b);

        assertEquals(expected, Streams.getQuoted(in));
    }

    /**
     * Read from an InputStream until a quote character (") is found, then read
     * until another quote character is found and return the bytes in between
     * the two quotes. If no quote character was found return null, if only one,
     * return the bytes from the quote to the end of the stream.
     *
     * @return A list containing the bytes between the first occurrence of a
     * quote character and the second.
     */
    @Test
    public void testGetQuotedExtended() throws IOException {
        Byte[][] byteResults = {null,
                {-115, -96, -103, -20, -60, 103, 12, 26, 23, 45},
                {-5, -91, -1, -49, 86, 112, -59, 23, -53, -9, 11, -73, 51},
                {-124, 10, -12, 49, 76, -103, 19, 95, 74, 91, 38, 100, 13, 71, -86, 62, -115, -16, -89, -1, -52, -3, 13, -62, -52},
                {-59, -122, -39, 16, -37, 99, -126, -22, 58, 103, 15, 116, -27, -75},
                {65, 109, -88, -117, 76, -125, -46, 14, -121, 51, 1, -5, 84, 121, -113, -1, 20, 99, 77, -37, -77, -54, -12, -34, 93, -69, 46, -61, -64, 104, 60, 73, -122, -121, -105, 32, 15, -63, 18, -114, 39, -93, -105},
                {-8, -58, -102, -112, -48, 31, -118, 57, -3, 69, -1, -80, 8, -22, 101, 37, -101, -45, -72, 32, 51, -116, -126, -72, -24, -85, -62, 69, 89, 73, -126, -127, 35, -123, 75, -81, -58, 92, -57, 54, 121, 76, 104, 29, -75, -12, -96, 42, 35, 70, 47, 9, 52, -42, -124, 125, 85, 61, 101, -17, 28, 80, 118, -88, -30, -41, -111, 116, 73, -103, 121, 52, -128, -6, -91, 29, 123, -74, -81, 84, 9, -58, 17, -110, -112, -98, 62, -64, 52, -29, 121, 56, -23, 23, -50, 3, -104, -31, -76, -118, 61, -56, -96, 55, -11, 47, -7, 36},
                {113, 108, 16, 67, -102, -92, 119, -3, -49, 55, -77, -40, -5, 81, 118, 108, -90, -9},
                {-112, -12, -78},
                {-25, -30, 19, -42, 88, -34, -100, 48, -123, 90, 121, -102, 35, 92, -71, 41, 70, 11, 117, -38, 1, 112, -43, 41, 101, -128, 127, 104, -107, -18, 3, 38, 118, 19, -88, 106, -64, -88, -58, -20, 78, 40, 126, -17, 79, -100, 97, -90, -67, 6, -32, 73, -19, 120, 51, 115, 108, 96, 108},
                {-106, -11, -115, -34, 124, 43, 126, 10, -54, -128, 64, -17, 123, 61},
                null,
                {102, 26, -22, 120, 48, -67, 74, -60, 102, 12, 107, -121, -36, 66, 18, 106, 88, -78, -25, -26, -75, 53, -39, -120, 30, 114, -89, -2, -82, 27, 57, -27, -107, 93, 110, 19, 19, -46, 5, -73, -68, 77, 61, -34, 115, 100, 30, 112, 23, -81, 80, -34, -109, 37, -11, -69, 52, -11, -65, 74, 64, 3, -17, -53},
                {},
                {-79, -43, -111, 26, 104, -110, 119, -50, -122, 36, -53, -51, 16, 80, 17, 79, 101, -93, 126, 81, 94, 35, 100, -124, 43, 6, -87, -42, 3, 77, -33, 104, 103, -111, 39, -36, -98, -108, -55, -94, 16, 98, 81, -28, -98, -26, 72, 118, 12, -79, -16, 78, -29, -71, -42, -13, -117, 25, 112, 66, -39, -31, 5, -40, 41, 36, 101, 13, 24, 35, -121, -46, 116, 127, -25, -79, -16, -2, -41, 45, -39, 118, 90, 110, -69, 12, -12, -27, 78, -76, -118},
                {},
                {-105, 8, -53, 5, 40, -1, -92, -73, -8, -76, 58, 109, 47, 47, -18, 124, 111, -48, 66, -93, 106, 105, 111, -119, -99, 35},
                {0, 39, -73, -79, -102, 67, -37, -104, 44, 71, -76},
                {-49, 15, -13, -52, 63, -45, -109, 57, 62, -109, 123, 67, -41, -16, 113, -123, 83, 44, -95, 78, 79, 127, -116, 73, -46, 94, 23, -56, -99, 25, 125, 51, 35, -121, 101, 9, 23, -31, 58, -56, -22, -14, 100, -48, -117, 103, -106, 49, -104, -10, 86, -101, -92, 79, -9},
                {-13, 56, -17, 6, 120, -4, 65, -71, 73, 35, 44, 82, 110, 97, -89, 13, -40, 80, -91, -39, -4, -97, -10, 106, -117, 99, 123, -44, 73, 3, -26, 95, 66, 115, -31, -32, 89, 81, -92, -104, -77, -33, 9, 19, 24, -24, 25, -34, -79, 15, 57, 77, 69, -107, 95, -85, 36, 12, 96, 93, 0, 53, 6, -12, 79, 105, -102, -87, -115, 12},
                null,
                null,
                {39, 68, -63, -95, -123, 10, 19, -101, 8, -125, -99, 22, -43, -97, -85, 37, 124, 88, 78, 84, 70, 92, -50},
                {-18, 21},
                {-126, 92, 112, 107, -38, -11, 64, 45, -103, 39, 67, -11, -28, 24, 27, -41, 22, 88, 28, -47, 1, -78, 35, 70, 100, 12, -46, -70, 2, 51, -89, 119, 97, -31, -42, -28, -79, -80, -7, 110, -90, 76, -105, -31, 8, 45, 16, -24, 39, 112, -90, 55, 92, -126, -111, 55, 83, 45, 33, 53, 110, 62, -12, 58},
                {6, -92, 4, -53, 50, -32, -98, 29, -31},
                {8, -48, 50, 2, -68, 24, 119},
                {78},
                {-116, -94, -25, 27, -5, -22, -5, -2},
                {},
                {75, 16, 84, 39, -40, 110},
                {77, 61, 89, 113, 68, -92, -103, -126, -68, 68, -54, 57, -50, 36, 109, -91, 71, 114, 117, -99, -8, 73, -1, 50, -124, -94, -60, 35}
        };

        for (int i = 0; i < NUM_EXTENDED; ++i) {
            int len = rnd.nextInt(MAX_LEN);
            double quoteProb = EXPECTED_QUOTES / (double) len;

            byte[] arr = new byte[len];
            rnd.nextBytes(arr);

            int numQuotes = 0;
            // Change each character to a quote with prob. quoteProb
            for (int j = 0; j < arr.length; ++j) {
                if (rnd.nextDouble() < quoteProb) {
                    arr[j] = '"';
                    ++numQuotes;
                } else if (arr[j] == '"') // If it's a quote and shouldn't be,
                    // change to something else
                    ++arr[j];
            }
            ByteArrayInputStream in = new ByteArrayInputStream(arr);
            List<Byte> expected = (byteResults[i] == null) ? null : Arrays.asList(byteResults[i]);
            in.reset();

            assertEquals("Failed random test with length " + len + " and "
                    + numQuotes + " quotes", expected, Streams.getQuoted(in));
        }
    }


    @Test
    public void testReadUntil() throws IOException {
        StringReader in = new StringReader(
                "This is a test<end|nope<endMark> some extra text");
        String expected = "This is a test<end|nope";
        String actual = Streams.readUntil(in, "<endMark>");
        assertEquals(expected, actual);
    }

    @Test
    public void testReadUntilExtended() throws IOException {
        for (int i = 0; i < NUM_EXTENDED; ++i) {
            int len = rnd.nextInt(MAX_LEN) + MIN_LEN;
            byte arr[] = new byte[len * 2];
            rnd.nextBytes(arr);
            String str1 = new String(arr, "UTF-16");
            rnd.nextBytes(arr);
            String str2 = new String(arr, "UTF-16");

            byte emark[] = new byte[20];
            rnd.nextBytes(emark);
            String endMark = new String(emark, "UTF-16");
            String endPart = endMark
                    .substring(0, rnd.nextInt(endMark.length()));

            StringReader in = new StringReader(str1 + endPart + str2 + endMark
                    + str1 + endPart + str1);
            String expected = str1 + endPart + str2;
            String actual = Streams.readUntil(in, endMark);
            assertEquals(
                    "Failed random sequence of characters with random endmark",
                    expected, actual);
        }
    }

    @Test
    public void testFilterOut() throws IOException {
        byte[] bytes = "aabbccddeeaabbccddeeabcde".getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] expectedBytes = "aaccddeeaaccddeeacde".getBytes();
        Streams.filterOut(in, out, (byte) 'b');
        byte[] actualBytes = out.toByteArray();
        assertArrayEquals(expectedBytes, actualBytes);
    }

    /*
     * Copy bytes from input to output, ignoring all occurrences of badByte.
     *
     * @param in
     *
     * @param out
     *
     * @param badByte
     */
    @Test
    public void testFilterOutExtended() throws IOException {
        byte[][] expectedBytesArr = {
                {46, 102, 125, 95, 88, 0, -30, 28, 56, 86, -65, 91, 31, 69, 99, -39},
                {-103, -20, -60, 12, 26, 23, 45, -23, 115, -100, -25, -51, 15, -55, 66, 122, -38, 44, 70, -25, 2, 127, -126, 105, -125, -79, -96, 52, 119, -118, -85, 86, -61, 15, 76, -68, -116, -33},
                {-127, 61, 23, -94, 4, 105, 31, -113, 54, 33},
                {18, 2, -102, -94, -81, -12, 58, -45, 30, 17},
                {},
                {69, 61, 38, 16, 83, -101, -14, 116},
                {-74, 16, 54, -119, 58, 7, 71},
                {},
                {70, 62, 56, 120, 2, 117, 29, -126, 46, -16, -114, 21, 32, 78, 115, 85, 98, 82, -3, -46, -114, -1, 1, 30, -93, 15, 100, -92, 20, 40, 31, 37, 95, 102, -105, 120, -31, 32, -107, -34, 11, 117},
                {-101, 106, 1, -82, -84, -94, 11, 126, 84, 107, -52, -11, 60},
                {126, 52, 46, 29, -16, 103, 59, -15, -116, 75, 83, -36, -65, -119, -54, 77, -23, -98, -62, -44, -25, -84, 35, 71, 69, -97, 63, -64, -24, 25, -53, -61, -63, 5, -71, -40, 2, -124, -46, -84, 27, 123, 3},
                {40, 41, -19, -55, 127, 107, -100, -77, 27, 43, -42, 33, 99, 49, 102, 5, 63, 62, 0, 0, -24, 53, -15, 51, 62, 40, 7, -70, 4, -90, -5, 15, 60, 17, -101, 34, 58, 95, -10, 13, 13, 104, 64, -35, -22, 103, -85, -27, 4, -27, 6, -94, 125, -102, -112, 31, -80, 37, -101, -45, -72, 32, 51, -116, -72, -85, -62, 69, 89, -123},
                {},
                {},
                {110, -76, -44, -17, -9, -113, 123, -81, 107, -111, -123, 54, -27, -15, 67, 17, -77, -120, 44, -58, -36, 23, -75, 112, -87},
                {-54, 8, -105, -96, 108, 48, 36, -6, 62, 56, 13, 1, 116, -49, 72, -24, -128, 84, -6, 31, 40, -123, -124, 124, -87, -109, 40, 93, -121, 49, 97, 102, 26, 120, 48, -67, 74, 107, -121, -36, 66, 18, 106, -78, -25, -26, -75, 53, -39, -120, 30, 114, -89, -2, -82, 27, 57, -27, -107, 110, 19, 19, 5, -73, -68, -34, 115, 100, 30, 23, 80, -109, 37, -11, -69, -11, -65, 74, 3, -17, -53, 93, -72, -11, 34, 125, -1, 114, 36, 12, -103, -67, -2},
                {62, -105, 111, 115, 90, -47, -85, -56, -34, 77, 112, -16, -23, -96, 21, -57, -24, -39, 122, 89, -88, 100, -40, 80, 23, 29, 13, 66, -18, -31, 71, -110, -59, -106, -27, -57, 109, 98, -109, -114, 67, 105, -87, -43},
                {},
                {101, 51, -61},
                {89, 86, 64, 3, -88, -87, -94, -37, 57, 77, 5, -116, 59, -17, -127, 20, 82, 22, -103, -45, -26, 67, -97, 104, 46, -45, 22, -59, -89, 4, 120, 41, -9, -98, -20, 117},
                {},
                {-85, 28, 2, -1, 36, -117, 92, -97, -124, -46, -17, -54, 56, -93, -76, -44, -116, -81, 0, -38, -5, 6, -120, -123, -39, 34, 43, 74, 101, 102, -104, 68, 13, -8, 17, 95, -67, 34, -126, 43, -33, -118, 94, -97, -103, 62, 101, 90, 40, 114, 82, 104, -5, -2, 82, -97, -24, -18, -70, -3, 70, 28, -36, -14, -85, 18, 14, -7, 17, 23, 66, 99, -110, -79, 4, 24, -108, -121, -41, -31, -79, -96, -18, 57, -11, -113, 71, 36, -105, -51, 20, -50, 116, 115, -111, -110, -117, 68, -91, 94, -35, -33, -98, 109, -69, 80, -16, -25, -25, -125, -18, 72, -81, 4, 84, -15, -29, -97, -7, 11, -71, -82, -104, -116, 2, 76, 8, -2, 48, 106},
                {9, -34, 57, -107, 95, -85, 36, 12, 96, 0, 53, 6, 79, -87, -115, 12, 26, -33, -88, -125, -90, 89, -70, -80, 59, 35, 38, -42, 32, 90, -127, -22, 18, 9, 1, -5, 36, 89, 47, -123, -108, 118, -56},
                {109, -47, 65, 40, -112, 93, -111, -63, 71, 46, -13, 99, 118, 26, -128, 114, -112, -29, -81, -34, -77, 37, 23, -83, -76, 112, -74, -101, -35, 119, -38, 118, -57, 0, -94, 66, 106, -74, -64, -109, -19, -64, 105, 115, 64, -56, 14, 25, 43, 13, 31, -109, 76, -64, 22, -20, -2, -10, -51, -86, -17, -95, -63, 63, -51, 117, -53, -99, 3, 7, 54, -34, -122, -7, 29, 3, -22, 38, -120, -13, -82, 53, 16, -76, -51, 38, 2, 97, 72, 89, -59, -41, 106, 93, 45, -96, -112, 108, 114, 79, 24, 66, 44, 101, -47, -17, 16, -94, 116, -93, -79, 114},
                {-114, -47, 43, 16, 35, -46, -112, 105, -54, 89, 22, 79, 120, 2, -55, -107, -12, -84, -112, -83, 52, 36, -19, -120, -70, -29, -112, 114, -17, -74, 38, -48, -119, -55, -78, 72, 67, 41, 36, -72, 80, 100, -25, 81, -1},
                {114, 61, -6, 4, -57, -25, 118, -128, 95, 34, 82, 34, -61, 104, -126, -95, -54, -20, -104, 93, 44, -106, -119, 112, 63, 28, -4, 103, -13, 71, 85, -97, -7, -17, 53, 88, 108, -45, -97, -43, -75, 91, 30, 43, -63, 123, 1, -74, 42, -28, -86, 18, 69, -126, 92, 112, 107, -38, -11, 64, 45, -103, 39, 67, -11, -28, 24, 27, -41, 22, 88, 28, -47, 1, -78, 34, 100, 12, -46, -70, 2, 51, -89, 119, 97, -42, -28, -80, -7, 110, -90, -105, -31, 8, 45, 16, -24, 39, 112, -90, 55, 92, -126, -111, 55, 83, 45, 33, 53, 110, -12, 58, 35, 78, 44, 83, -86, 39, -95, 58, -60, -55, -50, 51, -92, 28, 76, 78, -42, -89, -13, 102, -76, 6, -107, 4, -117, 107, -31, -19, 96, 87, -110, -72, 91, -39, -58, 114, 59, 41, 92, -106, 40, -106, 56, 5, 69, -103, 16, 72, 20, -23, 21, -64, -26, -115},
                {},
                {102, 80, 94, 14, 72, -22, -22, -5, 110, 103, -93, -80, -102, -41, 38, -16, -29, -69, 12, 91, -118, -7, 114, -21, -40, 64, 6, 46, -28, -32, -42, 105, -48, 35, -45, 9, 38, 109, 99, 63, -93, 40, 6, -29, 100, 3, -54, 7, -117, -87, -22, -19, 7, -6, 75, 25, -110, 127, 118, 122, 8, -28, 31, 0, 86, -108, -127, 96, 5, -103, -107, -110, 87, 127, 46, -9, 73, -45, -85, 9, -84, -56, 52, -41, 59, -78, -112, -38, -44, 42, -27, 11, 72, -49, -71, 25, 96, 94, 101, -81, 66, 7, -93, -55, -98, -30, -86, 74, -94, 115, 114, 117, 18, 47, 32, -36, 24, 15, -65, -107, -77, 88, -38, -115, -21, -84, -31, -13, -81, 1, -73, 47, -36, 48, 7, -15, -58, -48, -50, 101, 105, 27, 67, 60, 123, -49, 2, -99, 94, -5, 127, -67, -114, -24, 51, 16, -71, 22, 75, -95, -39, -53, -65, -10},
                {},
                {91, -58, 65, 87, 1, -125, -48, 19, -124, -111, 81, 23, -100, 67, -107, -25, 68, -81, 95, -72, 81, -109, -74, 26, 122, -107, 126, -90, 18, -73, 105, 52, 49, 79, 16, 122, 36, 19, -105},
                {90, 67, 113, -10, -8, -99, -85, 9, -44, -118, 99, -68, 96, 123, 83},
                {}
        };
        for (int i = 0; i < NUM_EXTENDED; ++i) {
            int len = rnd.nextInt(MAX_LEN) + MIN_LEN;
            int numExpectedBadBytes = rnd.nextInt((int) (len * 1.5)); // Expected number of bad bytes
            double badByteProb = (double) numExpectedBadBytes / len;
            byte badByte = (byte) rnd.nextInt();

            byte[] bytes = new byte[len];
            rnd.nextBytes(bytes);

            int numBadBytes = 0;
            for (int j = 0; j < bytes.length; ++j) {
                if (rnd.nextDouble() < badByteProb) {
                    bytes[j] = badByte;
                    ++numBadBytes;
                } else if (bytes[j] == badByte) {
                    ++bytes[j];
                }
            }

            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] expectedBytes = expectedBytesArr[i];

            in.reset();
            out.reset();

            Streams.filterOut(in, out, badByte);
            byte[] actualBytes = out.toByteArray();
            assertArrayEquals(
                    "Failed random test with length " + len + " and "
                            + numBadBytes + " bad bytes (bad byte was "
                            + badByte + ")", expectedBytes, actualBytes);
        }
    }

    @Test
    public void testReadNumber() throws IOException {
        byte[] bytes = {0x12, 0x34, 0x56, 0x78, 0x0a};
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        long num = Streams.readNumber(in);
        assertEquals(0x123456780aL, num);
    }

    /*
     * Read a 48-bit (unsigned) integer from the stream and return it. The
     * number is represented as five bytes, with the most-significant byte
     * first. If the stream ends before 5 bytes are read, return -1.
     *
     * @param in
     *
     * @return the number read from the stream
     */

    @Test
    public void testReadNumberExtended() throws IOException {
        long[] expectedArr = {
                202953117742L, -1, 366708849197L, 441656600337L, 208945852018L, 352396804571L, 180304120522L, -1,
                528147224634L, -1, -1, 61520846177L, 202451644346L, -1, 16209040937L, 1011654781320L, -1, 492932237884L,
                -1, 549410407345L, 634543157697L, -1, 368943563303L, 747542827473L, -1, 977557585411L, 705088309861L,
                107070656478L, -1, 212020643119L, 9084644584L, -1
        };
        for (int i = 0; i < NUM_EXTENDED; ++i) {
            int len = rnd.nextInt(MAX_LEN);
            if (rnd.nextDouble() < 0.2) {
                // We test for short inputs one fifth of the time
                len = rnd.nextInt(5);
            }
            byte[] bytes = new byte[len];
            rnd.nextBytes(bytes);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            long expected = expectedArr[i];

            in.reset();
            long num = Streams.readNumber(in);
            assertEquals("Failed random test with length " + len, expected, num);
        }
    }
}
