package engine.ui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.*;

import engine.SearchEngine;


// The Simple Search Engine UI.
public class Browser extends JFrame implements HyperlinkListener,ActionListener {
	private static final long serialVersionUID = 6462055141289424041L;

	private SearchEngine engine;
	// query input text field.
	private JTextField queryField;

	// Editor pane for displaying pages.
	private JEditorPane resultPane;

	// Constructor for VMI Search Engine.
	public Browser() {
		// Set application title.
		super("VMI Search Enginer");
		engine = new SearchEngine();
		engine.start();
		
		WeightButtons weightOptions = new WeightButtons(this);
		IndexingOptionButtons indexingOptions = new IndexingOptionButtons(this);
		
		
		// Open full size window.
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 

		// Handle closing events.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				actionExit();
			}
		});

		// Set up file menu.
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		fileExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExit();
			}
		});
		fileMenu.add(fileExitMenuItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);

		
		
		
		// Set up button panel.
		JPanel buttonPanel = new JPanel();
		
		
		/***********************
		 * EXTRA POINTS: 25 points
		 * display weight options and title indexing options using Combo Box (JComboBox)
		 * You need to change the class IndexingOptionButtons and WeightButtons Class
		 * of course, it should work! 
		 * references: https://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html
		 */
		//set weight buttons
		buttonPanel.add(weightOptions.getWeightButtons(), BorderLayout.LINE_START);

		//set title indexing options
		buttonPanel.add(indexingOptions.getIndexingOptions(), BorderLayout.LINE_START);

		queryField = new JTextField(35);
		queryField.setMaximumSize( queryField.getPreferredSize() );
		queryField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					search();
				}
			}
		});
		buttonPanel.add(queryField);
		JButton goButton = new JButton("Search");
		goButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		buttonPanel.add(goButton);

		// Set up page display.
		resultPane = new JEditorPane();
		resultPane.setContentType("text/html");
		resultPane.setEditable(false);
		resultPane.addHyperlinkListener(this);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buttonPanel, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(resultPane),
				BorderLayout.CENTER);
	}

	// Exit this program.
	private void actionExit() {
		System.exit(0);
	}

	
	// Load and show the page specified in the location text field.
	private void search() {
		String query = this.queryField.getText().trim();
		showPage(engine.query(query));

	}

	// Show dialog box with error message.
	private void showError(String errorMessage) {
		JOptionPane.showMessageDialog(this, errorMessage,
				"Error", JOptionPane.ERROR_MESSAGE);
	}


	/* Show the specified page and add it to
     the page list if specified. */
	private void showPage(String contents) {
		// Show hour glass cursor while crawling is under way.
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try {

			resultPane.setText(contents);


		} catch (Exception e) {
			// Show error messsage.
			showError("Unable to load page");
		} finally {
			// Return to default cursor.
			setCursor(Cursor.getDefaultCursor());
		}
	}



	// Handle hyperlink's being clicked.
	public void hyperlinkUpdate(HyperlinkEvent event) {
		if(event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		openWebpage(event.getURL());
	}

	public void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	// Run the VMI Search Engine.
	public static void main(String[] args) {
		try {
			 for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			        if ("Nimbus".equals(info.getName())) {
			            UIManager.setLookAndFeel(info.getClassName());
			            break;
			        }
			    }
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Browser browser = new Browser();
		
		
		browser.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String opt = e.getActionCommand();
		if(opt.compareTo(WeightButtons.bm11Weight)==0)
			SearchEngine.setWeightingOption(SearchEngine.W_BM11);
		if(opt.compareTo(WeightButtons.bm15Weight)==0)
			SearchEngine.setWeightingOption(SearchEngine.W_BM15);
		else if(opt.compareTo(WeightButtons.bm25Weight)==0)
			SearchEngine.setWeightingOption(SearchEngine.W_BM25);
		else if(opt.compareTo(WeightButtons.booleanWeight)==0)
			SearchEngine.setWeightingOption(SearchEngine.W_BOOL);
		else if(opt.compareTo(WeightButtons.tfWeight)==0)
			SearchEngine.setWeightingOption(SearchEngine.W_TF);
		else if(opt.compareTo(WeightButtons.tfidfWeight)==0)
			SearchEngine.setWeightingOption(SearchEngine.W_TFIDF);
		else if(opt.compareTo(WeightButtons.idfWeight)==0)
			SearchEngine.setWeightingOption(SearchEngine.W_IDF);
		else if(opt.compareTo(IndexingOptionButtons.withTitle)==0)
			SearchEngine.setIndexingOption(true);
		else if(opt.compareTo(IndexingOptionButtons.withoutTitle)==0)
			SearchEngine.setIndexingOption(false);
	}
}