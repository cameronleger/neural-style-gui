package com.cameronleger.neuralstyle;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;

class FileAdapter extends TypeAdapter<File> {
    public File read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        return new File(reader.nextString());
    }
    public void write(JsonWriter writer, File value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }
        writer.value(value.getAbsolutePath());
    }
}