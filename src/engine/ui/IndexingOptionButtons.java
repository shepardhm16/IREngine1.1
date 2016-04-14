package engine.ui;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

public class IndexingOptionButtons {
	public static String withTitle = "With Title Indexing";
	public static String withoutTitle = "Without Title Indexing";
	
	JRadioButton[] buttons;
	
	public IndexingOptionButtons(Browser browser) {
		String[] weights = {withoutTitle,withTitle};
		ButtonGroup group = new ButtonGroup();
		buttons = new JRadioButton[weights.length];
		for(int i=0; i<weights.length;i++){
			JRadioButton btn = new JRadioButton(weights[i]);
			btn.setActionCommand(weights[i]);
			if(i==0)
					btn.setSelected(true);
			btn.addActionListener(browser);
			group.add(btn);
			buttons[i] = btn;
		}
	}
	
	public JPanel getIndexingOptions() {
		JPanel radioPanel = new JPanel(new GridLayout(1, 6));
		for(int i=0; i<buttons.length;i++)
			radioPanel.add(buttons[i]);
		radioPanel.setSize(300, 200);
		
		TitledBorder title = BorderFactory.createTitledBorder("Indexing Options");
		radioPanel.setBorder(title);
		
		return radioPanel;
	}

}
