package com.jbake.ui.util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;

/**
 * Created by julianliebl on 07.03.2015.
 */
public class TextAreaOutputStream extends OutputStream{

    private TextArea mTextArea;
    
    public TextAreaOutputStream(TextArea txtara) {
        mTextArea = txtara;
    }

    public synchronized void close() {
        mTextArea=null;
    }

    public synchronized void flush() {
    }

    @Override
    public void write(int b) {
        updateTextArea(String.valueOf((char) b));
    }

    @Override
    public void write(byte[] b, int off, int len) {
        updateTextArea(new String(b, off, len));
    }

    @Override
    public void write(byte[] b) {
        write(b, 0, b.length);
    }

    private void updateTextArea(final String text) {
        Platform.runLater(() -> mTextArea.appendText(text));
    }

}