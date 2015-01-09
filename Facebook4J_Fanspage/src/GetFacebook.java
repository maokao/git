import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollBar;

import facebook4j.Comment;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Friend;
import facebook4j.Like;
import facebook4j.PagableList;
import facebook4j.Paging;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.User;
import facebook4j.auth.AccessToken;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GetFacebook extends JFrame {

	private JPanel contentPane;
	private JTextField tF_accesstoken;
	private JTextField tF_FansPageID;
	private JTextField tF_From;
	public static JTextArea memo = new JTextArea();
	static Date d1 = null;
	static java.sql.Date d3 = null;
	private static JTextField tFPost;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GetFacebook frame = new GetFacebook();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GetFacebook() {
		setTitle("Facebook spider ");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 498, 382);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Access Token:");
		lblNewLabel.setBounds(21, 19, 87, 15);
		contentPane.add(lblNewLabel);

		tF_accesstoken = new JTextField();
		tF_accesstoken.setBounds(118, 13, 328, 21);
		contentPane.add(tF_accesstoken);
		tF_accesstoken.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("FansPage ID:");
		lblNewLabel_1.setBounds(21, 62, 87, 15);
		contentPane.add(lblNewLabel_1);

		tF_FansPageID = new JTextField();
		tF_FansPageID.setBounds(118, 56, 96, 21);
		contentPane.add(tF_FansPageID);
		tF_FansPageID.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("From:(Date)");
		lblNewLabel_2.setBounds(21, 103, 87, 15);
		contentPane.add(lblNewLabel_2);

		tF_From = new JTextField();
		tF_From.setText("2014-07-01");
		tF_From.setBounds(118, 97, 96, 21);
		contentPane.add(tF_From);
		tF_From.setColumns(10);

		JButton Btn_run = new JButton("run");
		Btn_run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String accesstoken = tF_accesstoken.getText();
				String FP_id = tF_FansPageID.getText();
				String Fromdate = tF_From.getText();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

				try {
					d1 = formatter.parse(Fromdate);
					d3 = new java.sql.Date(d1.getTime());

				} catch (ParseException e) {
					System.out.println("unparseable using " + formatter);
				}

				Facebook facebook;
				facebook = new FacebookFactory().getInstance();
				facebook.setOAuthAppId("", "");
				facebook.setOAuthAccessToken(new AccessToken(accesstoken));
				try {
					getFanPages_Post(facebook, FP_id);
				} catch (FacebookException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("finish");
			}
		});
		Btn_run.setBounds(385, 99, 87, 23);
		contentPane.add(Btn_run);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 188, 462, 148);
		contentPane.add(scrollPane);

		// JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(memo);

		JLabel lblyyyymmdd = new JLabel("(YYYY-MM-DD)");
		lblyyyymmdd.setBounds(224, 103, 123, 15);
		contentPane.add(lblyyyymmdd);

		JLabel lblFrompost = new JLabel("From:(Post)");
		lblFrompost.setBounds(21, 148, 87, 15);
		contentPane.add(lblFrompost);

		tFPost = new JTextField();
		tFPost.setText("0");
		tFPost.setBounds(118, 145, 96, 21);
		contentPane.add(tFPost);
		tFPost.setColumns(10);
	}

	// 取得粉絲團po文與按讚的人
	public static void getFanPages_Post(Facebook facebook, String page_id)
			throws FacebookException, IOException {
		System.out.println("start get FansPage's Post!");
		memo.append("start get FansPage's Post!\n");
		// showtext a = new showtext(memo);
		// a.insert_text("start get FansPage's Post!\n");
		// a.start();
		String tempFrompost = tFPost.getText();
		int Frompost = Integer.parseInt(tempFrompost);

		String path = "FansPage_postlike_" + tempFrompost + ".txt";
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));

		String path2 = "FansPage_comments" + tempFrompost + ".txt";
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(path2));

		String path3 = "FansPage_info" + tempFrompost + ".txt";
		BufferedWriter writer3 = new BufferedWriter(new FileWriter(path3));

		ResponseList<Post> feeds = facebook.getFeed(page_id,
				new Reading().limit(50));
		Paging<Post> paging2 = feeds.getPaging();
		int post_count = 0;
		int already_end = 0;
		//User like_user = null;

		while (paging2 != null) {

			// For all 50 feeds...
			if (already_end == 1) {
				break;
			}
			for (int i = 0; i < feeds.size(); i++) {

				// Get post.
				Post post = feeds.get(i);
				// Get (string) message.
				String message = post.getMessage();
				// Print out the message.
				System.out.println("第" + post_count + "篇:");
				System.out.println(message);

				// Get more stuff...
				PagableList<Comment> comments = post.getComments();
				String date = post.getCreatedTime().toString();
				Date date2 = post.getCreatedTime();
				String name = post.getFrom().getName();
				String id = post.getId();
				String cate = post.getType();
				String sharecount = post.getSharesCount().toString();
				
				
				//Date d2 = null;
				//java.sql.Date d4 = null;
				Date d5 = null;
				String date_t=null;
				// java.sql.Date d6 = null;

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat formatter3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
				// DateFormat df = DateFormat.getDateInstance();
				try {
					//d2 = formatter.parse(date);
					d5 = formatter3.parse(date);
					//d4 = new java.sql.Date(d2.getTime());
					date_t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date2);

				} catch (ParseException e) {
					System.out.println("unparseable using " + formatter);
				}

				if (d3.after(date2)) {
					already_end = 1;
					break;
				}
				// System.out.println(name);
				// System.out.println(id);
				System.out.println(date);

				String line2 = "";
				line2 = name + "\t" + id + "\t" + post_count + "\t" + date_t + "\t" + date + "\t" + cate + "\t" + sharecount;
				System.out.println(line2);
				writer3.write(line2);
				writer3.newLine();

				// insert("第"+post_count+"篇:"+date+"\n");

				post_count++;
				int comments_count = 0;
				
				if (post_count >= Frompost) {
					
					// comments----------------------------------------------------------------------------
					Paging<Comment> paging5 = comments.getPaging();
					while (paging5 != null) {
						for (int j = 0; j < comments.size(); j++) {

							String comments_id = comments.get(j).getId();
							String comments_message = comments.get(j)
									.getMessage();
							String comments_userid = comments.get(j).getFrom()
									.getId();
							String comments_username = comments.get(j)
									.getFrom().getName();

							// System.out.println("第"+j+"個回文:"+comments_id+","+comments_message+","+comments_userid+","+comments_username);
							// Page thisPage = facebook.getPage(FanPage_id);
							String line1 = "";
							line1 = name + "\t" + id + "\t" + post_count + "\t"
									+ comments_count + "\t" + date_t + "\t"
									+ comments_userid + "\t"
									+ comments_username + "\t" + comments_id;
							writer2.write(line1);
							writer2.newLine();
							comments_count++;
						}
						comments = facebook.fetchNext(paging5);
						// System.out.println(paging1);
						if (comments == null)
							paging5 = null;
						else
							paging5 = comments.getPaging();
					}

					int like_count = 0;
					// String tmp_date = null;
					memo.append("第" + post_count + "篇:" + date + "\n");
					// insert_text("第"+post_count+"篇:"+date+"\n");
					// memo.paintImmediately(memo.getBounds());

					// post_like----------------------------------------------------------------------------
					PagableList<Like> post_like = post.getLikes();
					// int post_like_count=post.getLikes().getCount();
					Paging<Like> paging1 = post_like.getPaging();
					while (paging1 != null) {
						for (int j = 0; j < post_like.size(); j++) {

							String post_user_id = post_like.get(j).getId();
							String post_name = post_like.get(j).getName();
							String post_cate = post_like.get(j).getCategory();
							//Date post_time = post_like.get(j).getCreatedTime();
							//like_user = facebook.getUser(post_user_id);
							//String like_user_sex = like_user.getGender();
							//Date like_user_update_time =like_user.getUpdatedTime();
							//String like_user_update_time2=null;
							//ResponseList<Like> FanPages = facebook.getUserLikes(post_user_id);
							//ResponseList<Friend> FanPages = facebook.getFriends(post_user_id);
							//System.out.println(post_user_id+":"+FanPages.size());

							//like_user_update_time2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(like_user_update_time);
							// System.out.println("第"+like_count+"個案讚:"+post_user_id+","+post_name+","+post_cate);
							// Page thisPage = facebook.getPage(FanPage_id);
							String line = "";
							line = name + "\t" + id + "\t" + like_count + "\t"
									+ post_count + "\t" + date_t + "\t"
									+ post_user_id + "\t" + post_name + "\t"
									+ post_cate;
							//System.out.println(line);
							writer.write(line);
							writer.newLine();
							like_count++;
						}
						post_like = facebook.fetchNext(paging1);
						// System.out.println(paging1);
						if (post_like == null)
							paging1 = null;
						else
							paging1 = post_like.getPaging();
					}
				}

			}

			feeds = facebook.fetchNext(paging2);
			// System.out.println(paging1);
			if (feeds == null)
				paging2 = null;
			else
				paging2 = feeds.getPaging();
		}
		writer.close();
		writer2.close();
		writer3.close();
		memo.append("finish!\n");
	}
}

class showtext extends Thread {
	String text;
	JTextArea memo1;

	public showtext(JTextArea memo) {
		memo1 = memo;
	}

	public void run() {
		memo1.append(text + "\n");
	}

	public void insert_text(String x) {
		text = x;
	}

}
