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

import aya.Aya;
import aya.InteractiveAya;
import aya.variable.Variable;
import test.AyaTestCases;


@SuppressWarnings("serial")
public class AyaIDE extends JFrame
{	
	public static final String VERSION_NAME = "2016 Nov 11";
	
	private Aya _aya;
	
	//Layout
	private MyConsole _interpreter;
	private JMenu _menu;
	private JMenuBar _menuBar;
	
	private AyaIDE thiside;
    

	public AyaIDE(Aya aya) {
		super("Aya");
		
		this.thiside = this;

				
		this._aya = aya;
		this._interpreter = new MyConsole(_aya);
		this._menu = new JMenu();
		this._menuBar = new JMenuBar();
		
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
		    			  if(!_interpreter.getInputLine().getText().equals("") && _interpreter.getInputLine().inFocus()) {
							  _interpreter.eval(_interpreter.getInputLine().getText());
							  _interpreter.clrAndFocus();
						  } 
		    			  break;
		    		  case KeyEvent.VK_UP:
		    			  if(_interpreter.getInputLine().inFocus()) {
							  _interpreter.getInputLine().loadLastText();
						  }
		    			  break;
		    		  case KeyEvent.VK_DOWN:
						  if(_interpreter.getInputLine().inFocus()) {
							  _interpreter.getInputLine().loadPrevText();
						  }
						  break;
		    		  case KeyEvent.VK_TAB:
						  _interpreter.getInputLine().tabPressed();
						  break;
		    		  case KeyEvent.VK_I:
		    			  if(e.isControlDown()) {
		    				  _interpreter.getInputLine().grabFocus();
		    			  }
		    			  break;
		    		  case KeyEvent.VK_Q:
		    			  if(e.isControlDown()) {
		    				  if(QuickSearch.isFrameActive()) {
			    				  QuickSearch.updateHelpTextInFrame(Aya.getQuickSearchData());
		    					  QuickSearch.frameFocus();
		    				  } else {
		    					  QuickSearch.newQSFrame(Aya.getQuickSearchData());
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
		    						JOptionPane.showMessageDialog(_interpreter, "No editor window open", "ERROR", JOptionPane.ERROR_MESSAGE);
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
				exit();
		    }
		});
		
		//Menu Bar
		_menuBar.setPreferredSize(new Dimension(100, 20));
		
		//File
		_menu = new JMenu("File");
		_menu.setMnemonic(KeyEvent.VK_A);
		_menu.getAccessibleContext().setAccessibleDescription("");
		//Load
		JMenuItem mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				//JOptionPane.showMessageDialog(interpreter, "Load not yet implemted", "ERROR", JOptionPane.ERROR_MESSAGE);
				String path = requestFilePathUI();
				if (path != null) {
					path = path.replace("\\", "\\\\");
					Aya.getInstance().run("\"" + path + "\"G~");
					
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
		_menu.add(mi);


		
		_menuBar.add(_menu);
			
		//Tools
		_menu = new JMenu("Tools");
		_menu.setMnemonic(KeyEvent.VK_A);
		_menu.getAccessibleContext().setAccessibleDescription("");
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
		_menu.add(mi);
		//Clear Console
		mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				_interpreter.cw.clear();
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Clear Console");
		_menu.add(mi);
		mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				_interpreter.eval("100 .B");
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Primes below 100");
		_menu.add(mi);
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
		_menu.add(mi);
		mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				_interpreter.cw.printWarn(AyaTestCases.runTests());
				
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Run Tests");
		_menu.add(mi);
		_menuBar.add(_menu);
		
		
		//Help
		//Quick Search
		_menu = new JMenu("Help");
		_menu.setMnemonic(KeyEvent.VK_A);
		_menu.getAccessibleContext().setAccessibleDescription("");
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				if(QuickSearch.isFrameActive()) {
					QuickSearch.frameFocus();
				} else {
					QuickSearch.newQSFrame(Aya.getQuickSearchData());
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
		_menu.add(mi);
		
		//Key Bindings
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(_interpreter, ""
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
		_menu.add(mi);
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(_interpreter, "Aya IDE\nNicholas Paul\nVersion: " + VERSION_NAME + "\nAya Version: " + Aya.VERSION_NAME);
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("About");
		_menu.add(mi);
		_menuBar.add(_menu);
		
		JPanel all = new JPanel();
		all.setLayout(new BorderLayout());


		
		JPanel smallConsole = new JPanel();
		smallConsole.setLayout(new BorderLayout());
		smallConsole.add(_interpreter, BorderLayout.CENTER);
		all.add(smallConsole);
		
		add(all);
		
		setJMenuBar(_menuBar);
		pack();
		setVisible(true);
	
		
		_interpreter.getInputLine().grabFocus();
	}
	
	public static void exit() {
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
	
	public void insertFilenameAtCarat() {
		File file = chooseFile();
		if(file != null) {
			String path = file.getPath();
			path = path.replace("\\", "\\\\");
            _interpreter.getInputLine().insertAtCaret("\"" + path + "\"");
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
	
	public Aya getAya() {
		return this._aya;
	}
	
	public ConsoleWindow out() {
		return this._interpreter.out();
	}
	
	public void eval(String s, String input_name) {
		_interpreter.eval(s, input_name);
	}
	
	public static void main(String[] args) {
		Variable v = new Variable("uyiuyiutiyutcutc");
		v.getID();

		//No args: use the GUI
		if(args.length == 0) {
			//Load and initialize aya
			Aya aya = Aya.getInstance();
			boolean base_loaded_succ = InteractiveAya.loadBase(aya);
			
			//Load the ide
			AyaIDE ide = new AyaIDE(aya);

			//Print messages to the console if base had errors
			if(base_loaded_succ) {
				ide._interpreter.cw.print(aya.getOut().dumpAsString());	
			} else {
				ide._interpreter.cw.print(aya.getOut().dumpAsString());
				ide._interpreter.cw.print("\n\n");
			}
			
			//Grab focus
			ide._interpreter.getInputLine().grabFocus();
		}
		
		//Command line arguments: use the console
		else {
			InteractiveAya.main(args);
		}		
	}
}


