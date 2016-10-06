package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.File;


import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import element.Element;
import element.InteractiveElement;
import element.variable.Variable;


@SuppressWarnings("serial")
public class ElementIDE extends JFrame
{	
	public static final String VERSION_NAME = "2015 Nov 10";
	
	private Element element;
	
	//Layout
	private MyConsole interpreter;
	private JMenu menu;
	private JMenuBar menuBar;
	
	private ElementIDE thiside;
    

	public ElementIDE(Element elem) {
		super("Element");
		
		this.thiside = this;

				
		this.element = elem;
		this.interpreter = new MyConsole(element);
		this.menu = new JMenu();
		this.menuBar = new JMenuBar();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				
		//Keyboard Listener
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		  .addKeyEventDispatcher(new KeyEventDispatcher() {
			    public boolean dispatchKeyEvent(KeyEvent e) {
			    //System.out.println(e.getID() + " - " + e.getKeyCode());
			    	  
		    	  //On key up: 402, On key down: 401
		    	  if(e.getID() == 401) {
		    		  switch(e.getKeyCode()) {
		    		  case KeyEvent.VK_ENTER:
		    			  if(!interpreter.getInputLine().getText().equals("") && interpreter.getInputLine().inFocus()) {
							  interpreter.eval(interpreter.getInputLine().getText());
							  interpreter.clrAndFocus();
						  } 
		    			  break;
		    		  case KeyEvent.VK_UP:
		    			  if(interpreter.getInputLine().inFocus()) {
							  interpreter.getInputLine().loadLastText();
						  }
		    			  break;
		    		  case KeyEvent.VK_DOWN:
						  if(interpreter.getInputLine().inFocus()) {
							  interpreter.getInputLine().loadPrevText();
						  }
						  break;
		    		  case KeyEvent.VK_TAB:
						  interpreter.getInputLine().tabPressed();
						  break;
		    		  case KeyEvent.VK_I:
		    			  if(e.isControlDown()) {
		    				  interpreter.getInputLine().grabFocus();
		    			  }
		    			  break;
		    		  case KeyEvent.VK_Q:
		    			  if(e.isControlDown()) {
		    				  if(QuickSearch.isFrameActive()) {
			    				  QuickSearch.updateHelpTextInFrame(Element.getQuickSearchData());
		    					  QuickSearch.frameFocus();
		    				  } else {
		    					  QuickSearch.newQSFrame(Element.getQuickSearchData());
		    				  }
		    			  }
		    			  break;
		    		  case KeyEvent.VK_E:
		    			  if(e.isControlDown()) {
		    				  if(EditorWindow.isFrameActive()) {
		    					  EditorWindow.frameFocus();
		    				  } else {
		    					  EditorWindow.newEditorFrame(thiside);
		    				  }
		    			  }
		    			  break;
		    		  case KeyEvent.VK_R:
		    			  if(e.isControlDown()) {
		    				  if (EditorWindow.activeEditor == null) {
		    						JOptionPane.showMessageDialog(interpreter, "No editor window open", "ERROR", JOptionPane.ERROR_MESSAGE);
		    				  }
		    				  EditorWindow.activeEditor.run();
		    			  }
		    			  break;
		    		  }
		    	  }
		    	  return false;
			}
		});
		
		//Confirm Close
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) { 
				if(EditorWindow.hasText()) {
					String ObjButtons[] = {"Yes","No"};
				    int PromptResult = JOptionPane.showOptionDialog(null, 
				        "Editor still has code. Are you sure you want to exit?", "Editor Has Code", 
				        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, 
				        ObjButtons,ObjButtons[1]);
				    if(PromptResult==0) {
				      System.exit(0);          
				    }
				} else {
					System.exit(0);
				}
		    }
		});
		
		//Menu Bar
		menuBar.setPreferredSize(new Dimension(100, 20));
		
		//File
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("");
		//Load
		JMenuItem mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				//JOptionPane.showMessageDialog(interpreter, "Load not yet implemted", "ERROR", JOptionPane.ERROR_MESSAGE);
				String path = requestFilePathUI();
				if (path != null) {
					path = path.replace("\\", "\\\\");
					Element.getInstance().run("\"" + path + "\"G~");
					
				}
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Load");
		menu.add(mi);


		
		menuBar.add(menu);
			
		//Tools
		menu = new JMenu("Tools");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("");
		//Insert Filename
		mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				insertFilenameAtCarat();
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Insert Filename..");
		menu.add(mi);
		//Clear Console
		mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				interpreter.cw.clear();
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Clear Console");
		menu.add(mi);
		mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				interpreter.eval("100 .B");
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Primes below 100");
		menu.add(mi);
		mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				if(EditorWindow.isFrameActive()) {
					  EditorWindow.frameFocus();
				  } else {
					  EditorWindow.newEditorFrame(thiside);
				  }
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Open Editor   ctrl+E");
		menu.add(mi);
		menuBar.add(menu);
		
		
		//Help
		//Quick Search
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("");
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				if(QuickSearch.isFrameActive()) {
					QuickSearch.frameFocus();
				} else {
					QuickSearch.newQSFrame(Element.getQuickSearchData());
				}
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Quick Search");
		menu.add(mi);
		
		//Key Bindings
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(interpreter, ""
						+ "ctrl+Q		  Quick Search\n"
						+ "ctrl+I		  Interpreter\n"
						+ "ctrl+E         Editor\n"
						);
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Key Bindings");
		menu.add(mi);
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(interpreter, "Element IDE\nNicholas Paul\nVersion: " + VERSION_NAME + "\nElement Version: " + Element.VERSION_NAME);
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("About");
		menu.add(mi);
		menuBar.add(menu);
		
		JPanel all = new JPanel();
		all.setLayout(new BorderLayout());


		
		JPanel smallConsole = new JPanel();
		smallConsole.setLayout(new BorderLayout());
		smallConsole.add(interpreter, BorderLayout.CENTER);
		all.add(smallConsole);
		
		add(all);
		
		setJMenuBar(menuBar);
		pack();
		setVisible(true);
	
		
		interpreter.getInputLine().grabFocus();
	}
	
	public void insertFilenameAtCarat() {
		File file = chooseFile();
		if(file != null) {
			String path = file.getPath();
			path = path.replace("\\", "\\\\");
            interpreter.getInputLine().insertAtCaret("\"" + path + "\"");
		}
	}
	
	public String requestFilePathUI() {
		File file = chooseFile();
		if(file != null) {
			return file.getPath();
		}
		return null;
	}
	
	public static File chooseFile() {
		JFileChooser fc = new JFileChooser();
		
		//Set working directory
		File here = new File(".");
		fc.setCurrentDirectory(here);
		here.delete();
		
		int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        } else {
        	return null;
        }
	}
	
	public Element getElement() {
		return this.element;
	}
	
	public ConsoleWindow out() {
		return this.interpreter.out();
	}
	
	public void eval(String s, String input_name) {
		interpreter.eval(s, input_name);
	}
	
	public static void main(String[] args) {
		Variable v = new Variable("uyiuyiutiyutcutc");
		v.getID();

		//No args: use the GUI
		if(args.length == 0) {
			//Load and initialize element
			Element elem = Element.getInstance();
			boolean base_loaded_succ = InteractiveElement.loadBase(elem);
			
			//Load the ide
			ElementIDE ide = new ElementIDE(elem);

			//Print messages to the console if base had errors
			if(base_loaded_succ) {
				ide.interpreter.cw.print(elem.getOut().dumpAsString());	
			} else {
				ide.interpreter.cw.print(elem.getOut().dumpAsString());
				ide.interpreter.cw.print("\n\n");
			}
			
			//Grab focus
			ide.interpreter.getInputLine().grabFocus();
		}
		
		//Command line arguments: use the console
		else {
			InteractiveElement.main(args);
		}		
	}
}


