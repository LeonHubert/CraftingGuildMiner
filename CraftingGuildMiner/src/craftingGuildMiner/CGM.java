package craftingGuildMiner;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;

import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.PaintListener;
import org.powerbot.script.MessageListener;

@Script.Manifest(name = "Crafting Guild Miner", description = "Mines at the Crafting Guild", properties = "topic=1354351;client = 4;")
public class CGM extends PollingScript<ClientContext> implements PaintListener, MessageListener {
	
	private boolean guiDone = false;
	private int oresMined = 0;
	private static long startTime;
	private static int startXP, startLvl;
	
	private final Color color1 = new Color(205,133,63, 220);
	private final Color color2 = new Color(0,0,0, 255);
	private final Font font1 = new Font("Calibri", 1, 15);
	
	@SuppressWarnings("rawtypes")
	private List<Task> taskList = new ArrayList<Task>();

	@Override
	public void start() 
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI gui = new GUI();
				gui.setVisible(true);
			}
		});
		
		taskList.addAll(Arrays.asList(
			new mine(ctx),
			new bank(ctx),
			new walk(ctx),
			new antiBan(ctx)
		));
		
		startTime = System.currentTimeMillis();
		startXP = ctx.skills.experience(Constants.SKILLS_MINING);
		startLvl = ctx.skills.level(Constants.SKILLS_MINING);
	}
	
	@Override
	public void repaint(Graphics g) 
	{
		int hours = 0, minutes = 0, seconds = 0, xpTillLevel = 0, totalXpGained = 0;
		long totalRunTime = System.currentTimeMillis() - startTime;
		
		totalXpGained += (ctx.skills.experience(Constants.SKILLS_MINING) - startXP);
		
		while(totalRunTime >= 3600000)
		{
			hours++;
			totalRunTime -= 3600000;
		}
		while(totalRunTime >= 60000) 
		{
			minutes++;
			totalRunTime -= 60000;
		}
		while(totalRunTime >= 1000) 
		{
			seconds++;
			totalRunTime -= 1000;
		}
		
		xpTillLevel = (ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_MINING) + 1)) - ctx.skills.experience(Constants.SKILLS_MINING);
		
		String runTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		
		g.setColor(Color.RED);
		Graphics2D g2 = (Graphics2D) g;
	    g2.setStroke(new BasicStroke(2));
		g2.drawLine(ctx.input.getLocation().x-10, ctx.input.getLocation().y, ctx.input.getLocation().x+10, ctx.input.getLocation().y);
		g2.drawLine(ctx.input.getLocation().x, ctx.input.getLocation().y-10, ctx.input.getLocation().x, ctx.input.getLocation().y+10);
		g.setColor(color1);
		g.fillRect(30, 30, 160, 110);
		g.setColor(color2);
		g.drawRect(30, 30, 160, 110);
		g.setFont(font1);
		g.drawString("Time running: " + runTime, 35, 50);
		g.drawString("Current level: " + ctx.skills.level(Constants.SKILLS_MINING) + " (+" + (ctx.skills.level(Constants.SKILLS_MINING) - startLvl) + ")", 35, 70);
		g.drawString("Total xp gained: " + totalXpGained, 35, 90);
		g.drawString("Total ores mined: " + oresMined, 35, 110);
		g.drawString("Xp till level up: " + xpTillLevel, 35, 130);
	}
	
	@Override
	public void messaged(MessageEvent msg) {
		if(msg.toString().contains("You manage to mine some"))
		{
			oresMined++;
		}
	}

	@Override
	public void poll() {
		if(guiDone)
		{
			for (Task<?> task : taskList) {
	            if (task.activate()) {
	                task.execute();
	            }
	        }
		}
		else Condition.sleep(100);
	}
	
	@SuppressWarnings("serial")
	public class GUI extends JFrame {
		public GUI() {
			initComponents();
		}

		private void button1ActionPerformed(ActionEvent e) {
			
			mine.targetOreIndex = comboBox1.getSelectedIndex();
			guiDone = true;
			this.dispose();
		}

		private void initComponents() {
			comboBox1 = new JComboBox<>();
			label1 = new JLabel();
			button1 = new JButton();
			label2 = new JLabel();

			//======== this ========
			setTitle("CGM");
			setResizable(false);
			setFont(new Font("Calibri", Font.PLAIN, 14));
			Container contentPane = getContentPane();

			//---- comboBox1 ----
			comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
				"Gold",
				"Silver",
				"Clay"
			}));

			//---- label1 ----
			label1.setText("I want to mine");

			//---- button1 ----
			button1.setText("Start");
			button1.addActionListener(e -> button1ActionPerformed(e));

			//---- label2 ----
			label2.setText("Wear a Brown Apron");
			label2.setFont(new Font("Tahoma", Font.BOLD, 11));

			GroupLayout contentPaneLayout = new GroupLayout(contentPane);
			contentPane.setLayout(contentPaneLayout);
			contentPaneLayout.setHorizontalGroup(
				contentPaneLayout.createParallelGroup()
					.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
						.addContainerGap(32, Short.MAX_VALUE)
						.addGroup(contentPaneLayout.createParallelGroup()
							.addGroup(contentPaneLayout.createSequentialGroup()
								.addComponent(label1, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(comboBox1, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE))
							.addGroup(contentPaneLayout.createSequentialGroup()
								.addGap(10, 10, 10)
								.addGroup(contentPaneLayout.createParallelGroup()
									.addGroup(contentPaneLayout.createSequentialGroup()
										.addGap(10, 10, 10)
										.addComponent(button1, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE))
									.addComponent(label2, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE))))
						.addGap(30, 30, 30))
			);
			contentPaneLayout.setVerticalGroup(
				contentPaneLayout.createParallelGroup()
					.addGroup(contentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(label1)
							.addComponent(comboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(label2)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(button1)
						.addContainerGap(12, Short.MAX_VALUE))
			);
			pack();
			setLocationRelativeTo(getOwner());
		}
		private JComboBox<String> comboBox1;
		private JLabel label1;
		private JButton button1;
		private JLabel label2;
	}
}