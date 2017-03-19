package com.reforms.orm.dao.report.converter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;

import com.reforms.orm.dao.column.SelectedColumn;

/**
 * Контракт на преобразование значения из выборки ResultSet в строковое значение
 * @author evgenie
 */
class BinaryStreamColumnValueConverter implements IColumnValueConverter {

    private String encoding;

    public BinaryStreamColumnValueConverter(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String convertValue(SelectedColumn column, ResultSet rs) throws Exception {
        try (InputStream stream = rs.getBinaryStream(column.getIndex())) {
            if (rs.wasNull()) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] chunk = new byte[1024];
            int wasRead = -1;
            while ((wasRead = stream.read(chunk)) > 0) {
                baos.write(chunk, 0, wasRead);
            }
            return baos.toString(encoding);
        }
    }

}
