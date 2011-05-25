package client.main;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class TextFieldLimit extends PlainDocument {
	private static final long serialVersionUID = 2L;
	public static final int limitServerName = 20;
	public static final int limitUsername = 20;
    public static final int limitMessage = 60;
    
    private static int state = 0;
    
    public static synchronized int getState() {
    	return state;
    }
    
    public static void triggerState() {
    	if (state < 2)
    		state += 1;
    	else
    		state = 0;
    }
    
    private int getLimit() {
    	switch (state) {
	    	case 1:
	    		return limitUsername;
	    	case 2:
	    		return limitMessage;
    	}
    	
    	return limitServerName;
    }
    
    public void insertString(int offset, String  str, AttributeSet attr) throws BadLocationException {
        if (str == null) return;
        
        if ((getLength() + str.length()) <= getLimit()) {
            super.insertString(offset, str, attr);
        }
    }
}
