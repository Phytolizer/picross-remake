package bgusolver;

import java.util.Arrays;


public class JCLineHashKey {
    private byte[] line;
    private byte[] blocks;
    private int hashCode;

    JCLineHashKey(byte[] line, byte[] blocks) {
        this.line = line.clone();
        this.blocks = blocks;
        this.hashCode = Arrays.hashCode(line) ^ Arrays.hashCode(blocks);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object other) {
        return (this.blocks == ((JCLineHashKey) other).blocks) && Arrays.equals(this.line, ((JCLineHashKey) other).line);
    }

    /**
     * compresses a line - put each 4 cells into a single byte (every cell is 2 bits)
     * bits are: 0b01 - black cell
     * 0b10 - unknown cell
     * 0b00 - white cell
     *
     * @param line input (uncompressed) line
     * @return
     */
    public static int[] compressLine(byte[] line) {
        int[] ans = new int[line.length / 16 + 1];
        int compIdx = 0;
        int nextBit = 0;
        for (int i = 0; i < line.length; i++) {
            nextBit = (nextBit << 2) | line[i];
            if (((i + 1) % 16) == 0) {
                ans[compIdx] = nextBit;
                nextBit = 0;
                compIdx++;
            }
        }
        ans[compIdx] = nextBit;
        return ans;
    }
}
