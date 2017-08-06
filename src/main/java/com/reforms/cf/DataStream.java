package com.reforms.cf;

import java.io.*;

/**
 * Stream for data inside
 * @author evgenie
 */
public class DataStream implements Closeable {

    private int readCount = 0;
    private DataInputStream stream;

    public DataStream(DataInputStream stream) {
        this.stream = stream;
    }

    public int u1() {
       try {
           readCount++;
           return stream.readUnsignedByte();
       } catch (IOException ioe) {
           throw new RuntimeException(ioe);
       }
    }

    public int u1s() {
        try {
            readCount++;
            return stream.readByte();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
     }

    public int u2() {
        try {
            readCount += 2;
            return stream.readUnsignedShort();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
     }

    public int u2s() {
        try {
            readCount += 2;
            return stream.readShort();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
     }

    public int u4() {
        try {
            readCount += 4;
            return stream.readInt();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
     }

    public byte[] u1Array(int count) {
        try {
            byte[] data = new byte[count];
            int wasRead = 0;
            do {
                wasRead += stream.read(data, wasRead, count - wasRead);
                readCount += wasRead;
            } while (wasRead != count);
            return data;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public void skip(int count) {
        u1Array(count);
    }

    public int[] u2Array(int count) {
        int[] data = new int[count];
        for (int index = 0; index < count; index++) {
            data[index] = u2();
        }
        return data;
    }

    public int getReadCount() {
        return readCount;
    }

    @Override
    public void close() throws IOException {
        if (stream != null) {
            stream.close();
        }
    }

    public static DataStream from(File file) throws IOException {
        return new DataStream(new DataInputStream(new BufferedInputStream(new FileInputStream(file))));
    }

    public static DataStream from(InputStream stream) throws IOException {
        if (stream instanceof DataInputStream) {
            return new DataStream((DataInputStream) stream);
        }
        return new DataStream(new DataInputStream(new BufferedInputStream(stream)));
    }

    public static DataStream from(byte[] data) {
        try {
            return from(new ByteArrayInputStream(data));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
