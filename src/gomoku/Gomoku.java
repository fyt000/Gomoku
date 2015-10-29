package gomoku;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;
import java.io.*;
import java.util.*;

public class Gomoku extends JFrame implements ActionListener{

	private JMenuBar menubar; //the menu bar at the top
	private JMenu[] mainMenu={new JMenu("Game"),new JMenu("Help")};
	private JMenuItem[] subMenu1={new JMenuItem("New Game"),
			new JMenuItem("Save Game"),new JMenuItem("Load Game"),
			new JMenuItem("Save Replay"),new JMenuItem("Replay Game"),
			new JMenuItem("Setting"),new JMenuItem("Exit")};
	private JMenuItem[] subMenu2={new JMenuItem("How to play"),
			new JMenuItem("About"),new JMenuItem("Backtrack")};
	private static final int WIDTH=430;
	private static final int HEIGHT=530;
	private Wuziqi gamePanel; //panel for the actual game
	private JPanel botPanelPanel; //bottom panel to display information or waiting message
	private InformationPanel bottomPanel;//the information
	private int humanVsAi=1;//is human vs Ai? 1 : 0
	private int difficulty=2;//difficulty (1~3)
	private int goFirst=1;//goFirst?1:0
	boolean playMusic; //flag for playing music or not
	//URL musicFile = getClass().getResource("music.wav"); //the URL to music
	// AudioClip backGroundMusic = java.applet.Applet.newAudioClip(musicFile); //find the music
	//Menuitemclicked menuAction=new Menuitemclicked();
	Mouseclicked mouseclicked=new Mouseclicked();

	public Gomoku(){
		setTitle("Gomoku++");
		setSize(WIDTH,HEIGHT);
		setResizable(false);
		setLayout(new BorderLayout());

		//to add the menu and add listener to all of them
		menubar=new JMenuBar();
		subMenu1[0].setActionCommand("new");
		subMenu1[1].setActionCommand("save");
		subMenu1[2].setActionCommand("load");
		subMenu1[3].setActionCommand("saveRep");
		subMenu1[4].setActionCommand("replay");
		subMenu1[5].setActionCommand("setting");
		subMenu1[6].setActionCommand("exit");
		subMenu2[0].setActionCommand("how");
		subMenu2[1].setActionCommand("about");
		subMenu2[2].setActionCommand("backtrack");
		for (int i=0;i<7;i++){ //add subMenu to mainMenu
			mainMenu[0].add(subMenu1[i]);
			subMenu1[i].addActionListener(this);
		}
		for (int i=0;i<2;i++){ //change to i < 3 to add backtrack function
			mainMenu[1].add(subMenu2[i]);
			subMenu2[i].addActionListener(this);
		}
		for (int i=0;i<2;i++)
			menubar.add(mainMenu[i]);
		setJMenuBar(menubar); //set menu bar to the frame

		setLocation(
				Toolkit.getDefaultToolkit().getScreenSize().width/2-WIDTH/2,
				Toolkit.getDefaultToolkit().getScreenSize().height/2-HEIGHT/2); //set the frame in the middle of the secreen
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit on close

		try{
			Scanner s=new Scanner(new FileInputStream("config.ini"));//read the setting from file, only read info about music
			s.next(); //read until music
			s.nextInt();
			s.nextInt();
			s.nextInt();
			s.next();
			int music=s.nextInt();
			if (music==1)
				playMusic=true;
			else
				playMusic=false;
		}
		catch (IOException|NoSuchElementException ee){
			playMusic=true; //default true
		}
		playMusic=false; //disable music
		//if (playMusic) //loop if true
		//backGroundMusic.loop();

		botPanelPanel=new JPanel();
		bottomPanel=new InformationPanel(humanVsAi,difficulty,goFirst); //information panel
		gamePanel=new Wuziqi(humanVsAi,difficulty,goFirst,bottomPanel); //the actual game
		botPanelPanel.add(bottomPanel);
		Container p=getContentPane();
		//reset the actionlistener for retract button
		bottomPanel.retractButton.addActionListener(this);
		bottomPanel.retractButton.setActionCommand("retract");
		p.add(gamePanel,"Center");
		p.add(botPanelPanel,"South");
		//instNewGame();
		//gamePanel = new Wuziqi(humanVsAi,difficulty,goFirst,bottomPanel);
		gamePanel.addMouseListener(mouseclicked); //reAdd mouse listener
		setVisible(true);
	}

	/*public void paint(Graphics g){
	gamePanel.paint(g);
	bottomPanel.paint(g);
	}*/

	public void instNewGame(){//start new game
		NewGameFrame newWindow=new NewGameFrame(null,"New Game");//create NewGameFrame to setup the settings
		newWindow.setSize(430,125); //size
		newWindow.setLocation(
				Toolkit.getDefaultToolkit().getScreenSize().width/2-410/2,
				Toolkit.getDefaultToolkit().getScreenSize().height/2-125/2);
		newWindow.setVisible(true);
		if (newWindow.ok){ //if user clicked button
			humanVsAi=newWindow.hVsAi; //read all the information
			difficulty=newWindow.difficulty;
			goFirst=newWindow.first;
			newWindow.dispose(); //dispose dialog
		}
		System.out.println(humanVsAi+" "+difficulty+" "+goFirst);
		Container p=getContentPane();
		gamePanel.setVisible(false);//close the old one
		bottomPanel=new InformationPanel(humanVsAi,difficulty,goFirst); //add these panels to the frame
		gamePanel=new Wuziqi(humanVsAi,difficulty,goFirst,bottomPanel);
		//bottomPanel = new InformationPanel(humanVsAi,difficulty);
		bottomPanel.clear(humanVsAi,difficulty,goFirst);//clear the old information
		//readd listener
		bottomPanel.retractButton.addActionListener(this);
		bottomPanel.retractButton.setActionCommand("retract");
		botPanelPanel.removeAll();
		botPanelPanel.add(bottomPanel);
		//botPanelPanel.add(new WaitPanel());
		//update the graphics
		botPanelPanel.validate();
		botPanelPanel.revalidate();
		botPanelPanel.repaint();
		bottomPanel.repaint();
		p.add(gamePanel,"Center");
		p.add(botPanelPanel,"South");
		gamePanel.addMouseListener(mouseclicked);
		repaint();
	}

	public String myFileChooser(String title,String fileTypeName,
			String fileType,boolean openSave){ //steps needed to choose a file
		JFileChooser chooser=new JFileChooser(); //create file chooser object
		chooser.setCurrentDirectory(new java.io.File("."));//set to current directory
		chooser.setDialogTitle(title);
		FileNameExtensionFilter filter=new FileNameExtensionFilter(
				fileTypeName,fileType);
		chooser.setFileFilter(filter);
		int returnVal;
		if (openSave) //if true, open; otherwise, save
			returnVal=chooser.showOpenDialog(Gomoku.this);//show dialog to open
		else
			returnVal=chooser.showSaveDialog(Gomoku.this);//show dialog to save
		String re;
		if (returnVal==JFileChooser.APPROVE_OPTION){//if a file has choosen
			re=chooser.getSelectedFile().getPath(); //get its path name (do not know how to use the file directly)
		}
		else
			return null;
		if (!openSave) //save, add the extension
			re+=("."+fileType);
		return re;
	}

	class Mouseclicked extends MouseAdapter{ //read information about mouse
		public void mouseClicked(MouseEvent evt){
			if (gamePanel.wait||gamePanel.replay||gamePanel.isOver())//computer thinking
				return;
			int mouseX=evt.getX(); //mouse's coordinates
			int mouseY=evt.getY();
			//System.out.println(mouseX+" "+mouseY);
			for (int i=0;i<=14;i++)
				for (int j=0;j<=14;j++){
					if (mouseX>=14+i*28-10&&mouseX<=14+i*28+10
							&&mouseY>=14+j*28-10&&mouseY<=14+j*28+10){
						if (gamePanel.getBoard(i,j)==-1){//no one placed any stone on this pos yet 
							System.out.println("x: "+i+" y: "+j);
							gamePanel.placePiece(i,j,gamePanel.getCurTurn());//place the stone
							if (gamePanel.isVsAI()&&!gamePanel.isOver()){//if vs ai and game not end
								botPanelPanel.removeAll(); //clear the panel
								botPanelPanel
										.paint(botPanelPanel.getGraphics()); //display nothing.....
								WaitPanel x=new WaitPanel();
								botPanelPanel.add(x);
								x.paint(botPanelPanel.getGraphics()); //force paint waiting message
								gamePanel.placePieceAI();//ai place piece
								botPanelPanel.removeAll(); //clear panel again
								botPanelPanel.add(bottomPanel); //display the information
								botPanelPanel.revalidate();
							}
							return;
						}
						else{
							System.out.println("clicked x: "+i+" y: "+j);
						}
					}
				}
		}
	}

	public void actionPerformed(ActionEvent e){ //where actions happen
		String cmdx=e.getActionCommand();
		if (cmdx.equals("retract")){
			System.out.println("retract called");
			if (!gamePanel.isOver()&&!gamePanel.replay) //can only when game is not over and not replay
				gamePanel.retract();
			return;
		}
		JMenuItem target=(JMenuItem)e.getSource(); //get targeted menu item
		String cmd=target.getActionCommand(); //store the commad in cmd
		if (cmd.equals("new")){ //instantiate new game
			System.out.println("New game");
			instNewGame();
		}
		if (cmd.equals("save")){ //save game
			String fileChose=myFileChooser("Save Game","Gomoku Save File",
					"gsav",false);
			if (fileChose!=null){//if a file has choosen
				System.out.println("You chose to open this file: "+fileChose);
				gamePanel.replayWriter(fileChose);
			}

		}
		if (cmd.equals("saveRep")){ //save replay
			String fileChose=myFileChooser("Save Replay","Gomoku Replay File",
					"grep",false);
			if (fileChose!=null){//if a file has choosen
				System.out.println("You chose to open this file: "+fileChose);
				gamePanel.replayWriter(fileChose); //actually same as save game
			}

		}
		if (cmd.equals("load")){ //load game
			System.out.println("load");
			String fileChose=myFileChooser("Load Game","Gomoku Save File",
					"gsav",true);
			if (fileChose!=null){//if a file has choosen
				System.out.println("You chose to open this file: "+fileChose);
				gamePanel.loadGame(fileChose);
				//has to readd listener everytime new game instantiates
				bottomPanel.retractButton.setActionCommand("retract");
				bottomPanel.retractButton.addActionListener(this);
			}
		}
		if (cmd.equals("replay")){
			System.out.println("replay");
			String fileChose=myFileChooser("Open Replay","Gomoku Replay File",
					"grep",true);
			if (fileChose!=null){//if a file has choosen
				System.out.println("You chose to open this file: "+fileChose);
				gamePanel.doReplay(fileChose); //replay file
				System.out.println("replay finished");
				JOptionPane.showMessageDialog(null,"Replay has finished");
			}
			//bottomPanel = new InformationPanel(humanVsAi,difficulty);
			//getContentPane().add(bottomPanel,BorderLayout.SOUTH);
		}
		if (cmd.equals("setting")){ //read the setting
			System.out.println("setting");
			SettingFrame tempFrame=new SettingFrame(null,"Settings"); //tempFrame for setting
			if (tempFrame.ok==1){ //if tempFrame oked
				gamePanel.readSetting();
				//backGroundMusic.stop(); //stop the music every time setting is called
				//if (tempFrame.music==1) //restart music if required
				//backGroundMusic.loop();
				//read other settings
				gamePanel.boardColour=new Color(tempFrame.R,tempFrame.G,
						tempFrame.B);
				gamePanel.style=tempFrame.style;
				tempFrame.dispose();
			}
			gamePanel.repaint();
			//call setting frame
		}
		if (cmd.equals("exit")){
			System.out.println("exit");
			System.exit(1); //exit
		}
		if (cmd.equals("backtrack")){ //debuging only

			BackTrackDialog d=new BackTrackDialog(null,"debug");
			d.setVisible(true);
			int x,y;
			if (d.ok){
				x=d.x;
				y=d.y;
				int ty1=gamePanel.findType(x,y,0);
				int ty2=gamePanel.findType(x,y,1);
				System.out.println(x+" "+y);
				System.out.println("Piece colour: "+gamePanel.getBoard(x,y)
						+" pt0: "+ty1+" pt1: "+ty2);
				for (int i=1;i<=4;i++){
					int s[]=gamePanel.pieceCounterX(x,y,i,0);
					System.out.println("Length0: "+s[0]+" Close0: "+s[1]);
					int ss[]=gamePanel.pieceCounterX(x,y,i,1);
					System.out.println("Length1: "+ss[0]+" Close1: "+ss[1]);
				}

			}
			d.dispose();

			System.out.println("Eval: "+gamePanel.evalBoard(0));
			System.out.println("Eval: "+gamePanel.evalBoard(1));
		}
		if (cmd.equals("how")){ //how to play part
			JOptionPane
					.showMessageDialog(
							null,
							"\t                                                      "
									+"How to Play\n"
									+"Black plays first, and players alternate in placing a stone of their color on an \n"
									+"empty intersection. The winner is the first player to get an unbroken row of five \n"
									+"stone horizontally vertically, or diagonally.\n");
		}
		if (cmd.equals("about")){ //about message
			JOptionPane
					.showMessageDialog(null,
							"Gomoku++ V1.0\nImplmented by Felix Tian\nContact me: three0s@live.com");
		}

	}

	public static void main(String[] args){
		Gomoku theGame=new Gomoku();
	}

}
