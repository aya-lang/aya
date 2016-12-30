package ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import aya.Aya;

// Initial implementation from
// http://stackoverflow.com/questions/33067009/make-java-textarea-as-inputstream-to-run-shell-command-in-interactive-mode-using
public class TextPaneInputStream extends InputStream {

    final InputLine field;
    final BlockingQueue<String> q;

    public TextPaneInputStream() {
        this.field = new InputLine();
        q = new LinkedBlockingQueue<>();
        
        field.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyChar()=='\n'){
                	 String str = field.getText() + "\r\n";
                	 q.add(str);
                     field.clear();
                }
            }

			@Override
			public void keyPressed(KeyEvent arg0) {}

			@Override
			public void keyTyped(KeyEvent arg0) {}
        });
    }

    private String s;
    int pos;

    @Override
    public int read() throws IOException {
        while (null == s || s.length() <= pos) {
            try {
                s = q.take();
                pos = 0;
            } catch (InterruptedException ex) {
               ex.printStackTrace(Aya.getInstance().getErr());
            }
        }
        int ret = (int) s.charAt(pos);
        pos++;
        return ret;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytes_copied = 0;
        while (bytes_copied < 1) {
            while (null == s || s.length() <= pos) {
                try {
                    s = q.take();
                    pos = 0;
                } catch (InterruptedException ex) {
                    ex.printStackTrace(Aya.getInstance().getErr());
                }
            }
            int bytes_to_copy = len < s.length()-pos ? len : s.length()-pos;
            System.arraycopy(s.getBytes(), pos, b, off, bytes_to_copy);
            pos += bytes_to_copy;
            bytes_copied += bytes_to_copy;
        }
        return bytes_copied;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length); 
    }
    
    public InputLine getInputLine() {
    	return field;
    }

}
