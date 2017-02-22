package com.reforms.orm.scenario;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.Assert.assertNotNull;

/**
 * Получение скриптов для выполнения из файла
 * @author evgenie
 */
public class ScriptIterator {

    private final String content;

    private String fragment;

    private int index;

    public ScriptIterator(String content) {
        this.content = removeComments(content);
    }

    private String removeComments(String content) {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String preparedLine = line.trim();
                if (preparedLine.isEmpty() || preparedLine.startsWith("--")) {
                    continue;
                }
                result.append(preparedLine).append("\n");
            }
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
        return result.toString();
    }

    public boolean hasNext() {
        if (fragment != null) {
            return true;
        }
        if (index >= content.length()) {
            return false;
        }
        int prevIndex = index;
        index = content.indexOf(';', prevIndex);
        if (index == -1) {
            index = content.length();
        }
        fragment = content.substring(prevIndex, index);
        index++;
        return true;
    }

    public String next() {
        if (hasNext()) {
            String temp = fragment;
            fragment = null;
            return temp;
        }
        return null;
    }

    /**
     * Получить ресурс как файл
     * @param relativePath относительный путь к файлу
     * @param owner класс, относительно расположения которого указывается путь к ресурсу
     * @return файл
     * @throws IOException
     */
    public static ScriptIterator getResourceIterator(String relativePath, Class<?> owner) throws IOException {
        URL resourceUrl = owner.getResource(relativePath);
        assertNotNull("Ресурс " + relativePath + " не найден", resourceUrl);
        File resource = new File(resourceUrl.getFile());
        byte[] content = Files.readAllBytes(resource.toPath());
        return new ScriptIterator(new String(content, "Cp1251"));
    }

    public static void main(String[] args) {
        ScriptIterator si = new ScriptIterator("--------------\n\ndo();do2();");
        while (si.hasNext()) {
            System.out.println(si.next());
        }
    }
}
