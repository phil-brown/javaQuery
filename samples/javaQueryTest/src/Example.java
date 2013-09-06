import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import self.philbrown.javaQuery.$;
import self.philbrown.javaQuery.AjaxOptions;
import self.philbrown.javaQuery.AjaxTask.AjaxError;
import self.philbrown.javaQuery.ColorHelper;
import self.philbrown.javaQuery.Function;
import self.philbrown.javaQuery.Log;


/**
 * Simple javaQuery example
 * @author Phil Brown
 * @since 10:10:41 AM Sep 6, 2013
 *
 */
public class Example 
{
	private static JFrame mainframe;
	private static JPanel listView;
	
	private static void createAndShowGUI()
	{
		mainframe = new JFrame("APP.NET Client");
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		$.with(mainframe).change(new Function() {
			@Override
			public void invoke($ j, Object... params)
			{
				$.ChangeEvent event = ($.ChangeEvent) params[0];
				if (event == $.ChangeEvent.COMPONENT_RESIZED)
				{
					Log.info("Resize");
				}
				
			}
		});
		
		Container contentPane = mainframe.getContentPane();
		contentPane.setLayout(new BorderLayout());
		JPanel buttonbox = new JPanel(new GridLayout(1, 2));
		JButton dblclicker = new JButton("Double click to refresh");
		dblclicker.setName("btn_dblclick");
		JButton clicker = new JButton("Click once to refresh");
		clicker.setName("btn_click");
		buttonbox.add(dblclicker);
		buttonbox.add(clicker);
		contentPane.add(buttonbox, BorderLayout.PAGE_START);
		listView = new JPanel();
		listView.setName("list");
		listView.setLayout(new BoxLayout(listView, BoxLayout.Y_AXIS));
		listView.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JScrollPane scrollview = new JScrollPane(listView);
		scrollview.setAutoscrolls(true);
		scrollview.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollview.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollview);
		scrollview.setPreferredSize(new Dimension(200, 200));
		
		//refresh the list.
        refresh();
        
        //Register a click event
        $.with(clicker).click(new Function() {
			@Override
			public void invoke($ droidQuery, Object... params) {
				Log.i("Example", "Refresh");
				refresh();
			}
        });
        
        $.with(dblclicker).dblclick(new Function() {
        	@Override
			public void invoke($ droidQuery, Object... params) {
				Log.i("Example", "Refresh");
				refresh();
			}
        });
		
		//show the window
		mainframe.pack();
		mainframe.setVisible(true);
	}
	
	/**
	 * Called when the Example program first starts, and handles global configurations and initializes
	 * the GUI.
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		//setup global ajax functions
		$.ajaxStart(new Function() {
        	public void invoke($ droidQuery, Object... args)
        	{
        		Log.i("Ajax Test", "Global start");
        	}
        });
        
        $.ajaxStop(new Function() {
        	public void invoke($ droidQuery, Object... args)
        	{
        		Log.i("Ajax Test", "Global stop");
        	}
        });
        
        //stop all ajax requests when the app exits
        Runtime.getRuntime().addShutdownHook(new Thread() {
        	@Override
        	public void run()
        	{
        		$.ajaxKillAll();
        	}
        });
        
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
        	public void run()
        	{
        		createAndShowGUI();
        	}
        });
	}
	
	/**
	 * Refreshes the list of cells containing App.net messages. This <em>ListView</em> is actually
	 * a <em>scrollable LinearLayout</em>, and is assembled in much the same way a layout would be
	 * made using <em>JavaScript</em>, with the <em>CSS3</em> attribute <em>overscroll-y: scroll</em>.
	 * <br>
	 * For this example, the public stream is retrieved using <em>ajax</em>, and for each message
	 * received, a new cell is created. For each cell, a new <em>ajax</em> request is started to
	 * retrieve the thumbnail image for the user. As all these events occur on a background thread, the
	 * main ScrollView is populated with cells and displayed to the user.
	 * <br>
	 * The stream <em>JSON</em> request is performed in a <em>global ajax</em> request, which will
	 * trigger the global start and stop events (which show a progress indicator, using a droidQuery
	 * extension). The image get requests are not global, so they will not trigger global events.
	 */
	public static void refresh()
	{
		$.ajax(new AjaxOptions()
				.url("https://alpha-api.app.net/stream/0/posts/stream/global")
				.dataType("json")
				.type("GET")
				.error(new Function() {
					@Override
					public void invoke($ droidQuery, Object... params) {
						AjaxError error = (AjaxError) params[0];
						Log.warn("Ajax\tError %d: %s", error.status, error.reason);
					}
				})
				.success(new Function() {
					@Override
					public void invoke($ droidQuery, Object... params) 
					{
						//Object, reason
						JSONObject json = (JSONObject) params[0];
						//String reason = (String) params[1];
						try 
						{
							Map<String, ?> map = $.map(json);
							JSONArray datas = (JSONArray) map.get("data");
							
							
							
							if (datas.length() != 0)
							{
								//clear old subviews in layout
								if (((Container) $.with(listView).view(0)).getComponentCount() > 0)
									$.with(listView).selectChildren().remove();

								//get each message infos and create a cell
								for (int i = 0; i < datas.length(); i++) 
								{
									JSONObject jdata = (JSONObject) datas.get(i);
									Map<String, ?> data = $.map(jdata);
									
									String text = data.get("text").toString();
									
									Map<String, ?> user = $.map((JSONObject) data.get("user"));
									
									String username = user.get("username").toString();
									//String avatarURL = ((JSONObject) user.get("avatar_image")).getString("url");
									
									//get Avatar image in a new task (but go ahead and create the cell for now)
									JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT));
									//$ j = $.with(cell);
									//j.width(j.parent().width());
									cell.setBackground(ColorHelper.parseColor("#333333"));
									//the text location in the cell
									JPanel body = new JPanel();
									body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
									cell.add(body);
									
									//the username
									JLabel name = new JLabel();
									name.setForeground(Color.DARK_GRAY);
									body.setAlignmentX(JLabel.CENTER_ALIGNMENT);
									name.setText(username);
									body.add(name);
									
									//the message
									JLabel message = new JLabel();
									message.setForeground(Color.BLACK);
									message.setFont(new Font("Serif", Font.PLAIN, 18));
									body.setAlignmentX(JLabel.CENTER_ALIGNMENT);
									String format = "<html><div WIDTH=%d>%s</div></html>";
									message.setText(Log.format(format, $.with(mainframe).width(), text));
									body.add(message);
									
									$.with(listView).add(cell);
									body.setMaximumSize(body.getPreferredSize());
										
								}
								listView.revalidate();
								listView.repaint();
							}
							else
							{
								Log.w("app.net client", "could not update data");
							}
						}
						catch (Throwable t) 
						{
							t.printStackTrace();
						}
					}
				}));
	}
}
