package ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import aya.StaticData;

// Initial implementation from
// http://stackoverflow.com/questions/33067009/make-java-textarea-as-inputstream-to-run-shell-command-in-interactive-mode-using
public class TextPaneInputStream extends InputStream {

    final InputLine field;
    final BlockingQueue<Byte> q;

    public TextPaneInputStream() {
        this.field = new InputLine();
        q = new LinkedBlockingQueue<>();
        
        field.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyChar()=='\n'){
                	 String str = field.getText() + "\r\n";
                	 //q.add(str);
                	 for (byte b : str.getBytes(StandardCharsets.UTF_8)) {
                		 q.add(b);
                	 }
                     field.clear();
                }
            }

			@Override
			public void keyPressed(KeyEvent arg0) {}

			@Override
			public void keyTyped(KeyEvent arg0) {}
        });
    }

    //private String s;
    //int pos;
    private byte out_byte;

    @Override
    public int read() throws IOException {
        //while (null == s || s.length() <= pos) {
            try {
                out_byte = q.take();
                //pos = 0;
            } catch (InterruptedException ex) {
               ex.printStackTrace(StaticData.IO.err());
            }
        //}
        System.out.println("Sending byte: " + out_byte);
        return out_byte;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    byte[] buffer = new byte[1024];
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int byte_count = 0;
        byte prev = '\0';
        byte cur = '\0';
        
        while (true) {
            try {
                cur = q.take();
                buffer[byte_count] = cur;
                byte_count++;
                
                if (prev == (byte)'\r' && cur == (byte)'\n') {
                	break;
                }
                
                prev = cur;
            } catch (InterruptedException ex) {
                ex.printStackTrace(StaticData.IO.err());
            }
        }

        System.arraycopy(buffer, 0, b, off, byte_count);
        
        return byte_count;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length); 
    }
    
    public InputLine getInputLine() {
    	return field;
    }

}
