package com.reforms.orm.select.bobj.reader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;

/**
 * Контракт на чтение значения AsciiStream из выборки ResultSet
 * @author evgenie
 */
class AsciiStreamParamRsReader implements IParamRsReader<byte[]> {

    @Override
    public byte[] readValue(int columnIndex, ResultSet rs) throws Exception {
        try (InputStream stream = rs.getAsciiStream(columnIndex)) {
            if (rs.wasNull()) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] chunk = new byte[1024];
            int wasRead = -1;
            while ((wasRead = stream.read(chunk)) > 0) {
                baos.write(chunk, 0, wasRead);
            }
            return baos.toByteArray();
        }
    }

}
