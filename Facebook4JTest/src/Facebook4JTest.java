import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import Jama.Matrix;
import facebook4j.Checkin;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Friend;
import facebook4j.Friendlist;
import facebook4j.Like;
import facebook4j.Page;
import facebook4j.Paging;
import facebook4j.Place;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.User;
import facebook4j.auth.AccessToken;


public class Facebook4JTest {

	private static List<Friend> getFriends(Facebook facebook) throws FacebookException{
		System.out.println("start get friends");
		List<Friend> friends = facebook.getFriends();
		System.out.println(friends.size());
		
		/*
		//friends.clear();	//之後用完要刪掉
		Reading testR = new Reading();
		testR.limit(3000);
		
		ResponseList<Friendlist> friendslist = facebook.getFriendlists(testR);
		System.out.println(friendslist.size());
				
		int count=friends.size();
		for (Friendlist friendlist : friendslist) {
			System.out.println(friendlist.getName()+":");
			ResponseList<Friend> friends1=  facebook.getFriendlistMembers(friendlist.getId());
			friends.addAll(friends1);
			for (Friend friend : friends1) {
				count++;
				System.out.println(count+":"+friend.getName());				
			}
		}
		System.out.println("total:"+friends.size());	
		*/
		return friends;
	}
	
	//取得朋友的其他資訊
	private static void getFriendInformation(Facebook facebook, List<Friend> friends) throws IOException, FacebookException{
		System.out.println("start get information of friends");
		//HashMap<String,String> friends_sex_index = new HashMap<String,String>();

		int po_id=0;
		int temp_count=0;

		Reading testR = new Reading();
		testR.limit(300);

		String path="friend_Inf_index1.csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));

		for (Friend friend : friends) {
			User myFriends = facebook.getUser(friend.getId(),testR);
			
			String line = "";
			line = po_id + "\t" + myFriends.getName() + "\t" + friend.getId() + "\t" + myFriends.getUsername() + "\t" + myFriends.getGender() + "\t" + myFriends.getRelationshipStatus() + "\t" + myFriends.getBirthday() + "\t" + myFriends.getPolitical();
			writer.write(line);
			writer.newLine();
			
			po_id++;
				
		}
		
		writer.close();		


	}
	
	/*
	private static List<Like> getFriend_FanPages(Facebook facebook, List<Friend> friends) throws FacebookException{
		System.out.println("start getUserLikes");
		List<Like> FanPages = facebook.getUserLikes();
		return FanPages;
	}
	*/
	
	//取得粉絲團
	private static void getFanPagesIndex(Facebook facebook, List<Friend> friends) throws FacebookException, IOException{
		System.out.println("start getUserLikes");
		int po_id=0;
		int temp_count=0;

		Reading testR = new Reading();
		testR.limit(300);

		String path="friend_FP_index1.csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));

		for (Friend friend : friends) {
			if(temp_count>=566){
			  ResponseList<Like> FanPages = facebook.getUserLikes(friend.getId(),testR);
			  String friend_name=friend.getName();
			  String friend_id=friend.getId();
			  System.out.println(friend_name+":"+temp_count);
			  
			  Paging<Like> paging1 = FanPages.getPaging();
			  while(paging1!=null)
			  {
				  //String friend_name=friend.getName();
				  //String friend_id=friend.getId();

				  //System.out.println(friend_name+":"+FanPages.size());
				  for(int j = 0 ; j < FanPages.size() ; j++){
						
					    String FanPage_id =FanPages.get(j).getId();
					    Page thisPage = facebook.getPage(FanPage_id);
					    String line = "";
						line = po_id + "\t" + friend_name + "\t" + friend_id + "\t" + FanPages.get(j).getName() + "\t" + FanPage_id + "\t" + FanPages.get(j).getCategory() + "\t" + thisPage.getLikes();
						writer.write(line);
						writer.newLine();
						
						po_id++;
				  }
				  
				  
				  FanPages = facebook.fetchNext(paging1);
				  //System.out.println(paging1);
				  if(FanPages==null)
					  paging1 = null;
				  else
					  paging1 = FanPages.getPaging();
			  }
		    }
			  
			temp_count++;	
				
		}
		
		writer.close();		
	}
	
	//取得打卡資訊(已被取代，目前無用)
	private static HashMap<String,String> getCheckinIndex(Facebook facebook, List<Friend> friends) throws FacebookException{
		System.out.println("start getCheckin");
		HashMap<String,String> friends_ck_index = new HashMap<String,String>();

		for (Friend friend : friends) {
			ResponseList<Checkin> Checkins =  facebook.getCheckins(friend.getId());
			  
			  Paging<Checkin> paging1 = Checkins.getPaging();
			  while(paging1!=null)
			  {
				  String friend_name=friend.getName();

				  //System.out.println(friend_name+":"+FanPages.size());
				  for(int j = 0 ; j < Checkins.size() ; j++){
						//friends_ck_index.put(Checkins.get(j).getPlace(), friend_name);
						friends_ck_index.put(Checkins.get(j).getPlace().getName(), friend_name);
				  }
				  
				  
				  Checkins = facebook.fetchNext(paging1);
				  //System.out.println(paging1);
				  if(Checkins==null)
					  paging1 = null;
				  else
					  paging1 = Checkins.getPaging();
			  }
			  
				
			}

		return friends_ck_index;
		
	}
	
	//取得打卡資訊(替代getCheckinIndex)
	private static void getFriendCheckins(Facebook facebook, List<Friend> friends) throws FacebookException, IOException{
		System.out.println("start getCheckin");
		int checkin_id=0;
		//HashMap<String,String> friends_ck_index = new HashMap<String,String>();
		HashMap<String,Integer> friends_ck_index1 = new HashMap<String,Integer>();
		HashMap<String,Integer> friends_ck_index2 = new HashMap<String,Integer>();
		HashMap<Date,Integer> friends_ck_index3 = new HashMap<Date,Integer>();

		for (Friend friend : friends) {
			ResponseList<Checkin> Checkins =  facebook.getCheckins(friend.getId());
			  
			  Paging<Checkin> paging1 = Checkins.getPaging();
			  while(paging1!=null)
			  {
				  String friend_name=friend.getName();

				  //System.out.println(friend_name+":"+FanPages.size());
				  for(int j = 0 ; j < Checkins.size() ; j++){
						//friends_ck_index.put(Checkins.get(j).getPlace().getName(), friend_name);
					  friends_ck_index1.put(Checkins.get(j).getPlace().getName(), checkin_id);
					  friends_ck_index2.put(friend_name, checkin_id);
					  friends_ck_index3.put(Checkins.get(j).getCreatedTime(), checkin_id);
					  checkin_id++;
				  }
				  
				  
				  Checkins = facebook.fetchNext(paging1);
				  //System.out.println(paging1);
				  if(Checkins==null)
					  paging1 = null;
				  else
					  paging1 = Checkins.getPaging();
			  }
				
		}
		
		String path="friend_CK_index1.csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		for(Entry<String, Integer> index : friends_ck_index1.entrySet())
		{
			String line = "";
			line = index.getValue() + "," + index.getKey();
			writer.write(line);
			writer.newLine();
		}
		writer.close();
		
		path="friend_CK_index2.csv";
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(path));
		for(Entry<String, Integer> index : friends_ck_index2.entrySet())
		{
			String line = "";
			line = index.getValue() + "," + index.getKey();
			writer2.write(line);
			writer2.newLine();
		}
		writer2.close();
		
		path="friend_CK_index3.csv";
		BufferedWriter writer3 = new BufferedWriter(new FileWriter(path));
		for(Entry<Date, Integer> index : friends_ck_index3.entrySet())
		{
			String line = "";
			line = index.getValue() + "," + index.getKey();
			writer3.write(line);
			writer3.newLine();
		}
		writer3.close();

		
	}
	
	//取得po文資訊
	private static void getFriendPosts(Facebook facebook, List<Friend> friends) throws FacebookException, IOException, ParseException{
		System.out.println("start get Posts");
		int po_id=0;
		int temp_count=0;
		//HashMap<String,String> friends_ck_index = new HashMap<String,String>();
		//HashMap<String,Integer> friends_po_index1 = new HashMap<String,Integer>();
		//HashMap<Place,Integer> friends_po_index1 = new HashMap<Place,Integer>();
		//HashMap<String,Integer> friends_po_index2 = new HashMap<String,Integer>();
		//HashMap<Date,Integer> friends_po_index3 = new HashMap<Date,Integer>();
		//HashMap<String,Integer> friends_po_index4 = new HashMap<String,Integer>();
		
		Reading testR = new Reading();

		//SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		//  利用 DateFormat 來parse 日期的字串
		//DateFormat df = DateFormat.getDateInstance();
		//Date sincedate = df.parse("2013/01/01");
		//Date untildate = df.parse("2013/01/31");
		//testR.offset(50);
		testR.limit(300);
		//testR.since(sincedate); 
		//testR.until(untildate); 
		//testR.since("1 May 2013");
		//testR.until("now"); 
		String path="friend_PO_index1.csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		path="friend_PO_index2.csv";
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(path));

		for (Friend friend : friends) {

		  if(temp_count>=101){
			ResponseList<Post> Posts = facebook.getPosts(friend.getId(),testR);
			  
			  Paging<Post> paging1 = Posts.getPaging();
			  String friend_name=friend.getName();
			  String friend_user_name=friend.getUsername();
			  System.out.println(friend_name+":"+temp_count);
			  while(paging1!=null)
			  {
				  

				  //System.out.println(friend_name+":"+Posts.size());
				  for(int j = 0 ; j < Posts.size() ; j++){
					  Post tempPost=Posts.get(j);
					  if(tempPost==null)
						  break;
					  if(tempPost!=null&&tempPost.getPlace()!=null){
						  //System.out.println(Posts.get(j).getPlace().getName());
					  
					  String line = "";
					  line = po_id + "\t" + friend_name + "\t" + friend_user_name + "\t" + tempPost.getPlace().getName() + "\t" + tempPost.getPlace().getLocation().getCity()+ "\t" + tempPost.getPlace().getLocation().getLatitude()+ "\t" + tempPost.getPlace().getLocation().getLongitude() + "\t" + tempPost.getId() + "\t"+ tempPost.getCreatedTime();
					  writer.write(line);
					  writer.newLine();
					  
					  /*//取得文章內容
					  line = "";
					  line = po_id + "\t" + tempPost.getMessage();
					  writer2.write(line);
					  writer2.newLine();
					  */
					  
					  //friends_po_index1.put(tempPost.getMessage(), po_id); 
					  //friends_po_index2.put(friend_name, po_id);
					  //friends_po_index3.put(tempPost.getCreatedTime(), po_id);
					  //friends_po_index4.put(tempPost.getPlace().getName(), po_id);

					  po_id++;
					  }
				  }
				  
				  
				  Posts = facebook.fetchNext(paging1);
				  
				  //System.out.println(paging1);
				  if(Posts==null)
					  paging1 = null;
				  else
					  paging1 = Posts.getPaging();
			  }
			}
		  //else
			  //break;
		  
		  temp_count++;	
		}
		
		writer.close();
		writer2.close();
		
		/*
		String path="friend_PO_index1.csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		for(Entry<String, Integer> index : friends_po_index1.entrySet())
		//for(Entry<Place, Integer> index : friends_po_index1.entrySet())
		{
			String line = "";
			line = index.getValue() + "," + index.getKey();
			writer.write(line);
			writer.newLine();
		}
		writer.close();
		
		path="friend_PO_index2.csv";
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(path));
		for(Entry<String, Integer> index : friends_po_index2.entrySet())
		{
			String line = "";
			line = index.getValue() + "," + index.getKey();
			writer2.write(line);
			writer2.newLine();
		}
		writer2.close();
		
		path="friend_PO_index3.csv";
		BufferedWriter writer3 = new BufferedWriter(new FileWriter(path));
		for(Entry<Date, Integer> index : friends_po_index3.entrySet())
		{
			String line = "";
			line = index.getValue() + "," + index.getKey();
			writer3.write(line);
			writer3.newLine();
		}
		writer3.close();

		path="friend_PO_index4.csv";
		BufferedWriter writer4 = new BufferedWriter(new FileWriter(path));
		for(Entry<String, Integer> index : friends_po_index4.entrySet())
		{
			String line = "";
			line = index.getValue() + "," + index.getKey();
			writer4.write(line);
			writer4.newLine();
		}
		writer4.close();
		*/
	}

	//取得自己的po文資訊
	private static void getMyPosts(Facebook facebook) throws FacebookException, IOException, ParseException{
		System.out.println("start get Posts");
		int po_id=0;
		int temp_count=0;
		
		Reading testR = new Reading();

		testR.limit(300);

		String path="friend_myPO_index1.csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		path="friend_myPO_index2.csv";
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(path));


			ResponseList<Post> Posts = facebook.getPosts("100000446944723",testR);
			String user_name=facebook.getMe().getUsername();
			  
			  Paging<Post> paging1 = Posts.getPaging();
			  String friend_name="Mao Kao";
			  System.out.println(friend_name+":"+temp_count);
			  while(paging1!=null)
			  {
				  

				  //System.out.println(friend_name+":"+Posts.size());
				  for(int j = 0 ; j < Posts.size() ; j++){
					  Post tempPost=Posts.get(j);
					  if(tempPost==null)
						  break;
					  if(tempPost!=null&&tempPost.getPlace()!=null){
						  //System.out.println(Posts.get(j).getPlace().getName());
					  
					  String line = "";
					  line = po_id + "\t" + friend_name + "\t" + "100000446944723" + "\t" + user_name + "\t" + tempPost.getPlace().getName() + "\t" + tempPost.getPlace().getLocation().getCity()+ "\t" + tempPost.getPlace().getLocation().getLatitude() + "\t" + tempPost.getPlace().getLocation().getLongitude() + "\t"+ tempPost.getId() + "\t"+ tempPost.getCreatedTime();
					  writer.write(line);
					  writer.newLine();
					  
					  /*//取得文章內容
					  line = "";
					  line = po_id + "\t" + tempPost.getMessage();
					  writer2.write(line);
					  writer2.newLine();
					  */
					  
					  po_id++;
					  
					  /*//取得按讚的朋友名字
					  List<Like> tempLike = tempPost.getLikes();
					  for (Like friendlike : tempLike) {
						  
						  line = "";
						  line = po_id + "\t" + friendlike.getName() + "\t" + friendlike.getId() + "\t" + tempPost.getPlace().getName() + "\t" + tempPost.getPlace().getLocation().getCity()+ "\t" + tempPost.getPlace().getLocation().getLatitude() + "\t" + tempPost.getPlace().getLocation().getLongitude() + "\t"+ tempPost.getCreatedTime();
						  writer.write(line);
						  writer.newLine();
						  
						  po_id++;
					  }
					  */
					  
					  }
				  }
				  
				  
				  Posts = facebook.fetchNext(paging1);
				  
				  //System.out.println(paging1);
				  if(Posts==null)
					  paging1 = null;
				  else
					  paging1 = Posts.getPaging();
			  }
		
		writer.close();
		writer2.close();
		
	}	

	
	//取得朋友烈表
	private static HashMap<String,Integer> getFriendIndex(List<Friend> friends){
		System.out.println("start get friends_index");
		HashMap<String,Integer> friends_index = new HashMap<String,Integer>();
		for(int i = 0 ; i < friends.size() ; i++)
			friends_index.put(friends.get(i).getName(), i);
		return friends_index;
	}
	
	//完全無用
	private static HashMap<String,Integer> getFanPagesIndex(List<Like> FanPages){
		System.out.println("start get UserLikes_index");
		HashMap<String,Integer> friends_ul_index = new HashMap<String,Integer>();
		for(int i = 0 ; i < FanPages.size() ; i++)
			friends_ul_index.put(FanPages.get(i).getName(), i);
		return friends_ul_index;
	}
	
	//目前沒用到
	private static HashMap<String,List<Friend>> getMutualFriends(List<Friend> friends,Facebook facebook) throws FacebookException{
		System.out.println("start get mutualfriends");
		HashMap<String,List<Friend>> mutualfriends = new HashMap<String,List<Friend>>();
		for(Friend f : friends)
			mutualfriends.put(f.getName(), facebook.getMutualFriends(f.getId()));
		return mutualfriends;
	}
	
	//目前沒用到
	private static Matrix getAdjacency_matrix(HashMap<String,List<Friend>> mutual_friends,HashMap<String,Integer>friends_index){
		System.out.println("start get adjacency_matrix");
		Matrix adjacency_matrix = new Matrix(friends_index.size(),friends_index.size());
		for(String user : mutual_friends.keySet())
		{
			for(Friend f : mutual_friends.get(user))
			{
				int x = friends_index.get(user);
				int y = friends_index.get(f.getName());
				adjacency_matrix.set(x, y, 1);
			}
		}
		return adjacency_matrix;
	}
	
	//列印朋友烈表
	private static void write_friend_index(HashMap<String,Integer> friends_index,String path) throws IOException{

		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		for(Entry<String, Integer> index : friends_index.entrySet())
		{
			String line = "";
			line = index.getValue() + "," + index.getKey();
			writer.write(line);
			writer.newLine();
		}
		writer.close();
		
	}
	
	//列印粉絲頁(已合併到上面程式，無用)
	private static void write_friend_FP_index(HashMap<String,String> friends_FP_index,String path) throws IOException{

		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		for(Entry<String, String> index : friends_FP_index.entrySet())
		{
			String line = "";
			line = index.getValue() + "," + index.getKey();
			writer.write(line);
			writer.newLine();
		}
		writer.close();
		
	}
	
	//目前沒用到，已經合併到getFriendCheckins
	private static void write_friend_CK_index(HashMap<String,String> friends_CK_index,String path) throws IOException{

		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		for(Entry<String, String> index : friends_CK_index.entrySet())
		{
			String line = "";
			line = index.getValue() + "," + index.getKey();
			writer.write(line);
			writer.newLine();
		}
		writer.close();
		
	}
	
	//目前沒用到
	private static void write_adjacency_matrix(Matrix adjacency_matrix,String path) throws IOException {
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		for(int i = 0 ; i < adjacency_matrix.getRowDimension() ; i++)
		{
			String line = "";
			line = adjacency_matrix.get(i, 0)+"";
			for(int j = 1 ; j < adjacency_matrix.getColumnDimension() ; j++)
			{
				line += ","+adjacency_matrix.get(i, j);
			}
			writer.write(line);
			writer.newLine();
		}
		writer.close();
		
	}
	
	public static void main(String[] args) throws FacebookException, IOException, ParseException{

		Facebook facebook ;
		facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId("", "");
		facebook.setOAuthAccessToken(new AccessToken("CAACEdEose0cBAFVroL9Bux4KuH8n4hu7VWpRzYs868aVKxExdQBi6BAsZBRmDobtyJ0BxJMOs82RLy7PkJJ3btsu1riAeZBUzko0AFAQQbhKF2P6gA2460fl9eyr7UuKUcyvPewfTByxuZBVJmZBkDwd5idCgm4v3vFYztvbqGMJZBtuJM0LzAhipBqSXzsGxNuuhINZAeUQZDZD"));
		
		List<Friend> friends = getFriends(facebook);
		//HashMap<String,Integer> friends_index = getFriendIndex(friends);	//取得朋友的列表

		//HashMap<String,String> friends_FP_index = getFanPagesIndex(facebook,friends);	//取得朋友的粉絲團，已無用
		//getFanPagesIndex(facebook,friends);	//取得朋友的粉絲團,取代上面getFanPagesIndex()
		//HashMap<String,String> friends_CK_index = getCheckinIndex(facebook,friends);	//取得朋友按讚的地點，已無用
		//getFriendCheckins(facebook,friends);	//取得朋友的打卡資訊,取代上面getCheckinIndex()
		getFriendPosts(facebook,friends);	//取得朋友們Po文資訊
		//getMyPosts(facebook);	//取得自己Po文資訊
		//getFriendInformation(facebook,friends);	//取得盟友的其他資訊
		
		//write_friend_index(friends_index, "friend_index.csv");	//Friend寫入檔案
		//write_friend_FP_index(friends_FP_index, "friend_FP_index.csv");	//FanPage寫入檔案，已放入getFanPagesIndex()
		//write_friend_CK_index(friends_CK_index, "friend_CK_index.csv");	//Checkin寫入檔案

		
		System.out.println("finish");
		
	}



}
