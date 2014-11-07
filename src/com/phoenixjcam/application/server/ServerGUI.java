package com.phoenixjcam.application.server;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerGUI
{
	// gui components  
	private JFrame frame;
	private JTextField userText;
	private JScrollPane scrollPane;
	private JTextArea textArea;

	public ServerGUI()
	{
		frame = new JFrame("Server");

		// userText = new JTextField();
		// userText.setFont(new Font("Arial", 0, 20));
		//
		// userText.setEnabled(false);
		// frame.add(userText, BorderLayout.NORTH);

		textArea = new JTextArea();
		textArea.setFont(new Font("Arial", 0, 20));


		createPopupMenu();
		scrollPane = new JScrollPane(textArea);

		frame.add(scrollPane, BorderLayout.CENTER);

		frame.setSize(600, 400);
		frame.setLocation(700, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public JFrame getFrame()
	{
		return frame;
	}

	public void setFrame(JFrame frame)
	{
		this.frame = frame;
	}

	// public JTextField getUserText()
	// {
	// return userText;
	// }
	//
	// public void setUserText(JTextField userText)
	// {
	// this.userText = userText;
	// }

	public JTextArea getTextArea()
	{
		return textArea;
	}

	public void setTextArea(JTextArea textArea)
	{
		this.textArea = textArea;
	}

	public void createPopupMenu()
	{
		JPopupMenu popup = new JPopupMenu();

		JMenuItem menuItem;
		menuItem = new JMenuItem("Clear screen");
		menuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				getTextArea().setText("");
			}
		});
		popup.add(menuItem);
		
		menuItem = new JMenuItem("Test");
		menuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Test");
				getTextArea().append("Test");
			}
		});
		popup.add(menuItem);
		
		MouseListener popupListener = new PopupListener(popup);
		getTextArea().addMouseListener(popupListener);
	}

	class PopupListener extends MouseAdapter
	{
		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu)
		{
			popup = popupMenu;
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}	
	}

}
