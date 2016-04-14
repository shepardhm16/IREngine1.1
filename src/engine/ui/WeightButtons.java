package engine.ui;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

public class WeightButtons {
	public static String booleanWeight = "Boolean";
	public static String tfWeight = "TF";
	public static String idfWeight = "IDF";
	public static String tfidfWeight = "TF/IDF";
	public static String bm11Weight = "BM11";
	public static String bm15Weight = "BM15";
	public static String bm25Weight = "BM25";
	
	JRadioButton[] buttons;
	
	public WeightButtons(Browser browser) {
		String[] weights = {booleanWeight, tfWeight, idfWeight, tfidfWeight, bm11Weight, bm15Weight, bm25Weight};
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
	
	public JPanel getWeightButtons() {
		JPanel radioPanel = new JPanel(new GridLayout(1, 6));
		for(int i=0; i<buttons.length;i++)
			radioPanel.add(buttons[i]);
		radioPanel.setSize(300, 200);
		TitledBorder title = BorderFactory.createTitledBorder("Weighting Options");
		radioPanel.setBorder(title);
		return radioPanel;
	}

}
